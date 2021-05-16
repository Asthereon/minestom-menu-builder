package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.asthcore.StorageSystem.JsonFileStorage;
import com.asthereon.menus.Enums.CursorOverflowType;
import com.asthereon.menus.Menu.*;
import com.asthereon.menus.Utils.ClickInfo;
import com.asthereon.menus.Utils.Metadata;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import org.apache.commons.codec.binary.Base64;

public class Bank extends Menu {

    private static final StorageLocation storageLocation = MinecraftServer.getStorageManager().getLocation("bank", new StorageOptions(), new JsonFileStorage());
    private static final int MINIMUM_TAB_SLOT = 0;
    private static final int MAXIMUM_TAB_SLOT = 8;
    private static final MenuSchema schema = new MenuSchema(InventoryType.CHEST_6_ROW).mask(
            "RRRRRRRRR",
            "000000000",
            "000000000",
            "000000000",
            "000000000",
            "PPPPPPPPP"
    );

    @Override
    protected MenuData buildMenu() {
        // Create a MenuBuilder with 6 rows and give it a title
        MenuBuilder menuBuilder = MenuBuilder.of(this, InventoryType.CHEST_6_ROW, Component.text("{{player_username}}'s Bank"))
                // Set the top and bottom rows of the menu to be read only (unable to be modified with click events)
                .readOnlySlots(schema.getSlots("RP"));

        // Create all 9 bank tabs
        for (int i = 0; i < 9; i++) {
            // Create and add the bank tab button to the menu
            menuBuilder.button(createBankTabButton(i));
        }

        // Create all 9 placeholders
        menuBuilder.placeholder(schema.getSlots('P'), BANK_PLACEHOLDER);

        // Bind the data saving code to the onSave() event
        menuBuilder.onSave(Bank::save);

        // Bind the data loading code to the onLoad() event
        menuBuilder.onLoad(Bank::load);

        // Set the default tab info to pass to the MenuViews on creation
        Metadata metadata = new Metadata();
        metadata.set("previousBankTab",-1,Integer.class);
        metadata.set("bankTab",0,Integer.class);
        menuBuilder.metadata(metadata);

        // Build the menu
        MenuData menuData = menuBuilder.build();

        // Set up how this menu handles cursor items that won't fit in the player's inventory on menu close
        menuData.bindToCursorItemOverflow(Bank::cursorItemOverflow);

        return menuData;
    }

    @Override
    public String getMenuID() {
        return "Bank";
    }

    // A static MenuButtonBuilder with the click logic to use as a template for the bank tab buttons
    private static final MenuButtonBuilder BANK_TAB_BUTTON =
            // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
            MenuButton.builder()
                    // Create the item stack to represent this button
                    .itemStack(ItemStack.builder(Material.PAPER)
                            .lore(AsthCore.getComponent("<reset><green>{{player_username}}! Click to open this tab"))
                            .build())
                    // Bind a Menu Consumer to the click event of this button
                    .click(Bank::switchTab);

    // Creates a bank tab button by using the BANK_TAB_BUTTON to apply click functionality, while retaining the ability
    //  to change the slot and item stack of each tab
    private static MenuButton createBankTabButton(int tab) {
        return MenuButtonBuilder.from(BANK_TAB_BUTTON)
                // Set the slot the button will go in (using setSlot to ensure it overwrites previous slots)
                .setSlot(tab)
                // Set the amount of the item stack
                .amount(tab + 1)
                // Set the display name of the item stack
                .displayName(AsthCore.getComponent("<reset><yellow>{{player_username}}'s Bank Tab " + (tab + 1)))
                // Build the menu button
                .build();
    }

    // A static ItemStack to use for the bank placeholders
    private static final ItemStack BANK_PLACEHOLDER =
            ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE)
                    .displayName(AsthCore.getComponent("<reset>{{player_username}}"))
                    .amount(1)
                    .build();

    // Click functionality for bank tabs
    private static void switchTab(MenuData menuData, ClickInfo clickInfo) {
        MenuView menuView = menuData.getMenuView(clickInfo.getPlayer());

        if (menuView != null) {
            // Get this tab's index
            int bankTab = clickInfo.getItemStack().getAmount() - 1;

            // Only process click if it's a different tab
            if (bankTab != menuView.getMetadata("bankTab", 0)) {
                // Cache the previous tab for saving
                Integer previousTab = menuView.getMetadata("bankTab", 0);
                menuView.setMetadata("previousBankTab", previousTab, Integer.class);
                // Set the new bank tab to this tab
                menuView.setMetadata("bankTab", bankTab, Integer.class);
                // Mark this as a bank tab swap to correctly save the bank tab data
                menuView.setMetadata("bankTabSwap", true, Boolean.class);
                // Open the new bank menu with the existing metadata
                MenuManager.open(menuData.getMenuID(), clickInfo.getPlayer(), menuView.getMetadata());
            }
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
            menuView.getInventory().deserialize(storageData);
        }
    }

    // Cursor item handling functionality
    private static void cursorItemOverflow(MenuView menuView, ItemStack itemStack) {
        // IF the item stack can't be put into the bank storage
        if (!menuView.getInventory().addItemStack(itemStack)) {
            // Default to dropping the item stack on the ground using the default cursor overflow drop type
            CursorOverflow.getCursorOverflowHandler(CursorOverflowType.DROP).accept(menuView.getPlayer(), itemStack);
        }
    }
}
