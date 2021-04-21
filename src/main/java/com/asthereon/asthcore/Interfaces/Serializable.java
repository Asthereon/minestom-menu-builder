package com.asthereon.asthcore.Interfaces;

/**
 * Interface for a standardized serialization for objects to strings for easy storage
 */
public interface Serializable {

    String serialize();

    void deserialize(String data);

}
