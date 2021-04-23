package com.asthereon.menus;

import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;

public class ClickInfo {

    private final Player player;
    private final int slot;
    private final ClickType clickType;
    private final InventoryConditionResult inventoryConditionResult;
    private final Data metadata;

    public ClickInfo(Player player, int slot, ClickType clickType, InventoryConditionResult inventoryConditionResult, Data metadata) {
        this.player = player;
        this.slot = slot;
        this.clickType = clickType;
        this.inventoryConditionResult = inventoryConditionResult;
        this.metadata = metadata;
    }

    public boolean isMenuClickType(MenuClickType menuClickType) {
        return menuClickType.equals(MenuClickType.ALL) || menuClickType.toString().equals(clickType.toString());
    }

    public Player getPlayer() {
        return player;
    }

    public int getSlot() {
        return slot;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public InventoryConditionResult getInventoryConditionResult() {
        return inventoryConditionResult;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = ItemStack.AIR;

        Inventory inventory = player.getOpenInventory();
        if (null != inventory) {
            itemStack = inventory.getItemStack(slot);
        }

        return itemStack;
    }

    public Data getMetadata() {
        return metadata;
    }

    public <T> T getMetadata(String key, T defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }

    public <T> void setMetadata(String key, T value, Class<T> type) { metadata.set(key, value, type); }

    public <T> void setMetadata(String key, T value) { metadata.set(key, value); }
}
