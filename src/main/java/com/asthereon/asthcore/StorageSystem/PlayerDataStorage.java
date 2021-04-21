package com.asthereon.asthcore.StorageSystem;

public class PlayerDataStorage extends JsonFileStorage {

    @Override
    public String getFolder() {
        return "playerdata";
    }

}