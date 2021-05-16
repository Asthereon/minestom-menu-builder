package com.asthereon.menus.Utils;

/**
 * A simple base class that handles metadata instantiation and pass-through methods.
 */
public class MetadataContainer {

    protected Metadata metadata = new Metadata();

    public boolean hasMetadata() {
        return metadata != null && !metadata.isEmpty();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    public <T> T getMetadata(String key, T defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }

    public <T> void setMetadata(String key, T value, Class<T> type) { metadata.set(key, value, type); }

    public <T> void setMetadata(String key, T value) { metadata.set(key, value); }

}
