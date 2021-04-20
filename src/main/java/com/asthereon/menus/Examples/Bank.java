package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.asthcore.StorageSystem.JsonFileStorage;
import com.asthereon.menus.Buttons.MenuButton;
import com.asthereon.menus.Menu;
import com.asthereon.menus.MenuBuilder;
import com.asthereon.menus.MenuManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.data.DataImpl;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import org.apache.commons.codec.binary.Base64;

public class Bank {

    private static final StorageLocation storageLocation = MinecraftServer.getStorageManager().getLocation("bank", new StorageOptions(), new JsonFileStorage());

    public void open(Player player) {
        open(player, false);
    }

    public void open(Player player, Boolean isRefresh) {
        isRefresh = isRefresh != null ? isRefresh : false;
        if (!isRefresh) {
            Data data = getPlayerData(player);
            data.set("previousBankTab",-1,Integer.class);
            data.set("bankTab",0,Integer.class);
        }
        MenuBuilder menuBuilder = MenuBuilder.of(InventoryType.CHEST_6_ROW, player.getUsername() + "'s Bank")
                .readOnlyRange(0,8);
        for (int i = 0; i < 9; i++) {
            final int tab = i;
            menuBuilder.button(MenuButton
                    .slot(tab)
                    .itemStack(ItemStack.builder(Material.PAPER)
                            .amount(tab + 1)
                            .displayName(AsthCore.getComponent("<reset><yellow>Bank Tab " + (tab + 1)))
                            .build())
                    .click(() -> {
                        Data data = getPlayerData(player);
                        Integer previousTab = data.getOrDefault( "bankTab",0);
                        data.set("previousBankTab",previousTab,Integer.class);
                        data.set("bankTab",tab,Integer.class);
                        data.set("bankTabSwap",true,Boolean.class);
                        player.setData(data);
                        new Bank().open(player, true);
                    })
            );
        }
        Menu menu = menuBuilder.build();
        menu.bindToSave((serializedData) -> {
            Boolean isTabSwap = getPlayerDataValue(player, "bankTabSwap", false);
            Integer tab;
            if (isTabSwap) {
                tab = getPlayerDataValue(player, "previousBankTab", -1);
            } else {
                tab = getPlayerDataValue(player, "bankTab", -1);
            }
            if (tab != -1) {
                storageLocation.set("tab" + tab + "-" + player.getUuid().toString(), Base64.decodeBase64(serializedData));
                AsthCore.console("Saved Tab " + tab + " " + isTabSwap);
                getPlayerData(player).set("bankTabSwap",null);
            }
        });
        menu.bindToLoad((player1) -> {
            Integer tab = getPlayerDataValue(player1,"bankTab",0);
            String storageData = Base64.encodeBase64String(storageLocation.get("tab" + tab + "-" + player.getUuid().toString()));
            menu.getInventory().deserialize(storageData);
            AsthCore.console("Loaded Tab " + tab);
        });
        menu.open(player);
    }

    public static Data getPlayerData(Player player) {
        Data data = player.getData();
        if (data == null) {
            data = new DataImpl();
        }
        return data;
    }

    public static <T> T getPlayerDataValue(Player player, String key, T defaultValue) {
        Data data = player.getData();
        if (data == null) {
            data = new DataImpl();
        }
        return data.getOrDefault(key, defaultValue);
    }

}
