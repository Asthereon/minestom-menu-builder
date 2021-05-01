package com.asthereon.menus.Menu;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Enums.CursorInventoryType;
import com.asthereon.menus.Enums.CursorOverflowType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.time.TimeUnit;

import java.util.function.BiConsumer;

/**
 * This class is designed to allow for pre-configured but customizable handling for items that are on the player's
 *  cursor when they close an inventory.
 */
public class CursorOverflow {

    // This is the default behavior for Minestom, items on the cursor are just deleted from existence
    private final static BiConsumer<Player, ItemStack> DESTROY = (player, itemStack) -> {};

    // This is the default behavior expected of Minecraft, where the items are dropped in front of the player
    private final static BiConsumer<Player, ItemStack> DROP = (player, itemStack) -> {
        Instance instance = player.getInstance();
        if (null != instance) {
            ItemEntity itemEntity = new ItemEntity(itemStack, player.getPosition().clone().add(0, 1.5, 0));
            itemEntity.setPickupDelay(500, TimeUnit.MILLISECOND);
            itemEntity.setInstance(player.getInstance());
            Vector velocity = player.getPosition().clone().getDirection().multiply(6);
            itemEntity.setVelocity(velocity);
        }
    };

    // This is a customizable behavior that can be set to allow for easy re-use to meet the needs of a server
    private static BiConsumer<Player, ItemStack> CUSTOM = (player, itemStack) -> {};

    // This is the default behavior assigned to non-menu inventories that are closed
    private static BiConsumer<Player, ItemStack> DEFAULT = DROP;

    // The default behavior for cursor item overflow on menus handled by this menu system
    private static CursorOverflowType defaultMenuOverflowType = CursorOverflowType.MENU;

    // The default behavior for cursor item overflow on inventories not handled by this menu system
    private static CursorOverflowType defaultInventoryOverflowType = CursorOverflowType.DEFAULT;

    // The default behavior for cursor item overflow on the player's personal inventory
    private static CursorOverflowType defaultPlayerInventoryOverflowType = CursorOverflowType.DEFAULT;

    public static CursorOverflowType getDefaultMenuOverflowType() { return defaultMenuOverflowType; }
    public static void setDefaultMenuOverflowType(CursorOverflowType defaultMenuOverflowType) { CursorOverflow.defaultMenuOverflowType = defaultMenuOverflowType; }

    public static CursorOverflowType getDefaultInventoryOverflowType() { return defaultInventoryOverflowType; }
    public static void setDefaultInventoryOverflowType(CursorOverflowType defaultInventoryOverflowType) { CursorOverflow.defaultInventoryOverflowType = defaultInventoryOverflowType; }

    public static CursorOverflowType getDefaultPlayerInventoryOverflowType() { return defaultPlayerInventoryOverflowType; }
    public static void setDefaultPlayerInventoryOverflowType(CursorOverflowType defaultPlayerInventoryOverflowType) { CursorOverflow.defaultPlayerInventoryOverflowType = defaultPlayerInventoryOverflowType; }

    /**
     * Sets the customizable cursor item overflow to a re-usable handler
     * @param cursorOverflowHandler how the overflow items should be handled
     */
    public static void setCustom(BiConsumer<Player, ItemStack> cursorOverflowHandler) {
        CursorOverflow.CUSTOM = cursorOverflowHandler;
    }

    /**
     * Sets the default cursor item overflow to a customizable handler
     * @param cursorOverflowHandler how the overflow items should be handled
     */
    public static void setDefault(BiConsumer<Player, ItemStack> cursorOverflowHandler) {
        CursorOverflow.DEFAULT = cursorOverflowHandler;
    }

    /**
     * Sets the default cursor item overflow to a pre-defined handler
     * @param cursorOverflowType the type of cursor item overflow to set the handler to
     */
    public static void setDefault(CursorOverflowType cursorOverflowType) {
        CursorOverflow.DEFAULT = getCursorOverflowHandler(cursorOverflowType);
    }

    /**
     * Converts a {@link CursorOverflowType} to the associated handler
     * @param cursorOverflowType the type of cursor item overflow
     * @return the handler for the cursor item overflow
     */
    public static BiConsumer<Player, ItemStack> getCursorOverflowHandler(CursorOverflowType cursorOverflowType) {
        switch (cursorOverflowType) {
            case DESTROY:
                return DESTROY;
            case DROP:
                return DROP;
            case CUSTOM:
                return CUSTOM;
            default:
                return DEFAULT;
        }
    }

