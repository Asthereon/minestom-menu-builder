package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.asthcore.StorageSystem.JsonFileStorage;
import com.asthereon.menus.Menu;
import com.asthereon.menus.MenuBuilder;
import com.asthereon.menus.MenuSchema;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.storage.StorageOptions;
import org.apache.commons.codec.binary.Base64;

public class Schema {

    public void open(Player player) {
        // Create a schema (spaces are removed and ignored, so they're allowed for easier readability)
        MenuSchema schema = new MenuSchema(InventoryType.CHEST_6_ROW)
                .mask("000 111 000")
                .mask("020 222 020")
                .mask("000 111 000")
                .mask("333 333 333")
                .mask("012 000 210")
                .mask("321 333 123");

        // Create a MenuBuilder with 6 rows and give it a title
        MenuBuilder menuBuilder = MenuBuilder.of(InventoryType.CHEST_6_ROW, "Schema Example")
                // Set the menu to be read only (unable to be modified with click events)
                .readOnly(true);

        // Add a button to the menu
        menuBuilder.button(
                // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
                menuBuilder.createMenuButton()
                        // Set the slots the button will go in by pulling the slots for character "0" in the schema
                        .slots(schema.getSlots("0"))
                        // Create the item stack to represent this button
                        .itemStack(ItemStack.builder(Material.COAL)
                                .amount(1)
                                .displayName(AsthCore.getComponent("Schema Button 0"))
                                .build())
                        // Bind a Menu Consumer to the click event of this button
                        .click((menu) -> {
                            AsthCore.sendMessage(player, "You clicked the Schema Button 0");
                        })
        );

        // Add a button to the menu
        menuBuilder.button(
                // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
                menuBuilder.createMenuButton()
                        // Set the slots the button will go in by pulling the slots for character "0" in the schema
                        .slots(schema.getSlots("1"))
                        // Create the item stack to represent this button
                        .itemStack(ItemStack.builder(Material.IRON_INGOT)
                                .amount(1)
                                .displayName(AsthCore.getComponent("Schema Button 1"))
                                .build())
                        // Bind a Menu Consumer to the click event of this button
                        .click((menu) -> {
                            AsthCore.sendMessage(player, "You clicked the Schema Button 1");
                        })
        );

        // Add a button to the menu
        menuBuilder.button(
                // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
                menuBuilder.createMenuButton()
                        // Set the slots the button will go in by pulling the slots for character "0" in the schema
                        .slots(schema.getSlots("2"))
                        // Create the item stack to represent this button
                        .itemStack(ItemStack.builder(Material.GOLD_INGOT)
                                .amount(1)
                                .displayName(AsthCore.getComponent("Schema Button 2"))
                                .build())
                        // Bind a Menu Consumer to the click event of this button
                        .click((menu) -> {
                            AsthCore.sendMessage(player, "You clicked the Schema Button 2");
                        })
        );

        // Add a button to the menu
        menuBuilder.button(
                // Creates a menu button using the menu's UUID behind the scenes to create a lazy binding for the Menu to pass to the click Consumer
                menuBuilder.createMenuButton()
                        // Set the slots the button will go in by pulling the slots for character "0" in the schema
                        .slots(schema.getSlots("3"))
                        // Create the item stack to represent this button
                        .itemStack(ItemStack.builder(Material.DIAMOND)
                                .amount(1)
                                .displayName(AsthCore.getComponent("Schema Button 3"))
                                .build())
                        // Bind a Menu Consumer to the click event of this button
                        .click((menu) -> {
                            AsthCore.sendMessage(player, "You clicked the Schema Button 3");
                        })
        );

        // Build the menu
        Menu menu = menuBuilder.build();

        // Open the menu to the player
        menu.open(player);
    }
}