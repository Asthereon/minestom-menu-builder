package com.asthereon.menus.Menu;

import com.asthereon.menus.Buttons.MenuButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.data.Data;
import net.minestom.server.data.DataImpl;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.*;

/**
 * A builder for creating {@link Menu Menus}
 */
public class MenuBuilder {

    private final MenuInventory inventory;
    private String storageData = null;
    private boolean readOnly = false;
    private final UUID uuid;
    private final List<Integer> readOnlySlots = new ArrayList<>();
    private final List<MenuButton> buttons = new ArrayList<>();
    private final List<MenuButton> menuPlaceholders = new ArrayList<>();
    private final HashMap<String, MenuSection> sections = new HashMap<>();
    private Data metadata = new DataImpl();

    private MenuBuilder(InventoryType inventoryType, Component title) {
        this.inventory = new MenuInventory(inventoryType, title);
        this.uuid = UUID.randomUUID();
    }

    /**
     * Create a MenuBuilder for a menu with a given inventory type and title
     * @param inventoryType the type of inventory to use for the menu
     * @param title the title of the menu
     * @return the new MenuBuilder
     */
    public static MenuBuilder of(InventoryType inventoryType, Component title) {
        return new MenuBuilder(inventoryType, title);
    }

    /**
     * Sets if the entire Menu will be read only.  Read only prevents all items in the menu from being removed, which is
     *  preferred for interfaces that do not contain any items the player should be able to remove.
     * @param readOnly whether the entire menu should be read only
     * @return this MenuBuilder
     */
    public MenuBuilder readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    /**
     * Adds a specific slot to the list of read only slots.  This feature only works if the menu is set to not be read
     *  only, otherwise the menu's read only status will override the read only slots specified.
     * @param slot the slot to make read only
     * @return this MenuBuilder
     */
    public MenuBuilder readOnlySlot(int slot) {
        this.readOnlySlots.add(slot);
        return this;
    }

    /**
     * Adds the specified slots to the list of read only slots.  This feature only works if the menu is set to not be
     * read only, otherwise the menu's read only status will override the read only slots specified.
     * @param slots the slots to make read only
     * @return this MenuBuilder
     */
    public MenuBuilder readOnlySlots(Integer... slots) {
        this.readOnlySlots.addAll(Arrays.asList(slots));
        return this;
    }

    /**
     * Adds the specified slots to the list of read only slots.  This feature only works if the menu is set to not be
     * read only, otherwise the menu's read only status will override the read only slots specified.
     * @param slots the slots to make read only
     * @return this MenuBuilder
     */
    public MenuBuilder readOnlySlots(Collection<Integer> slots) {
        this.readOnlySlots.addAll(slots);
        return this;
    }

    /**
     * Adds the slots between min and max inclusive to the list of read only slots.  This feature only works if the menu
     * is set to not bb read only, otherwise the menu's read only status will override the read only slots specified.
     * @param min the minimum slot to make read only
     * @param max the maximum slot to make read only
     * @return this MenuBuilder
     */
    public MenuBuilder readOnlyRange(int min, int max) {
        for (int i = min; i <= max; i++) {
            this.readOnlySlots.add(i);
        }
        return this;
    }

    /**
     * Sets the metadata for the {@link Menu} to the provided {@link Data} object. If this is not provided, it will
     *  default to an empty {@link DataImpl} when the Menu is built.
     * @param metadata the metadata
     * @return this MenuBuilder
     */
    public MenuBuilder metadata(Data metadata) {
        if (metadata != null) {
            this.metadata = metadata;
        }
        return this;
    }

    /**
     * Sets a specific slot to have a given {@link ItemStack}. If the specified slot is not read only, and the Menu is
     *  not read only, this item will be able to be taken by a player viewing the Menu.
     * @param slotID the slot to put the ItemStack in
     * @param itemStack the ItemStack to put in the Menu
     * @return this MenuBuilder
     */
    public MenuBuilder setItem(int slotID, ItemStack itemStack) {
        this.inventory.setItemStack(slotID, itemStack);
        return this;
    }

    /**
     * Adds a {@link MenuButton} to the Menu, as well as sets the menu UUID on the MenuButton.
     * @param menuButton the MenuButton to add to the Menu
     * @return this MenuBuilder
     */
    public MenuBuilder button(MenuButton menuButton) {
        // Set the UUID of the menu on the button, then add it to the buttons list
        buttons.add(menuButton.uuid(uuid));
        return this;
    }

    /**
     * Adds a {@link MenuButton} that has no click effect
     * @param slot the slot
     * @param itemStack the item stack to display
     * @return this MenuBuilder
     */
    public MenuBuilder placeholder(int slot, ItemStack itemStack) {
        menuPlaceholders.add(MenuButton.builder()
                .slot(slot)
                .metadata("placeholder",itemStack)
                .build());
        return this;
    }

    /**
     * Adds a {@link MenuButton} that has no click effect
     * @param slots the slots
     * @param itemStack the item stack to display
     * @return this MenuBuilder
     */
    public MenuBuilder placeholder(Collection<Integer> slots, ItemStack itemStack) {
        menuPlaceholders.add(MenuButton.builder()
                .slots(slots)
                .metadata("placeholder",itemStack)
                .build());
        return this;
    }

    /**
     * Adds a {@link MenuSection} to the Menu.
     * @param menuSection the MenuSection to add to the Menu
     * @return this MenuBuilder
     */
    public MenuBuilder section(MenuSection menuSection) {
        sections.put(menuSection.getName(), menuSection);
        return this;
    }

    /**
     * Sets the serialized storage data to be sent to the {@link Menu}, which is used to populate the menu with a
     *  default set of ItemStacks
     * @param storageData the serialized storage data
     * @return this MenuBuilder
     */
    public MenuBuilder storageData(String storageData) {
        this.storageData = storageData;
        return this;
    }

    /**
     * Builds the {@link Menu} using this MenuBuilder, by creating a readOnlyCondition which handles cancelling interact
     *  events with those slots, instantiating the Menu object, and setting up which slots are considered storage slots.
     * @return the new Menu
     */
    public Menu build() {
        InventoryCondition readOnlyCondition = null;

        if (readOnly) {
            readOnlyCondition = (player, slot, clickType, inventoryConditionResult) -> inventoryConditionResult.setCancel(true);
        } else {
            if (readOnlySlots.size() > 0) {
                readOnlyCondition = (player, slot, clickType, inventoryConditionResult) -> {
                    if (readOnlySlots.contains(slot)) {
                        inventoryConditionResult.setCancel(true);
                    }
                };
            }
        }

        Menu menu = new Menu(uuid, metadata, inventory, readOnlyCondition, buttons, sections, menuPlaceholders, storageData);

        if (!readOnly) {
            // IF there are read only slots, that means there should be storage slots so set those on the menu
            MenuInventory menuInventory = menu.getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (!readOnlySlots.contains(i)) {
                    menuInventory.storageSlot(i);
                }
            }
        }

        return menu;
    }
}
