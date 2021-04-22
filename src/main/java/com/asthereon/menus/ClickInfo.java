package com.asthereon.menus;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;

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
        return menuClickType.toString().equals(clickType.toString());
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
}
