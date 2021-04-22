package com.asthereon.menus.Buttons;

import com.asthereon.menus.Menu;
import com.asthereon.menus.MenuClickType;
import com.asthereon.menus.MenuManager;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MenuButton {

    private ItemStack itemStack = ItemStack.AIR;
    private UUID uuid;
    private List<Integer> slots = new ArrayList<>();
    private List<InventoryCondition> inventoryConditions = new ArrayList<>();

    private MenuButton() { }

    public static MenuButton on(UUID menuUUID) {
        MenuButton menuButton = new MenuButton();
        menuButton.uuid = menuUUID;
        return menuButton;
    }

    public MenuButton slot(int slotID) {
        this.slots.add(slotID);
        return this;
    }

    public MenuButton slots(Collection<Integer> slotIDs) {
        this.slots.addAll(slotIDs);
        return this;
    }

    public MenuButton itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public MenuButton inventoryCondition(InventoryCondition inventoryCondition) {
        this.inventoryConditions.add(inventoryCondition);
        return this;
    }

    public MenuButton click(Consumer<Menu> callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slots.contains(slot)) {
                Menu menu = MenuManager.getMenu(uuid);
                if (null != menu) {
                    callback.accept(menu);
                }
            }
        }));
    }

    public MenuButton click(MenuClickType menuClickType, Consumer<Menu> callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slots.contains(slot)) {
                if (menuClickType.toString().equals(clickType.toString())) {
                    Menu menu = MenuManager.getMenu(uuid);
                    if (null != menu) {
                        callback.accept(menu);
                    }
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
