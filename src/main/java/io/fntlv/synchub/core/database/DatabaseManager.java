package io.fntlv.synchub.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.fntlv.synchub.SyncHub;
import io.fntlv.synchub.config.DatabaseSetting;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static HikariDataSource dataSource;
    private static boolean isConnection;

    public static void init() {
        try {
            dataSource = new HikariDataSource(createHikariConfig());
            isConnection = true;
            SyncHub.info("Database connection pool initialized successfully.");
        } catch (Exception e) {
            isConnection = false;
            SyncHub.warn("Failed to initialize database connection pool: " + e.getMessage());
        }
    }

    private static HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildJdbcUrl());
        config.setUsername(DatabaseSetting.getUsername());
        config.setPassword(DatabaseSetting.getPassword());
        config.setMaximumPoolSize(DatabaseSetting.getMaxPoolSize());
        config.setConnectionTimeout(DatabaseSetting.getConnectionTimeout());
        config.setIdleTimeout(DatabaseSetting.getIdleTimeout());

        configureMySqlProperties(config);
        return config;
    }

    private static String buildJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s",
                DatabaseSetting.getIp(),
                DatabaseSetting.getPort(),
                DatabaseSetting.getDatabase());
    }

    private static void configureMySqlProperties(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            SyncHub.debug("Database connection obtained successfully.");
            return connection;
        } catch (SQLException e) {
            SyncHub.warn("Failed to obtain database connection: " + e.getMessage());
            throw e;
        }
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            SyncHub.info("Database connection pool closed.");
        }
    }

    public static boolean isConnection() {
        return isConnection;
    }
}
