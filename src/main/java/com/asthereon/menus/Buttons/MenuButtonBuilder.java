package com.asthereon.menus.Buttons;

import com.asthereon.menus.ClickInfo;
import com.asthereon.menus.Menu;
import com.asthereon.menus.MenuClickType;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;

public class MenuButtonBuilder {

    private ItemStack itemStack = ItemStack.AIR;
    private UUID uuid;
    private final List<Integer> slots = new ArrayList<>();
    private HashMap<MenuClickType, BiConsumer<Menu, ClickInfo>> inventoryConditions = new HashMap<>();

    private MenuButtonBuilder() { }

    protected static MenuButtonBuilder builder() {
        return new MenuButtonBuilder();
    }

    public static MenuButtonBuilder from(MenuButtonBuilder menuButtonBuilder) {
        MenuButtonBuilder newMenuButtonBuilder = new MenuButtonBuilder();
        newMenuButtonBuilder.itemStack = menuButtonBuilder.getItemStack();
        newMenuButtonBuilder.slots.addAll(menuButtonBuilder.getSlots());
        newMenuButtonBuilder.inventoryConditions = new HashMap<>(menuButtonBuilder.getInventoryConditions());
        newMenuButtonBuilder.uuid = menuButtonBuilder.getUUID();
        return newMenuButtonBuilder;
    }

    public MenuButtonBuilder slot(int slotID) {
        this.slots.add(slotID);
        return this;
    }

    public MenuButtonBuilder slots(Collection<Integer> slotIDs) {
        this.slots.addAll(slotIDs);
        return this;
    }

    public MenuButtonBuilder itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public MenuButtonBuilder amount(int amount) {
        this.itemStack = this.itemStack.with(builder -> {
            builder.amount(amount);
        });
        return this;
    }

    public MenuButtonBuilder displayName(Component displayName) {
        this.itemStack = this.itemStack.with(builder -> {
            builder.displayName(displayName);
        });
        return this;
    }

    public MenuButtonBuilder inventoryCondition(MenuClickType menuClickType, BiConsumer<Menu,ClickInfo> callback) {
        this.inventoryConditions.put(menuClickType, callback);
        return this;
    }

    public MenuButtonBuilder click(BiConsumer<Menu,ClickInfo> callback) {
        return this.inventoryCondition(MenuClickType.ALL, callback);
    }

    public MenuButtonBuilder click(MenuClickType menuClickType, BiConsumer<Menu,ClickInfo> callback) {
        return this.inventoryCondition(menuClickType, callback);
    }

    public MenuButtonBuilder click(MenuButtonBuilder menuButtonBuilder) {
        this.inventoryConditions = menuButtonBuilder.getInventoryConditions();
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

    public MenuButtonBuilder setSlot(int slot) {
        this.slots.clear();
        this.slots.add(slot);
        return this;
    }

    public HashMap<MenuClickType, BiConsumer<Menu, ClickInfo>> getInventoryConditions() {
        return inventoryConditions;
    }

    public MenuButton build() {
        MenuButton menuButton = new MenuButton()
                .slots(slots)
                .itemStack(itemStack);

        inventoryConditions.forEach((menuButton::click));

        return menuButton;
    }

}
