package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Menu.Menu;
import com.asthereon.menus.Menu.MenuBuilder;
import com.asthereon.menus.Menu.MenuData;
import com.asthereon.menus.Menu.MenuView;
import net.minestom.server.MinecraftServer;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import net.minestom.server.storage.systems.FileStorageSystem;
import org.apache.commons.codec.binary.Base64;

public class EnderChest extends Menu {

    private static final StorageLocation storageLocation = MinecraftServer.getStorageManager().getLocation("enderChest", new StorageOptions(), new FileStorageSystem());

    private static void save(MenuView menuView, String serializedData) {
        storageLocation.set(menuView.getPlayer().getUuid().toString(), Base64.decodeBase64(serializedData));
    }

    private static void load(MenuView menuView) {
        String storageData = Base64.encodeBase64String(storageLocation.get(menuView.getPlayer().getUuid().toString()));
        menuView.getInventory().deserialize(storageData);
    }

    @Override
    protected MenuData buildMenu() {
        MenuData menuData = MenuBuilder.of(this, InventoryType.CHEST_3_ROW, AsthCore.getComponent("{{player_username}}'s Ender Chest")).build();
        menuData.bindToSave(EnderChest::save);
        menuData.bindToLoad(EnderChest::load);
        return menuData;
    }

    @Override
    public String getMenuID() {
        return "EnderChest";
    }
}
