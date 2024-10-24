package io.fntlv.synchub.api.data;

import io.fntlv.synchub.SyncHub;
import io.fntlv.synchub.handlers.SyncHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSyncRegistry is responsible for registering serializable data for either mods or plugins.
 * It checks if the specified mod or plugin is loaded before allowing the registration of associated data.
 */
public class DataSyncRegistry {
    private final List<ISerializableData> serializableData; // List to hold all serializable data
    private String dataId; // Identifier for the mod or plugin
    private final Type type; // Type of the data (MOD_DATA or PLUGIN_DATA)

    /**
     * Constructor for DataSyncRegistry.
     *
     * @param type   The type of data being registered (MOD_DATA or PLUGIN_DATA).
     * @param dataId The identifier for the mod or plugin associated with this registry.
     */
    private DataSyncRegistry(Type type, String dataId) {
        serializableData = new ArrayList<>();
        this.type = type;
        this.dataId = dataId;
    }

    /**
     * Enum representing the types of data that can be registered.
     */
    public enum Type {
        MOD_DATA,
        PLUGIN_DATA
    }

    /**
     * Adds serializable data to the registry.
     *
     * @param iSerializableData The serializable data to add.
     * @return The current DataSyncRegistry instance for method chaining.
     */
    public DataSyncRegistry addSerializableData(ISerializableData iSerializableData) {
        this.serializableData.add(iSerializableData);
        return this;
    }

    /**
     * Creates a new instance of DataSyncRegistry.
     *
     * @param type   The type of data to be registered.
     * @param dataId The identifier for the mod or plugin.
     * @return A new instance of DataSyncRegistry.
     */
    public static DataSyncRegistry create(Type type, String dataId) {
        return new DataSyncRegistry(type, dataId);
    }

    /**
     * Registers the serializable data if the mod or plugin is loaded.
     * It checks the validity of the table names before registration.
     */
    public void register() {
        // Check if the type is MOD_DATA and the class is available
        if (type.equals(Type.MOD_DATA)) {
            try {
                Class.forName(dataId);
            } catch (ClassNotFoundException e) {
                // If class not found, registration is ignored
                return;
            }
        } else if (type.equals(Type.PLUGIN_DATA)) {
            // Check if the plugin is loaded
            Plugin plugin = Bukkit.getPluginManager().getPlugin(dataId);
            if (plugin == null) {
                return; // If the plugin is not loaded, registration is ignored
            }
        }

        // Register all serializable data
        for (ISerializableData serializableDatum : serializableData) {
            String key = serializableDatum.key();

            // Validate the table name before registration
            if (!isValidTableName(key)) {
                SyncHub.warn("Invalid table name: " + key + ". Registration skipped.");
                continue; // Skip registration if the table name is invalid
            }

            SyncHandler.getInstance().registerData(serializableDatum);
            SyncHub.info("Registered data: " + key);
        }
    }

    /**
     * Validates the table name to ensure it follows naming conventions.
     *
     * @param tableName The name of the table to validate.
     * @return True if the table name is valid, false otherwise.
     */
    private boolean isValidTableName(String tableName) {
        // Check if tableName is non-null and matches the pattern
        return tableName != null && tableName.matches("[a-zA-Z0-9_]+");
    }
}
