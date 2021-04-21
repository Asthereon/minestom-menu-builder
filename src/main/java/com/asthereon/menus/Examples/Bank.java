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
    private static final int MINIMUM_TAB_SLOT = 0;
    private static final int MAXIMUM_TAB_SLOT = 8;

    public void open(Player player) {
        open(player, null);
    }

    public void open(Player player, Data metadata) {
        // Create a MenuBuilder with 6 rows and give it a title
        MenuBuilder menuBuilder = MenuBuilder.of(InventoryType.CHEST_6_ROW, player.getUsername() + "'s Bank")
                // Set the top row of the menu to be read only (unable to be modified with click events)
                .readOnlyRange(0,8)
                // Transfer any existing metadata from a menu refresh due to tab changes
                .metadata(metadata);

        // Create all 9 bank tabs
        for (int i = 0; i < 9; i++) {
            // Final variable required for the lambdas for the click method
            final int tab = i;

            // Add a button to the menu
            menuBuilder.button(
                    // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
                    menuBuilder.createMenuButton()
                            // Set the slot the button will go in
                            .slot(tab)
                            // Create the item stack to represent this button
                            .itemStack(ItemStack.builder(Material.PAPER)
                                    .amount(tab + 1)
                                    .displayName(AsthCore.getComponent("<reset><yellow>Bank Tab " + (tab + 1)))
                                    .build())
                            // Bind a Menu Consumer to the click event of this button
                            .click((menu) -> {
                                // Cache the previous tab for saving
                                Integer previousTab = menu.getMetadata( "bankTab",0);
                                menu.setMetadata("previousBankTab",previousTab,Integer.class);
                                // Set the new bank tab to this tab
                                menu.setMetadata("bankTab",tab,Integer.class);
                                // Mark this as a bank tab swap to correctly save the bank tab data
                                menu.setMetadata("bankTabSwap",true,Boolean.class);
                                // Open the new bank menu with the existing metadata
                                new Bank().open(player, menu.getMetadata());
                            })
            );
        }

        // Build the menu
        Menu menu = menuBuilder.build();

        // IF this is a brand new menu (not a tab refresh)
        if (metadata == null) {
            // Reset the tab info back to the first tab
            menu.setMetadata("previousBankTab",-1,Integer.class);
            menu.setMetadata("bankTab",0,Integer.class);
        }

        // Bind the data saving code to the onSave() event
        menu.bindToSave((serializedData) -> {
            // Tab swapping saves the previous tab, whereas menu closing normally will save the current bank tab
            Boolean isTabSwap = menu.getMetadata("bankTabSwap", false);
            Integer tab;
            if (isTabSwap) {
                tab = menu.getMetadata("previousBankTab", -1);
            } else {
                tab = menu.getMetadata("bankTab", -1);
            }
            // IF the tab is in the correct range
            if (tab >= MINIMUM_TAB_SLOT && tab <= MAXIMUM_TAB_SLOT) {
                // Save the tab data to the storage location (this should ideally be cached in memory to avoid latency desync)
                storageLocation.set("tab" + tab + "-" + player.getUuid().toString(), Base64.decodeBase64(serializedData));

                // Clear the bank tab swapping to ensure that the menu closing normally will save correctly if the next event is a normal menu close
                menu.setMetadata("bankTabSwap",null);
            }
        });

        // Bind the data loading code to the onLoad() event
        menu.bindToLoad((player1) -> {
            // Get the bank tab that is currently selected
            Integer tab = menu.getMetadata("bankTab",0);

            // IF the tab is in the correct range
            if (tab >= MINIMUM_TAB_SLOT && tab <= MAXIMUM_TAB_SLOT) {
                // Load the tab data from the storage location (could be stored somewhere other than memory, but will block so memory is best)
                String storageData = Base64.encodeBase64String(storageLocation.get("tab" + tab + "-" + player.getUuid().toString()));

                // Deserialize the tab data to populate the menu
                menu.getInventory().deserialize(storageData);
            }
        });

        // Open the menu to the player
        menu.open(player);
    }
}
