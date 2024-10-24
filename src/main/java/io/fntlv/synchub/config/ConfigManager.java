package io.fntlv.synchub.config;

import br.com.finalcraft.evernifecore.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private static Config mainConfig;

    public static void init(JavaPlugin plugin){
        mainConfig = new Config(plugin, "config.yml");
        DatabaseSetting.init(mainConfig);
        SyncSetting.init(mainConfig);
        mainConfig.save();
    }

    public static Config getMainConfig() {
        return mainConfig;
    }
}
