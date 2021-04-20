package com.asthereon.menus.Examples;

import com.asthereon.menus.Menu;
import com.asthereon.menus.MenuBuilder;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import net.minestom.server.storage.systems.FileStorageSystem;
import org.apache.commons.codec.binary.Base64;

public class EnderChest {

    private static final StorageLocation storageLocation = MinecraftServer.getStorageManager().getLocation("enderChest", new StorageOptions(), new FileStorageSystem());

    public void open(Player player) {
        Menu menu = MenuBuilder.of(InventoryType.CHEST_3_ROW, player.getUsername() + "'s Ender Chest").build();
        menu.bindToSave((serializedData) -> {
            storageLocation.set(player.getUuid().toString(), Base64.decodeBase64(serializedData));
        });
        menu.bindToLoad((player1) -> {
            String storageData = Base64.encodeBase64String(storageLocation.get(player.getUuid().toString()));
            menu.getInventory().deserialize(storageData);
        });
        menu.open(player);
    }

}
