package com.asthereon.menus.Examples;

import com.asthereon.menus.Menu.Menu;
import com.asthereon.menus.Menu.MenuBuilder;

import com.asthereon.menus.Menu.MenuView;
import net.kyori.adventure.text.Component;
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
        Menu menu = MenuBuilder.of(InventoryType.CHEST_3_ROW, Component.text(player.getUsername() + "'s Ender Chest")).build();
        menu.bindToSave(EnderChest::save);
        menu.bindToLoad(EnderChest::load);
        menu.open(player);
    }

    private static void save(MenuView menuView, String serializedData) {
        storageLocation.set(menuView.getPlayer().getUuid().toString(), Base64.decodeBase64(serializedData));
    }

    private static void load(MenuView menuView) {
        String storageData = Base64.encodeBase64String(storageLocation.get(menuView.getPlayer().getUuid().toString()));
        menuView.getMenu().getInventory().deserialize(storageData);
    }

}
