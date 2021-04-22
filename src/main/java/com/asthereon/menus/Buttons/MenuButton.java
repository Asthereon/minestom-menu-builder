package com.asthereon.menus.Buttons;

import com.asthereon.menus.ClickInfo;
import com.asthereon.menus.Menu;
import com.asthereon.menus.MenuClickType;
import com.asthereon.menus.MenuManager;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MenuButton {

    private ItemStack itemStack = ItemStack.AIR;
    private UUID uuid;
    private List<Integer> slots = new ArrayList<>();
    private List<InventoryCondition> inventoryConditions = new ArrayList<>();

    public MenuButton() { }

    public static MenuButton from(MenuButton menuButton) {
        MenuButton newMenuButton = new MenuButton();
        newMenuButton.itemStack = menuButton.getItemStack();
        newMenuButton.slots = menuButton.getSlots();
        newMenuButton.inventoryConditions = menuButton.getInventoryConditions();
        newMenuButton.uuid = menuButton.getUUID();
        return newMenuButton;
    }

    public MenuButton uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
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

    public MenuButton click(BiConsumer<Menu,ClickInfo> callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            ClickInfo clickInfo = new ClickInfo(player, slot, clickType, inventoryConditionResult);
            if (slots.contains(slot)) {
                Menu menu = MenuManager.getMenu(uuid);
                if (null != menu) {
                    callback.accept(menu, clickInfo);
                }
            }
        }));
    }

    public MenuButton click(MenuClickType menuClickType, BiConsumer<Menu,ClickInfo> callback) {
        return this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            ClickInfo clickInfo = new ClickInfo(player, slot, clickType, inventoryConditionResult);
            if (slots.contains(slot)) {
                if (clickInfo.isMenuClickType(menuClickType)) {
                    Menu menu = MenuManager.getMenu(uuid);
                    if (null != menu) {
                        callback.accept(menu,clickInfo);
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

    public UUID getUUID() {
        return uuid;
    }

    public void setSlot(int slot) {
        this.slots.clear();
        this.slots.add(slot);
    }

    public List<InventoryCondition> getInventoryConditions() {
        return inventoryConditions;
    }
}
