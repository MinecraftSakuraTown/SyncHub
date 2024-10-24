package io.fntlv.synchub.api.data;

import java.util.UUID;

/**
 * ISerializableData is an interface that defines the methods required for
 * serializing and deserializing data associated with a specific UUID.
 * Implementing classes must provide functionality to convert data to and
 * from a string format, as well as provide a unique key for database storage.
 */
public interface ISerializableData {

    /**
     * Serializes the data for the given UUID to a string.
     *
     * @param uuid the unique identifier for the data
     * @return the serialized data as a string
     */
    String serialize(UUID uuid);

    /**
     * Deserializes the given string data for the specified UUID.
     *
     * @param data the serialized data as a string
     * @param uuid the unique identifier for the data
     */
    void deserialize(String data, UUID uuid);

    /**
     * Returns the key for the data, which also serves as the database table name.
     * The key must match the pattern "[a-zA-Z0-9_]+" to ensure valid table names.
     *
     * @return the key for the data
     */
    String key();
}
