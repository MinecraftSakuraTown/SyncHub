package io.fntlv.synchub.api.events;

import br.com.finalcraft.evernifecore.api.events.base.ECPlayerDataEvent;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;

/**
 * This event is triggered when all data synchronization for a player is complete.
 * It is recommended for plugins that utilize synchronization features to use this event
 * instead of PlayerLoginEvent, as it ensures that all player data is fully synchronized
 * before further actions are taken.
 */
public class SyncCompleteEvent extends ECPlayerDataEvent {

    /**
     * Constructor for SyncCompleteEvent.
     *
     * @param playerData The PlayerData associated with the player.
     */
    public SyncCompleteEvent(PlayerData playerData) {
        super(playerData);
    }
}