    // Handles cursor overflow based on cursor overflow type
    private static void handleCursorOverflow(Player player, ItemStack itemStack, CursorOverflowType cursorOverflowType) {
        // Get the handler for the specified overflow type
        BiConsumer<Player, ItemStack> cursorOverflowHandler = getCursorOverflowHandler(cursorOverflowType);

        // IF the cursor overflow type is a menu
        if (cursorOverflowType.equals(CursorOverflowType.MENU)) {
            Menu menu = MenuManager.getInstance().getMenu(player);
            if (null != menu) {
                // IF the menu has a menu-specific cursor item overflow handler
                if (menu.isMenuCursorItemOverflow()) {
                    // Use the cursor item overflow handler from the menu
                    menu.getMenuCursorItemOverflow().accept(new MenuView(menu, player), itemStack);
                    return;
                } else {
                    // Use the default cursor item overflow handler for the menu
                    cursorOverflowHandler = menu.getDefaultCursorItemOverflow();
                }
            } else {
                // The menu is unrecognized, set the handler to the default
                cursorOverflowHandler = DEFAULT;
            }
        }

        // Handler the cursor overflow
        cursorOverflowHandler.accept(player, itemStack);
    }

    // Attempts to put the cursor item in the player's inventory, otherwise it attempts to handle the overflow
    protected static void handleCursorItem(Player player, CursorOverflowType cursorOverflowType) {
        CursorInventoryType cursorInventoryType;

        // Get the player's inventory cursor item, if it exists
        ItemStack inventoryCursorItem = player.getInventory().getCursorItem();

        // Get the player's open inventory's cursor item, if it exists
        Inventory menu = player.getOpenInventory();
        ItemStack menuCursorItem = ItemStack.AIR;
        if (null != menu) {
            menuCursorItem = menu.getCursorItem(player);
        }

        ItemStack cursorItem = ItemStack.AIR;

        // Determine what kind of cursor inventory type is being handled
        if (!inventoryCursorItem.isAir() && menuCursorItem.isAir()) {
            cursorInventoryType = CursorInventoryType.PLAYER_INVENTORY;
            cursorItem = inventoryCursorItem;
        } else if (inventoryCursorItem.isAir() && !menuCursorItem.isAir()) {
            cursorInventoryType = CursorInventoryType.MENU;
            cursorItem = menuCursorItem;
        } else if (inventoryCursorItem.isAir()){
            cursorInventoryType = CursorInventoryType.NONE;
        } else {
            cursorInventoryType = CursorInventoryType.UNKNOWN;
        }

        // IF there is a cursor item to handle
        if (cursorInventoryType != CursorInventoryType.NONE) {
            // Attempt to place the item stack in the player's inventory
            boolean couldFit = player.getInventory().addItemStack(cursorItem);

            // IF the cursor item couldn't fit in the inventory, it's considered overflow
            if (!couldFit) {
                // Handle the overflow item following the given overflow type
                handleCursorOverflow(player, cursorItem, cursorOverflowType);
            }

            // Clear the player's cursor
            clearCursor(player, cursorInventoryType);
        }
    }

    /**
     * Clears the cursor of the provided {@link CursorInventoryType}
     * @param player the player who should have their cursor cleared
     * @param cursorInventoryType the cursor inventory type, or UNKNOWN if not known
     */
    private static void clearCursor(Player player, CursorInventoryType cursorInventoryType) {
        Inventory menu = player.getOpenInventory();
        switch (cursorInventoryType) {
            case PLAYER_INVENTORY:
                player.getInventory().setCursorItem(ItemStack.AIR);
                break;
            case MENU:
                if (null != menu) {
                    AsthCore.console("Clearing menu cursor");
                    menu.setCursorItem(player, ItemStack.AIR);
                }
                break;
            case UNKNOWN:
                player.getInventory().setCursorItem(ItemStack.AIR);
                if (null != menu) {
                    menu.setCursorItem(player, ItemStack.AIR);
                }
                break;
        }
    }

    /**
     * Clears both the player inventory cursor and the player's open inventory's cursor at the same time
     * @param player the player who should have their cursor cleared
     */
    public static void clearCursor(Player player) {
        clearCursor(player, CursorInventoryType.UNKNOWN);
    }
}
