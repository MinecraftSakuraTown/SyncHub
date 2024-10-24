# SyncHub

This is a spigot plugin which could sync player data between servers by using MySQL database. It is designed for servers which have multiple servers and want to share player data between them.

## Prerequisites

Before using the SyncHub plugin, please ensure that the following requirements are met:

- **Bukkit 1.7.10 or higher**: Make sure your server is running Bukkit 1.7.10 or a later version to ensure compatibility with SyncHub's features.
- **EverNifeCore**: SyncHub relies on the EverNifeCore plugin, so ensure it is properly installed and configured.
- **MySQL 5.7**: Use MySQL version 5.7 for database connections and data storage.

Please ensure all prerequisites are met before starting the server to guarantee the plugin operates correctly.

## Usage Example

### Implementing the ISerializableData Interface

Below is an example implementation of the FEData class, which implements the ISerializableData interface for serializing and deserializing FinalEconomy data:

```java
public class FEData implements ISerializableData {

    @Override
    public String serialize(UUID uuid) {
        FEPlayerData pdSection = PlayerController.getPDSection(uuid, FEPlayerData.class);
        return String.valueOf(pdSection.getMoney());
    }

    @Override
    public void deserialize(String data, UUID uuid) {
        FEPlayerData pdSection = PlayerController.getPDSection(uuid, FEPlayerData.class);
        pdSection.setMoney(Double.parseDouble(data));
        pdSection.savePDSection();
    }

    @Override
    public String key() {
        return "FinalEconomy";
    }
}
```

### Registration Example

Here is an example code snippet demonstrating how to register serializable data:

```java
// Example registration method
public void registerData() {
    // Register MOD data
    DataSyncRegistry.create(DataSyncRegistry.Type.MOD_DATA, ModDataAPIInvoker.getModDataApiClassName())
            .addSerializableData(new ModData())
            .register();
    
    // Register plugin data
    DataSyncRegistry.create(DataSyncRegistry.Type.PLUGIN_DATA, FinalEconomy.instance.getName())
            .addSerializableData(new FEData())
            .register();
}
```