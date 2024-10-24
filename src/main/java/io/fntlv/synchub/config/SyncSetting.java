package io.fntlv.synchub.config;

import br.com.finalcraft.evernifecore.config.Config;

public class SyncSetting {
    private static final String pathHead = "sync";
    private static String serverName;
    private static boolean isDebug;
    private static int maxSaveData;
    private static int delayedLoadTime;

    public static void init(Config config){
        serverName = config.getOrSetDefaultValue(
                pathHead + ".serverName",
                "spawn",
                "Server name"
        );

        isDebug = config.getOrSetDefaultValue(
                pathHead + ".isDebug",
                false,
                "is enable debug mode"
        );

        maxSaveData = config.getOrSetDefaultValue(
                pathHead + ".maxSaveData",
                10,
                "a player can save max data num"
        );

        delayedLoadTime = config.getOrSetDefaultValue(
                pathHead + ".delayedLoadTime",
                20,
                "Time for delayed loading of data in ticks, 1tick = 20s.\n" +
                        "Mainly to wait for the original data to be read"
        )
        ;
    }

    public static String getServerName() {
        return serverName;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static int getMaxSaveData() {
        return maxSaveData;
    }

    public static int getDelayedLoadTime() {
        return delayedLoadTime;
    }
}
