package io.fntlv.synchub.api.events;

import br.com.finalcraft.evernifecore.api.events.base.ECPlayerDataEvent;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;

/**
 * This event is triggered when a specific data synchronization for a player occurs.
 * It includes the key of the synchronized data and whether the synchronization was successful.
 */
public class SyncDataEvent extends ECPlayerDataEvent {

    private final String key;
    private final boolean success;

    /**
     * Constructor for SyncDataEvent.
     *
     * @param playerData The PlayerData associated with the player.
     * @param key        The key of the synchronized data.
     * @param success    Indicates if the synchronization was successful.
     */
    public SyncDataEvent(PlayerData playerData, String key, boolean success) {
        super(playerData);
        this.key = key;
        this.success = success;
    }

    /**
     * Gets the key of the synchronized data.
     *
     * @return The key of the synchronized data.
     */
    public String getKey() {
        return key;
    }

    /**
     * Checks if the synchronization was successful.
     *
     * @return True if successful, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }
}
