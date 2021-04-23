package com.asthereon.menus;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;

public class ClickInfo {

    Player player;
    int slot;
    ClickType clickType;
    InventoryConditionResult inventoryConditionResult;

    public ClickInfo(Player player, int slot, ClickType clickType, InventoryConditionResult inventoryConditionResult) {
        this.player = player;
        this.slot = slot;
        this.clickType = clickType;
        this.inventoryConditionResult = inventoryConditionResult;
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
}
