package io.fntlv.synchub.config;

import br.com.finalcraft.evernifecore.config.Config;

public class DatabaseSetting {
    private static final String pathHead = "mysql";

    private static String ip;
    private static int port;
    private static String database;
    private static String username;
    private static String password;

    private static int maxPoolSize;
    private static int connectionTimeout;
    private static int idleTimeout;

    public static void init(Config config) {
        ip = config.getOrSetDefaultValue(
                pathHead + ".ip",
                "localhost",
                "Database host address"
        );
        port = config.getOrSetDefaultValue(
                pathHead + ".port",
                3306,
                "Database port"
        );
        database = config.getOrSetDefaultValue(
                pathHead + ".database",
                "synchub",
                "Database name"
        );
        username = config.getOrSetDefaultValue(
                pathHead + ".username",
                "root",
                "Database username"
        );
        password = config.getOrSetDefaultValue(
                pathHead + ".password",
                "123456abc",
                "Database password"
        );
        maxPoolSize = config.getOrSetDefaultValue(
                pathHead + ".maxPoolSize",
                10,
                "Maximum number of connections in the pool"
        );
        connectionTimeout = config.getOrSetDefaultValue(
                pathHead + ".connectionTimeout",
                30000,
                "Connection timeout in milliseconds"
        );
        idleTimeout = config.getOrSetDefaultValue(
                pathHead + ".idleTimeout",
                600000,
                "Idle timeout in milliseconds"
        );
    }

    // Getters for the configuration values
    public static String getIp() {
        return ip;
    }

    public static int getPort() {
        return port;
    }

    public static String getDatabase() {
        return database;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static int getMaxPoolSize() {
        return maxPoolSize;
    }

    public static int getConnectionTimeout() {
        return connectionTimeout;
    }

    public static int getIdleTimeout() {
        return idleTimeout;
    }
}
