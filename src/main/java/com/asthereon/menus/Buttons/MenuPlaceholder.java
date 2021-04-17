package com.asthereon.menus.Buttons;

import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MenuPlaceholder {
    
    private ItemStack itemStack;
    private List<Integer> slots = new ArrayList<>();

    public MenuPlaceholder() { }

    public static MenuPlaceholder slot(int slotID) {
        MenuPlaceholder menuPlaceholder = new MenuPlaceholder();
        menuPlaceholder.slots.add(slotID);
        return menuPlaceholder;
    }

    public static MenuPlaceholder slots(Collection<Integer> slotIDs) {
        MenuPlaceholder menuPlaceholder = new MenuPlaceholder();
        menuPlaceholder.slots.addAll(slotIDs);
        return menuPlaceholder;
    }

    public static MenuPlaceholder slotRange(int minSlot, int maxSlot) {
        MenuPlaceholder menuPlaceholder = new MenuPlaceholder();
        for (int slot = minSlot; slot <= maxSlot; slot++) {
            menuPlaceholder.slots.add(slot);
        }
        return menuPlaceholder;
    }

    public MenuPlaceholder itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
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
    
}
