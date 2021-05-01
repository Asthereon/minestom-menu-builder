package com.asthereon.menus.Menu;

import com.asthereon.menus.Enums.MenuClickType;
import com.asthereon.menus.Utils.ClickInfo;
import com.asthereon.menus.Utils.MetadataContainer;
import net.minestom.server.data.Data;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class MenuButton extends MetadataContainer {

    private ItemStack itemStack = ItemStack.AIR;
    private UUID uuid;
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
        newMenuButton.uuid = menuButton.getUUID();
        return newMenuButton;
    }

    protected MenuButton uuid(UUID uuid) {
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

    public MenuButton metadata(Data metadata) {
        this.metadata = metadata;
        return this;
    }

    public void inventoryCondition(InventoryCondition inventoryCondition) {
        this.inventoryConditions.add(inventoryCondition);
    }

    public void click(MenuClickType menuClickType, BiConsumer<Menu, ClickInfo> callback) {
        this.inventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            ClickInfo clickInfo = new ClickInfo(player, slot, clickType, inventoryConditionResult, metadata);
            if (slots.contains(slot)) {
                if (clickInfo.isMenuClickType(menuClickType)) {
                    Menu menu = MenuManager.getMenu(uuid);
                    if (null != menu) {
                        callback.accept(menu, clickInfo);
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

    public UUID getUUID() {
        return uuid;
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
