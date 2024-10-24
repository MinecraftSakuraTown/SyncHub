package io.fntlv.synchub.data.mod;

import io.fntlv.synchub.api.data.ISerializableData;

import java.util.UUID;

/**
 * need mod ModDataSerializer.
 * it serialize mod data, such as thaumcraft、baubles.
 */
public class ModData implements ISerializableData {

    @Override
    public String serialize(UUID uuid) {
        return ModDataAPIInvoker.getPlayerModData(uuid).orElse("");
    }

    @Override
    public void deserialize(String data, UUID uuid) {
        ModDataAPIInvoker.loadPlayerModData(uuid, data);
    }

    @Override
    public String key() {
        return "ModData";
    }
}
