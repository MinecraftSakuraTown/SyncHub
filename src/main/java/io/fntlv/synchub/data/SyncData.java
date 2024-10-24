package io.fntlv.synchub.data;

public class SyncData {
    private String uuid;
    private String username;
    private String serializedData;
    private String server;
    private String saveDate;

    public SyncData(String uuid, String username, String serializedData, String server, String saveDate) {
        this.uuid = uuid;
        this.username = username;
        this.serializedData = serializedData;
        this.server = server;
        this.saveDate = saveDate;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public String getServer() {
        return server;
    }

    public String getSaveDate() {
        return saveDate;
    }
}
