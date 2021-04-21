package com.asthereon.menus.Buttons;

import com.asthereon.menus.MenuClickType;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PageButton {

    private ItemStack itemStack;
    private List<Integer> slots = new ArrayList<>();
    private List<InventoryCondition> inventoryConditions = new ArrayList<>();
    private int offset = 0;

    public PageButton() { }

    public static PageButton slot(int slotID) {
        PageButton pageButton = new PageButton();
        pageButton.slots.add(slotID);
        return pageButton;
    }

    public static PageButton slots(Collection<Integer> slotIDs) {
        PageButton pageButton = new PageButton();
        pageButton.slots.addAll(slotIDs);
        return pageButton;
    }

    public PageButton itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public PageButton offset(int offset) {
        this.offset = offset;
        return this;
    }

    public PageButton inventoryCondition(InventoryCondition inventoryCondition) {
        this.inventoryConditions.add(inventoryCondition);
        return this;
    }

    public PageButton click(Runnable callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slots.contains(slot)) {
                callback.run();
            }
        }));
    }

    public PageButton click(MenuClickType menuClickType, Runnable callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slots.contains(slot)) {
                if (menuClickType.toString().equals(clickType.toString())) {
                    callback.run();
                }
            }
        }));
    }

    public PageButton click(PageButton pageButton) {
        this.inventoryConditions = pageButton.getInventoryConditions();
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isIncrement() {
        return offset > 0;
    }

    public boolean isDecrement() {
        return offset < 0;
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