package io.fntlv.synchub.handlers;

import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;
import br.com.finalcraft.evernifecore.util.FCBukkitUtil;
import io.fntlv.synchub.SyncHub;
import io.fntlv.synchub.api.events.SyncCompleteEvent;
import io.fntlv.synchub.api.events.SyncDataEvent;
import io.fntlv.synchub.config.SyncSetting;
import io.fntlv.synchub.api.data.ISerializableData;
import io.fntlv.synchub.data.SyncPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SyncHandler {

    private static SyncHandler instance;
    private final Map<String, ISerializableData> serializableData = new HashMap<>();

    private SyncHandler() {
    }

    public static synchronized SyncHandler getInstance() {
        if (instance == null) {
            instance = new SyncHandler();
            DatabaseHandler.getInstance().createPlayerTable();
        }
        return instance;
    }

    public void registerData(ISerializableData data) {
        this.serializableData.put(data.key(), data);
        DatabaseHandler.getInstance().createDataTable(data.key());
    }

    public Optional<SyncPlayer> getSyncPlayer(Player player) {
        return DatabaseHandler.getInstance().getPlayer(player.getUniqueId().toString());
    }

    public LoginResult loginAndSyncData(Player player) {
        Optional<SyncPlayer> syncPlayerOpt = getSyncPlayer(player);
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        if (syncPlayerOpt.isPresent()) {
            SyncPlayer syncPlayer = syncPlayerOpt.get();
            return handleExistingPlayer(syncPlayer);
        } else {
            databaseHandler.updatePlayer(SyncPlayer.of(player));
            return LoginResult.ALLOW;
        }
    }

    private LoginResult handleExistingPlayer(SyncPlayer syncPlayer) {
        SyncPlayer.Status status = syncPlayer.getStatus();
        switch (status) {
            case saving:
                return LoginResult.DENY_SAVING;
            case save_failed:
                return LoginResult.DENY_SAVE_FAIL;
            case pending_save:
                if (!SyncSetting.getServerName().equals(syncPlayer.getServer())) {
                    return LoginResult.DENY_NOT_SAVE_IN_SERVER;
                }
            default:
                loadData(syncPlayer);
                return LoginResult.ALLOW;
        }
    }

    private void loadData(SyncPlayer player) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        UUID uuid = player.getUuid();

        new BukkitRunnable() {
            @Override
            public void run() {

                // player is not online, wo not need to load data
                if (!player.isOnline()){
                    return;
                }

                player.setServer(SyncSetting.getServerName());
                player.setStatus(SyncPlayer.Status.loading);
                databaseHandler.updatePlayer(player);
                PlayerData playerData = player.getPlayerData();

                serializableData.forEach((key, value) -> {
                    databaseHandler.getLatestSyncData(key, uuid.toString()).ifPresent(syncData -> {
                        try {
                            value.deserialize(syncData.getSerializedData(), uuid);
                            Bukkit.getPluginManager().callEvent(new SyncDataEvent(playerData,key,true));
                        }catch (Exception e){
                            Bukkit.getPluginManager().callEvent(new SyncDataEvent(playerData,key,false));
                            SyncHub.warn("Failed to deserialize data for key: " + key + ", UUID: " + uuid + ". Exception: " + e.getMessage());
                        }
                    });
                });
                player.setStatus(SyncPlayer.Status.pending_save);
                databaseHandler.updatePlayer(player);

                // sync complete
                Bukkit.getPluginManager().callEvent(new SyncCompleteEvent(playerData));
            }
        }.runTaskLater(SyncHub.getInstance(), SyncSetting.getDelayedLoadTime());
    }

    public boolean isPlayerSync(Player player){
        // not handler faker player
        if (FCBukkitUtil.isFakePlayer(player)){
            return true;
        }
        Optional<SyncPlayer> cache = SyncPlayer.getCache(player);
        if (cache.isPresent()){
            SyncPlayer syncPlayer = cache.get();
            if (syncPlayer.getStatus().equals(SyncPlayer.Status.pending_save)){
                return true;
            }
            return false;
        }
        return false;
    }

    public void saveData(Player player) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        UUID uuid = player.getUniqueId();
        String displayName = player.getDisplayName();

        Optional<SyncPlayer> syncPlayerOpt = getSyncPlayer(player);

        if (syncPlayerOpt.isPresent()) {
            SyncPlayer syncPlayer = syncPlayerOpt.get();
            SyncPlayer.Status status = syncPlayer.getStatus();
            if (status != SyncPlayer.Status.pending_save){
                return;
            }
            if (!syncPlayer.getServer().equals(SyncSetting.getServerName())){
                return;
            }

            syncPlayer.setStatus(SyncPlayer.Status.saving);
            databaseHandler.updatePlayer(syncPlayer);

            serializableData.forEach((key, value) -> {
                String serialize;
                try {
                    serialize = value.serialize(uuid);
                } catch (Exception e) {
                    SyncHub.warn("Failed to serialize data for UUID: " + uuid + ", Exception: " + e.getMessage());
                    syncPlayer.setStatus(SyncPlayer.Status.save_failed);
                    databaseHandler.updatePlayer(syncPlayer);
                    return;
                }
                databaseHandler.insertData(
                        key,
                        uuid.toString(),
                        displayName,
                        serialize,
                        SyncSetting.getServerName(),
                        SyncSetting.getMaxSaveData()
                );
            });

            syncPlayer.setStatus(SyncPlayer.Status.saved);
            databaseHandler.updatePlayer(syncPlayer);
        }
    }

    public enum LoginResult {
        DENY_SAVING,
        DENY_SAVE_FAIL,
        DENY_NOT_SAVE_IN_SERVER,
        ALLOW
    }
}