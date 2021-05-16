package com.asthereon.menus.Utils;

import net.minestom.server.data.SerializableDataImpl;
import net.minestom.server.utils.binary.BinaryReader;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Metadata is a collection of {@link net.minestom.server.data.DataType DataTypes} that can be serialized and
 *  deserialized.  Metadata has an optional default state that can be reverted to using {@link Metadata#revertToDefault(boolean)}.
 */
public class Metadata extends SerializableDataImpl {

    private byte[] defaultMetadata = null;

    public Metadata(@NotNull Metadata defaultMetadata) {
        super();
        this.defaultMetadata = defaultMetadata.getIndexedSerializedData();
        this.revertToDefault(false);
    }

    public Metadata() {
        super();
    }

    /**
     * Reverts the metadata to a pre-defined default, with an option to remove keys that do not exist in the default.
     * @param removeNonDefaultKeys whether keys in the metadata that are not in the default should be removed
     */
    public void revertToDefault(boolean removeNonDefaultKeys) {
        // IF non-default keys should be removed
        if (removeNonDefaultKeys) {
            // Clear all the existing data, since default keys will be set anyway
            this.data.clear();
            this.dataType.clear();
        }

        // Load all the existing default metadata values from the default
        this.readIndexedSerializedData(new BinaryReader(this.defaultMetadata));
    }

    @NotNull
    @Override
    public Metadata clone() {
        // Create a new Metadata object
        Metadata metadata = new Metadata();

        // Serialize this metadata so that it captures a deep copy of all values
        byte[] serializedData = this.getIndexedSerializedData();

        // Read the serialized data into the new metadata
        metadata.readIndexedSerializedData(new BinaryReader(serializedData));

        return metadata;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\nMetadata:");
        @NotNull Set<String> keys = this.getKeys();
        for (String key : keys) {
            Object o = this.get(key);
            String objectString = "NULL";
            if (o != null) {
                objectString = o.toString();
            }
            stringBuilder
                    .append("  \n")
                    .append(key)
                    .append(": ")
                    .append(objectString);
        }
        return stringBuilder.toString();
    }
}
