package com.asthereon.menus.Buttons;

import com.asthereon.menus.MenuClickType;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MenuButton {

    private ItemStack itemStack;
    private List<Integer> slots = new ArrayList<>();
    private List<InventoryCondition> inventoryConditions = new ArrayList<>();

    public MenuButton() { }

    public static MenuButton slot(int slotID) {
        MenuButton menuButton = new MenuButton();
        menuButton.slots.add(slotID);
        return menuButton;
    }

    public static MenuButton slots(Collection<Integer> slotIDs) {
        MenuButton menuButton = new MenuButton();
        menuButton.slots.addAll(slotIDs);
        return menuButton;
    }

    public MenuButton itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public MenuButton inventoryCondition(InventoryCondition inventoryCondition) {
        this.inventoryConditions.add(inventoryCondition);
        return this;
    }

    public MenuButton click(Runnable callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slots.contains(slot)) {
                callback.run();
            }
        }));
    }

    public MenuButton click(MenuClickType menuClickType, Runnable callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slots.contains(slot)) {
                if (menuClickType.toString().equals(clickType.toString())) {
                    callback.run();
                }
            }
        }));
    }

    public MenuButton click(MenuButton menuButton) {
        this.inventoryConditions = menuButton.getInventoryConditions();
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlot(int slot) {
        this.slots.clear();
        this.slots.add(slot);
    }

    public List<InventoryCondition> getInventoryConditions() {
        return inventoryConditions;
    }
}
