package com.asthereon.menus.Menu;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Utils.MetadataContainer;
import com.asthereon.placeholders.PlaceholderManager;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This class represents a player and the {@link MenuData} they are viewing, as a simplified argument to pass to Menu
 *  events like onLoad(), onSave() and onCursorItemOverflow()
 */
public class MenuView extends MetadataContainer {

    private final MenuData menuData;
    private final Player player;
    private MenuInventory inventory;
    private final HashMap<String, Integer> sectionOffsets = new HashMap<>();

    public MenuView(MenuData menuData, Player player) {
        this.menuData = menuData;
        this.player = player;
        this.inventory = new MenuInventory(menuData.getInventoryType(), PlaceholderManager.getComponent(player, menuData.getTitle()));
        this.inventory.storageSlots(menuData.getInventory().getStorageSlots());
    }

    // Draws a button by setting the item stack in the inventory, then adding its inventory condition to the menu
    protected void drawButton(MenuButton menuButton) {
        for (int slot : menuButton.getSlots()) {
            inventory.setItemStack(slot, PlaceholderManager.getItemStack(player, menuButton.getItemStack()));
        }

        for (InventoryCondition inventoryCondition : menuButton.getInventoryConditions()) {
            inventory.addInventoryCondition(inventoryCondition);
        }
    }

    // Draws a placeholder by checking for placeholder metadata on a menu button, with no inventory condition
    protected void drawPlaceholder(MenuButton menuPlaceholder) {
        ItemStack itemStack = PlaceholderManager.getItemStack(player, menuPlaceholder.getMetadata("placeholder",ItemStack.AIR));
        for (int slot : menuPlaceholder.getSlots()) {
            if (inventory.getItemStack(slot).isAir()) {
                inventory.setItemStack(slot, itemStack);
            }
        }
    }

    // Sets a slot to AIR
    protected void clearSlot(int slot) {
        inventory.setItemStack(slot, ItemStack.AIR);
    }

    // Sets a collection of slots to AIR
    protected void clearSlots(Collection<Integer> slots) {
        for (int slot : slots) {
            inventory.setItemStack(slot, ItemStack.AIR);
        }
    }

    public void redraw() {
        AsthCore.console("Redraw");
        Set<Player> players = inventory.getViewers();
        List<Integer> storageSlots = inventory.getStorageSlots();
        String serializedData = inventory.serialize();

        // Make sure non-button items are copied over to the new inventory or stuff like banks will be impossible
        inventory = new MenuInventory(inventory.getInventoryType(), inventory.getTitle());
        inventory.storageSlots(storageSlots);
        inventory.deserialize(serializedData);

        InventoryCondition readOnly = menuData.getReadOnly();
        if (readOnly != null) {
            inventory.addInventoryCondition(readOnly);
        }

        for (MenuButton button : menuData.getButtons()) {
            drawButton(button);
        }

        for (MenuSection section : menuData.getSections().values()) {
            section.draw(this);
        }

        // Draw placeholders last to avoid stomping on buttons
        for (MenuButton menuPlaceholder : menuData.getMenuPlaceholders()) {
            drawPlaceholder(menuPlaceholder);
        }

        for (Player player : players) {
            player.openInventory(inventory);
        }
    }

    public int getSectionOffset(String sectionName) {
        return sectionOffsets.getOrDefault(sectionName, 0);
    }

    public void setSectionOffset(String sectionName, int offset) {
        MenuSection section = menuData.getSection(sectionName);
        if (section != null) {
            _setSectionOffset(section, sectionName, offset);
        }
    }

    private void _setSectionOffset(MenuSection section, String sectionName, int offset) {
        sectionOffsets.put(sectionName, Math.min(0, Math.max(offset, section.getButtons().size() - section.getMinimumVisible())));
    }

    public void addSectionOffset(String sectionName, int offset) {
        MenuSection section = menuData.getSection(sectionName);
        if (section != null) {
            int previousOffset = sectionOffsets.getOrDefault(sectionName, 0);
            _setSectionOffset(section, sectionName, previousOffset + offset);
        }
    }

    public MenuData getMenu() {
        return menuData;
    }

    public Player getPlayer() {
        return player;
    }

    public MenuInventory getInventory() {
        return inventory;
    }
}
