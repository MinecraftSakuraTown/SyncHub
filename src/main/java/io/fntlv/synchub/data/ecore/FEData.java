package io.fntlv.synchub.data.ecore;

import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.finaleconomy.config.data.FEPlayerData;
import io.fntlv.synchub.api.data.ISerializableData;

import java.util.UUID;

public class FEData implements ISerializableData {

    @Override
    public String serialize(UUID uuid) {
        FEPlayerData pdSection = PlayerController.getPDSection(uuid, FEPlayerData.class);
        return pdSection.getMoney()+"";
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
