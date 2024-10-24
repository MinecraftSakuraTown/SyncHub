package io.fntlv.synchub.data;

import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;
import io.fntlv.synchub.config.SyncSetting;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SyncPlayer {
    public static Map<UUID, SyncPlayer> syncPlayers = new HashMap<>();

    private UUID uuid;
    private String username;
    private String server;
    private String lastLoginDate;
    private Status status;

    private SyncPlayer(UUID uuid, String username, String server, Status status, String lastLoginDate) {
        this.uuid = uuid;
        this.username = username;
        this.server = server;
        this.status = status != null ? status : Status.pending_save; // 默认状态
        this.lastLoginDate = lastLoginDate;
    }

    // Update SyncPlayer Cache
    public static SyncPlayer of(UUID uuid, String username, String server, Status status, String lastLoginDate) {
        if (syncPlayers.containsKey(uuid)) {
            SyncPlayer syncPlayer = syncPlayers.get(uuid);
            syncPlayer.setServer(server);
            syncPlayer.setStatus(status);
            syncPlayer.setLastLoginDate(lastLoginDate);
            return syncPlayer;
        }
        SyncPlayer syncPlayer = new SyncPlayer(uuid, username, server, status, lastLoginDate);
        syncPlayers.put(uuid, syncPlayer);
        return syncPlayer;
    }

    // Create a default SyncPlayer if the database is not available
    public static SyncPlayer of(Player player) {
        UUID uniqueId = player.getUniqueId();
        String name = player.getName();
        return of(uniqueId, name, SyncSetting.getServerName(), Status.pending_save, null);
    }

    // Getting the cache to avoid reading from the database every time
    public static Optional<SyncPlayer> getCache(Player player){
        UUID uniqueId = player.getUniqueId();
        if (syncPlayers.containsKey(uniqueId)){
            return Optional.of(syncPlayers.get(uniqueId));
        }
        return Optional.empty();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public String getServer() {
        return server;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public enum Status {
        pending_save,
        saving,         // 新增状态
        saved,
        save_failed,
        loading
    }

    public boolean isOnline(){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.uuid);
        return offlinePlayer.isOnline();
    }

    public PlayerData getPlayerData(){
        return PlayerController.getPlayerData(this.uuid);
    }
}
