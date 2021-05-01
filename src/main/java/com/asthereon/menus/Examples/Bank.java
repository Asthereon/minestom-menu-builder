package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.asthcore.StorageSystem.JsonFileStorage;
import com.asthereon.menus.Enums.CursorOverflowType;
import com.asthereon.menus.Menu.MenuButton;
import com.asthereon.menus.Menu.MenuButtonBuilder;
import com.asthereon.menus.Menu.*;
import com.asthereon.menus.Utils.ClickInfo;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Nullable;

public class Bank {

    private static final StorageLocation storageLocation = MinecraftServer.getStorageManager().getLocation("bank", new StorageOptions(), new JsonFileStorage());
    private static final int MINIMUM_TAB_SLOT = 0;
    private static final int MAXIMUM_TAB_SLOT = 8;

    public void open(Player player, @Nullable Data metadata) {
        // Create a schema
        MenuSchema schema = new MenuSchema(InventoryType.CHEST_6_ROW)
                .mask("RRRRRRRRR")
                .mask("000000000")
                .mask("000000000")
                .mask("000000000")
                .mask("000000000")
                .mask("PPPPPPPPP");

        // Create a MenuBuilder with 6 rows and give it a title
        MenuBuilder menuBuilder = MenuBuilder.of(InventoryType.CHEST_6_ROW, Component.text(player.getUsername() + "'s Bank (Tab " + (metadata == null ? "1" : metadata.getOrDefault("bankTab", 0) + 1) + ")"))
                // Set the top and bottom rows of the menu to be read only (unable to be modified with click events)
                .readOnlySlots(schema.getSlots("RP"))
                // Transfer any existing metadata from a menu refresh due to tab changes
                .metadata(metadata);

        // Create all 9 bank tabs
        for (int i = 0; i < 9; i++) {
            // Create and add the bank tab button to the menu
            menuBuilder.button(createBankTabButton(i));
        }

        // Create all 9 placeholders
        menuBuilder.placeholder(schema.getSlots('P'), BANK_PLACEHOLDER);

        // Build the menu
        Menu menu = menuBuilder.build();

        // IF this is a brand new menu (not a tab refresh)
        if (metadata == null) {
            // Reset the tab info back to the first tab
            menu.setMetadata("previousBankTab",-1,Integer.class);
            menu.setMetadata("bankTab",0,Integer.class);
        }

        // Bind the data saving code to the onSave() event
        menu.bindToSave(Bank::save);

        // Bind the data loading code to the onLoad() event
        menu.bindToLoad(Bank::load);

        // Set up how this menu handles cursor items that won't fit in the player's inventory on menu close
        menu.bindToCursorItemOverflow(Bank::cursorItemOverflow);

        // Open the menu to the player
        menu.open(player);
    }

    // Creates a bank tab button by using the BANK_TAB_BUTTON to apply click functionality, while retaining the ability
    //  to change the slot and item stack of each tab
    private MenuButton createBankTabButton(int tab) {
        return MenuButtonBuilder.from(BANK_TAB_BUTTON)
                // Set the slot the button will go in (using setSlot to ensure it overwrites previous slots)
                .setSlot(tab)
                // Set the amount of the item stack
                .amount(tab + 1)
                // Set the display name of the item stack
                .displayName(AsthCore.getComponent("<reset><yellow>Bank Tab " + (tab + 1)))
                // Build the menu button
                .build();
    }

    // A static MenuButtonBuilder with the click logic to use as a template for the bank tab buttons
    private static final MenuButtonBuilder BANK_TAB_BUTTON =
            // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
            MenuButton.builder()
                    // Create the item stack to represent this button
                    .itemStack(ItemStack.builder(Material.PAPER)
                            .lore(AsthCore.getComponent("<reset><green>Click to open this tab"))
                            .build())
                    // Bind a Menu Consumer to the click event of this button
                    .click(Bank::switchTab);

    // A static ItemStack to use for the bank placeholders
    private static final ItemStack BANK_PLACEHOLDER =
            ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE)
                    .displayName(AsthCore.getComponent("<reset>"))
                    .amount(1)
                    .build();

    // Click functionality for bank tabs
    private static void switchTab(Menu menu, ClickInfo clickInfo) {
        // Get this tab's index
        int bankTab = clickInfo.getItemStack().getAmount() - 1;

        // Only process click if it's a different tab
        if (bankTab != menu.getMetadata("bankTab",0)) {
            // Cache the previous tab for saving
            Integer previousTab = menu.getMetadata("bankTab", 0);
            menu.setMetadata("previousBankTab", previousTab, Integer.class);
            // Set the new bank tab to this tab
            menu.setMetadata("bankTab", bankTab, Integer.class);
            // Mark this as a bank tab swap to correctly save the bank tab data
            menu.setMetadata("bankTabSwap", true, Boolean.class);
            // Open the new bank menu with the existing metadata
            new Bank().open(clickInfo.getPlayer(), menu.getMetadata());
        }
    }

    // Save functionality
    private static void save(MenuView menuView, String serializedData) {
        // Tab swapping saves the previous tab, whereas menu closing normally will save the current bank tab
        Boolean isTabSwap = menuView.getMetadata("bankTabSwap", false);
        Integer tab;
        if (isTabSwap) {
            tab = menuView.getMetadata("previousBankTab", -1);
        } else {
            tab = menuView.getMetadata("bankTab", -1);
        }
        // IF the tab is in the correct range
        if (tab >= MINIMUM_TAB_SLOT && tab <= MAXIMUM_TAB_SLOT) {
            // Save the tab data to the storage location (this should ideally be cached in memory to avoid latency de-sync)
            storageLocation.set("tab" + tab + "-" + menuView.getPlayer().getUuid().toString(), Base64.decodeBase64(serializedData));

            // Clear the bank tab swapping to ensure that the menu closing normally will save correctly if the next event is a normal menu close
            menuView.setMetadata("bankTabSwap",null);
        }
    }

    // Load functionality
    private static void load(MenuView menuView) {
        // Get the bank tab that is currently selected
        Integer tab = menuView.getMetadata("bankTab",0);

        // IF the tab is in the correct range
        if (tab >= MINIMUM_TAB_SLOT && tab <= MAXIMUM_TAB_SLOT) {
            // Load the tab data from the storage location (could be stored somewhere other than memory, but will block so memory is best)
            String storageData = Base64.encodeBase64String(storageLocation.get("tab" + tab + "-" + menuView.getPlayer().getUuid().toString()));

            // Deserialize the tab data to populate the menu
            menuView.getMenu().getInventory().deserialize(storageData);
        }
    }

    // Cursor item handling functionality
    private static void cursorItemOverflow(MenuView menuView, ItemStack itemStack) {
        // IF the item stack can't be put into the bank storage
        if (!menuView.getMenu().getInventory().addItemStack(itemStack)) {
            // Default to dropping the item stack on the ground using the default cursor overflow drop type
            CursorOverflow.getCursorOverflowHandler(CursorOverflowType.DROP).accept(menuView.getPlayer(), itemStack);
        }
    }
}
