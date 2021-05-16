package com.asthereon.menus.Menu;

import com.asthereon.menus.Enums.MenuClickType;
import com.asthereon.menus.Utils.ClickInfo;
import com.asthereon.menus.Utils.Metadata;
import com.asthereon.menus.Utils.MetadataContainer;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class MenuButton extends MetadataContainer {

    private ItemStack itemStack = ItemStack.AIR;
    private String menuID;
    private final List<Integer> slots = new ArrayList<>();
    private List<InventoryCondition> inventoryConditions = new ArrayList<>();

    public MenuButton() { }

    public static MenuButtonBuilder builder() {
        return MenuButtonBuilder.builder();
    }

    public static MenuButton from(MenuButton menuButton) {
        MenuButton newMenuButton = new MenuButton();
        newMenuButton.itemStack = menuButton.getItemStack();
        newMenuButton.slots.addAll(menuButton.getSlots());
        newMenuButton.inventoryConditions = menuButton.getInventoryConditions();
        newMenuButton.menuID = menuButton.getMenuID();
        return newMenuButton;
    }

    protected MenuButton menuID(String menuID) {
        this.menuID = menuID;
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

    public MenuButton metadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public void inventoryCondition(InventoryCondition inventoryCondition) {
        this.inventoryConditions.add(inventoryCondition);
    }

    public void click(MenuClickType menuClickType, BiConsumer<MenuData, ClickInfo> callback) {
        this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            ClickInfo clickInfo = new ClickInfo(player, slot, clickType, inventoryConditionResult, metadata);
            if (slots.contains(slot)) {
                if (clickInfo.isMenuClickType(menuClickType)) {
                    Menu menu = MenuManager.getMenu(menuID);
                    if (null != menu) {
                        MenuData menuData = menu.getMenuData();
                        if (null != menuData) {
                            callback.accept(menuData, clickInfo);
                        }
                    }
                }
            }
        }));
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public String getMenuID() {
        return menuID;
    }

    public MenuButton setSlot(int slot) {
        this.slots.clear();
        this.slots.add(slot);
        return this;
    }

    public List<InventoryCondition> getInventoryConditions() {
        return inventoryConditions;
    }
}
