package com.asthereon.menus.Utils;

import net.minestom.server.data.Data;
import net.minestom.server.data.DataImpl;

/**
 * A simple base class that handles metadata instantiation and pass-through methods.
 */
public class MetadataContainer {

    protected Data metadata = new DataImpl();

    public boolean hasMetadata() {
        return metadata != null && !metadata.isEmpty();
    }

    public Data getMetadata() {
        return metadata;
    }

    public <T> T getMetadata(String key, T defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }

    public <T> void setMetadata(String key, T value, Class<T> type) { metadata.set(key, value, type); }

    public <T> void setMetadata(String key, T value) { metadata.set(key, value); }

}
