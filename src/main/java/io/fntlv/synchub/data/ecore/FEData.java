package io.fntlv.synchub.data.ecore;

import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.finaleconomy.config.data.FEPlayerData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.fntlv.synchub.SyncHub;
import io.fntlv.synchub.api.data.ISerializableData;

import java.util.UUID;

public class FEData implements ISerializableData {
    private final Gson gson = new Gson();

    @Override
    public String serialize(UUID uuid) {
        FEPlayerData pdSection = PlayerController.getPDSection(uuid, FEPlayerData.class);
        return pdSection.getMoney()+"";
    }

    @Override
    public void deserialize(String data, UUID uuid) {
        try {
            FEPlayerData pdSection = PlayerController.getPDSection(uuid, FEPlayerData.class);
            pdSection.setMoney(Double.parseDouble(data));
            pdSection.savePDSection();
        } catch (JsonSyntaxException e) {
            SyncHub.warn("Failed to deserialize FinalEconomy data for UUID " + uuid + ": " + e.getMessage());
        }
    }

    @Override
    public String key() {
        return "FinalEconomy";
    }
}
