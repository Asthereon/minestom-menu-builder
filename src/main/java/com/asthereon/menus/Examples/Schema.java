package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Enums.CursorOverflowType;
import com.asthereon.menus.Menu.MenuButton;
import com.asthereon.menus.Menu.MenuButtonBuilder;
import com.asthereon.menus.Menu.*;
import com.asthereon.menus.Utils.ClickInfo;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class Schema extends Menu {

    // Create a schema (spaces are removed and ignored, so they're allowed for easier readability)
    private static final MenuSchema schema = new MenuSchema(InventoryType.CHEST_6_ROW)
            .mask("000 111 000")
            .mask("020 222 020")
            .mask("000 111 000")
            .mask("333 333 333")
            .mask("012 000 210")
            .mask("321 333 123");

    @Override
    protected MenuData buildMenu() {
        // Create a MenuBuilder with 6 rows and give it a title
        MenuBuilder menuBuilder = MenuBuilder.of(this, InventoryType.CHEST_6_ROW, Component.text("Schema Example"))
                // Set the menu to be read only (unable to be modified with click events)
                .readOnly(true);

        // Add the button to the menu
        menuBuilder.button(createSchemaButton(0));
        menuBuilder.button(createSchemaButton(1));
        menuBuilder.button(createSchemaButton(2));
        menuBuilder.button(createSchemaButton(3));

        MenuData menuData = menuBuilder.build();

        menuData.bindToCursorItemOverflow(CursorOverflowType.DROP);

        // Build the menu
        return menuBuilder.build();
    }

    @Override
    public String getMenuID() {
        return "Schema";
    }

    // Creates the MenuButton for a specific schema ID
    private static MenuButton createSchemaButton(int schemaID) {
        // Get the correct material to make the button
        Material material = Material.AIR;
        switch (schemaID) {
            case 0: material = Material.COAL; break;
            case 1: material = Material.IRON_INGOT; break;
            case 2: material = Material.GOLD_INGOT; break;
            case 3: material = Material.DIAMOND; break;
        }

        // Build the menu button
        return MenuButtonBuilder.from(SCHEMA_BUTTON)
                // Set the slots the button will go in by pulling the slots for character "0" in the schema
                .slots(schema.getSlots(Integer.toString(schemaID)))
                // Set the metadata for the click method
                .metadata("schemaButtonID", schemaID)
                // Create the item stack to represent this button
                .itemStack(ItemStack.builder(material)
                        .displayName(AsthCore.getComponent("Schema Button " + schemaID))
                        .build())
                .build();
    }

    // A static MenuButtonBuilder with the click logic to use as a template for the schema buttons
    private static final MenuButtonBuilder SCHEMA_BUTTON = MenuButton.builder().click(Schema::schemaButtonClick);

    // A simple click function that shows ClickInfo metadata functionality
    private static void schemaButtonClick(MenuData menuData, ClickInfo clickInfo) {
        AsthCore.sendMessage(clickInfo.getPlayer(), "You clicked the Schema Button " + clickInfo.getMetadata("schemaButtonID", 0));
    }
}
