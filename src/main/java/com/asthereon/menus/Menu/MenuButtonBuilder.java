package com.asthereon.menus.Menu;

import com.asthereon.menus.Enums.MenuClickType;
import com.asthereon.menus.Utils.ClickInfo;
import com.asthereon.menus.Utils.MetadataContainer;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;

public class MenuButtonBuilder extends MetadataContainer {

    private ItemStack itemStack = ItemStack.AIR;
    private UUID uuid;
    private final List<Integer> slots = new ArrayList<>();
    private HashMap<MenuClickType, BiConsumer<MenuData, ClickInfo>> inventoryConditions = new HashMap<>();

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
        this.itemStack = this.itemStack.with(builder -> builder.amount(amount));
        return this;
    }

    public MenuButtonBuilder displayName(Component displayName) {
        this.itemStack = this.itemStack.with(builder -> builder.displayName(displayName));
        return this;
    }

    public MenuButtonBuilder inventoryCondition(MenuClickType menuClickType, BiConsumer<MenuData,ClickInfo> callback) {
        this.inventoryConditions.put(menuClickType, callback);
        return this;
    }

    public MenuButtonBuilder click(BiConsumer<MenuData,ClickInfo> callback) {
        return this.inventoryCondition(MenuClickType.ALL, callback);
    }

    public MenuButtonBuilder click(MenuClickType menuClickType, BiConsumer<MenuData,ClickInfo> callback) {
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

    public HashMap<MenuClickType, BiConsumer<MenuData, ClickInfo>> getInventoryConditions() {
        return inventoryConditions;
    }

    public <T> MenuButtonBuilder metadata(String key, T value, Class<T> type) { metadata.set(key, value, type); return this; }

    public <T> MenuButtonBuilder metadata(String key, T value) { metadata.set(key, value); return this; }

    public MenuButton build() {
        MenuButton menuButton = new MenuButton()
                .slots(slots)
                .metadata(metadata)
                .itemStack(itemStack);

        inventoryConditions.forEach((menuButton::click));

        return menuButton;
    }

}
