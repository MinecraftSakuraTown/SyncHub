package io.fntlv.synchub.handlers;

import io.fntlv.synchub.SyncHub;
import io.fntlv.synchub.core.database.DatabaseManager;
import io.fntlv.synchub.data.SyncData;
import io.fntlv.synchub.data.SyncPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DatabaseHandler {

    private static DatabaseHandler databaseHandler;

    public static synchronized DatabaseHandler getInstance() {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler();
        }
        return databaseHandler;
    }

    public void createPlayerTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Players (" +
                "uuid CHAR(36) NOT NULL PRIMARY KEY, " +
                "username VARCHAR(16) NOT NULL, " +
                "register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "status ENUM('pending_save', 'saving', 'saved', 'save_failed', 'loading') NOT NULL DEFAULT 'pending_save', " +  // 更新此行
                "server VARCHAR(255) NOT NULL, " +
                "INDEX (uuid) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        executeUpdate(createTableSQL, "Players table created successfully.", "Error creating Players table");
    }


    public void updatePlayer(SyncPlayer syncPlayer) {
        String uuid = syncPlayer.getUuid().toString();
        String username = syncPlayer.getUsername();
        String server = syncPlayer.getServer();
        String status = syncPlayer.getStatus().name();

        String upsertSQL = "INSERT INTO Players (uuid, username, server, status) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE username = ?, server = ?, status = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(upsertSQL)) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, server);
            preparedStatement.setString(4, status);
            preparedStatement.setString(5, username);
            preparedStatement.setString(6, server);
            preparedStatement.setString(7, status);
            preparedStatement.executeUpdate();
            SyncHub.debug("Player updated/inserted successfully: " + uuid);
        } catch (SQLException e) {
            SyncHub.warn("Error updating/inserting player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<SyncPlayer> getPlayer(String uuid) {
        String querySQL = "SELECT * FROM Players WHERE uuid = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String status = resultSet.getString("status");
                String server = resultSet.getString("server");
                String lastLoginDate = resultSet.getString("last_login");

                SyncPlayer syncPlayer = SyncPlayer.of(UUID.fromString(uuid), username, server, SyncPlayer.Status.valueOf(status), lastLoginDate);
                SyncHub.debug("Player found: UUID = " + uuid + ", Username = " + username + ", Status = " + syncPlayer.getStatus());
                return Optional.of(syncPlayer);
            } else {
                SyncHub.debug("Player not found: UUID = " + uuid);
                return Optional.empty();
            }
        } catch (SQLException e) {
            SyncHub.warn("Error retrieving player: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void deletePlayer(String uuid) {
        String deleteSQL = "DELETE FROM Players WHERE uuid = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, uuid);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                SyncHub.debug("Player deleted successfully: " + uuid);
            } else {
                SyncHub.debug("Player not found for deletion: " + uuid);
            }
        } catch (SQLException e) {
            SyncHub.warn("Error deleting player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeUpdate(String sql, String successMessage, String errorMessage) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            SyncHub.debug(successMessage);
        } catch (SQLException e) {
            SyncHub.warn(errorMessage + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createDataTable(String tableName) {
        String createTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "uuid CHAR(36) NOT NULL, " +
                        "username VARCHAR(16) NOT NULL, " +
                        "save_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "serialized_data TEXT NOT NULL, " +
                        "server VARCHAR(255) NOT NULL, " +
                        "PRIMARY KEY (uuid, save_date), " +
                        "INDEX (uuid), " +
                        "INDEX idx_uuid_save (uuid, save_date) " +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
                tableName
        );

        executeUpdate(createTableSQL, tableName + " table created successfully.", "Error creating " + tableName + " table.");
    }

    public void insertData(
            String tableName,
            String uuid,
            String username,
            String serializedData,
            String server,
            int maxEntries
    ) {
        String countSQL = String.format("SELECT COUNT(*) FROM %s WHERE uuid = ?", tableName);
        int currentCount = 0;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement countStatement = connection.prepareStatement(countSQL)) {
            countStatement.setString(1, uuid);
            ResultSet countResult = countStatement.executeQuery();

            if (countResult.next()) {
                currentCount = countResult.getInt(1);
            }
        } catch (SQLException e) {
            SyncHub.warn("Error counting entries in table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (currentCount >= maxEntries) {
            String deleteSQL = String.format("DELETE FROM %s WHERE uuid = ? ORDER BY save_date ASC LIMIT 1", tableName);
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL)) {
                deleteStatement.setString(1, uuid);
                deleteStatement.executeUpdate();
                SyncHub.debug("Old data deleted from table: " + tableName);
            } catch (SQLException e) {
                SyncHub.warn("Error deleting old data from table " + tableName + ": " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        String insertSQL = String.format(
                "INSERT INTO %s (uuid, username, serialized_data, server, save_date) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)",
                tableName
        );

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, serializedData);
            preparedStatement.setString(4, server);
            preparedStatement.executeUpdate();
            SyncHub.debug("Data inserted successfully into table: " + tableName);
        } catch (SQLException e) {
            SyncHub.warn("Error inserting data into table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<SyncData> getLatestSyncData(String tableName, String uuid) {
        String querySQL = String.format("SELECT * FROM %s WHERE uuid = ? ORDER BY save_date DESC LIMIT 1", tableName);

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String serializedData = resultSet.getString("serialized_data");
                String server = resultSet.getString("server");
                String saveDate = resultSet.getString("save_date");

                SyncData syncData = new SyncData(uuid, username, serializedData, server, saveDate);
                SyncHub.debug("Data retrieved successfully from table: " + tableName);
                return Optional.of(syncData);
            } else {
                SyncHub.debug("Data not found in table: " + tableName);
                return Optional.empty();
            }
        } catch (SQLException e) {
            SyncHub.warn("Error retrieving data from table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
