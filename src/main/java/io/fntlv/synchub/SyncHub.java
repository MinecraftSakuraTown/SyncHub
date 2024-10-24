package io.fntlv.synchub;

import br.com.finalcraft.finaleconomy.FinalEconomy;
import io.fntlv.synchub.config.ConfigManager;
import io.fntlv.synchub.config.SyncSetting;
import io.fntlv.synchub.core.database.DatabaseManager;
import io.fntlv.synchub.core.libs.SyncHubLib;
import io.fntlv.synchub.api.data.DataSyncRegistry;
import io.fntlv.synchub.data.ecore.FEData;
import io.fntlv.synchub.data.mod.ModData;
import io.fntlv.synchub.data.mod.ModDataAPIInvoker;
import io.fntlv.synchub.handlers.SyncHandler;
import io.fntlv.synchub.listener.PreventListener;
import io.fntlv.synchub.listener.SyncListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SyncHub extends JavaPlugin {
    private static SyncHub inst;

    public static void info(String msg){
        inst.getLogger().info("[info]"+msg.replace("&","§"));
    }

    public static void warn(String msg){
        inst.getLogger().warning("[warn]"+msg.replace("&","§"));
    }

    public static void debug(String msg){
        if (!SyncSetting.isDebug()){
            return;
        }
        inst.getLogger().warning("[debug]"+msg.replace("&","§"));
    }

    @Override
    public void onEnable() {
        inst = this;
        info("SyncHub Loading");
        SyncHubLib.init();
        ConfigManager.init(inst);
        DatabaseManager.init();
        this.registerData();
        SyncListener.register(inst);
        PreventListener.register(inst);
        info("SyncHub Loaded");
    }

    @Override
    public void onDisable() {
        saveAllPlayer();
        if (DatabaseManager.isConnection()){
            DatabaseManager.close();
        }
    }

    public static SyncHub getInst() {
        return inst;
    }

    // example
    public void registerData(){
        DataSyncRegistry.create(DataSyncRegistry.Type.MOD_DATA, ModDataAPIInvoker.getModDataApiClassName())
                .addSerializableData(new ModData())
                .register();
        DataSyncRegistry.create(DataSyncRegistry.Type.PLUGIN_DATA, FinalEconomy.instance.getName())
                .addSerializableData(new FEData())
                .register();
    }

    public void saveAllPlayer(){
        Bukkit.getOnlinePlayers().forEach(SyncHandler.getInstance()::saveData);
    }

    public static SyncHub getInstance(){
        return inst;
    }
}
