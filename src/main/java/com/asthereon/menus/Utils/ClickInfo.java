package com.asthereon.menus.Utils;

import com.asthereon.menus.Enums.MenuClickType;
import com.asthereon.menus.Utils.MetadataContainer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;

/**
 * A collection of the information provided by the click event, as well as the metadata from the button that was clicked
 *  to trigger the event.
 */
public class ClickInfo extends MetadataContainer {

    private final Player player;
    private final int slot;
    private final ClickType clickType;
    private final InventoryConditionResult inventoryConditionResult;

    public ClickInfo(Player player, int slot, ClickType clickType, InventoryConditionResult inventoryConditionResult, Metadata metadata) {
        this.player = player;
        this.slot = slot;
        this.clickType = clickType;
        this.inventoryConditionResult = inventoryConditionResult;
        this.metadata = metadata;
    }

    /**
     * Checks if a given {@link MenuClickType} matches the click type that triggered the event.
     * @param menuClickType the menu click type to compare to
     * @return whether the click types match
     */
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

    /**
     * Gets the ItemStack from the associated slot on the open inventory, or AIR if the slot is empty or inventory is null
     * @return the ItemStack in the associated slot
     */
    public ItemStack getItemStack() {
        ItemStack itemStack = ItemStack.AIR;

        Inventory inventory = player.getOpenInventory();
        if (null != inventory) {
            itemStack = inventory.getItemStack(slot);
        }

        return itemStack;
    }
}
