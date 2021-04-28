package com.asthereon.menus.Menu;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A singleton manager that controls the references to the various menus that are created, as well as registering the
 *  events to handle the inventory open and close events.
 */
public class MenuManager {

    // Instance
    private static final MenuManager instance = new MenuManager();

    // Variables
    public static HashMap<UUID, Menu> menus = new HashMap<>();

    // Default private constructor (cant instantiate from other classes)
    private MenuManager() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addEventCallback(InventoryCloseEvent.class, event -> closeInventoryEvent(event.getPlayer(), event.getInventory()));

        globalEventHandler.addEventCallback(InventoryOpenEvent.class, event -> closeInventoryEvent(event.getPlayer(), event.getPlayer().getOpenInventory()));
    }

    // Get Instance of class
    public static MenuManager getInstance() {
        return instance;
    }

    // Handles the cursor item overflow and close event for a player's open menu when their current menu is closed
    private static void closeInventoryEvent(Player player, Inventory inventory) {
        if (inventory != null) {
            Menu menu = MenuManager.getMenu(player);
            if (null != menu) {
                if (menu.getInventory().equals(inventory)) {
                    // Menu inventory closed, handle the menu cursor
                    CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultMenuOverflowType());
                    menu.closeEvent(player);
                } else {
                    // Non-menu inventory closed, handle the inventory cursor
                    CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultInventoryOverflowType());
                }
            } else {
                // Non-menu inventory closed, handle the inventory cursor
                CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultInventoryOverflowType());
            }
        } else {
            // Player inventory closed, handle the player inventory cursor
            CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultPlayerInventoryOverflowType());
        }
    }

    public static void register(Menu menu) {
        MenuManager.getInstance()._register(menu);
    }

    private void _register(Menu menu) {
        menus.put(menu.getUuid(), menu);
    }

    public static Menu unregister(Menu menu) {
        return MenuManager.getInstance()._unregister(menu.getUuid());
    }

    public static Menu unregister(UUID uuid) {
        return MenuManager.getInstance()._unregister(uuid);
    }

    private Menu _unregister(UUID uuid) {
        return menus.remove(uuid);
    }

    /**
     * Gets a {@link Menu} from a given menu UUID
     * @param uuid the uuid of the menu
     * @return the menu, or null if none was found with the given uuid
     */
    public static Menu getMenu(UUID uuid) {
        return MenuManager.getInstance()._getMenu(uuid);
    }

    @Nullable
    private Menu _getMenu(UUID uuid) {
        return menus.getOrDefault(uuid, null);
    }

    /**
     * Gets a {@link Menu} from a given player
     * @param player the player whose menu should be found
     * @return the menu, or null if none was found with the given player
     */
    @Nullable
    public static Menu getMenu(Player player) { return MenuManager.getInstance()._getMenu(player); }

    private Menu _getMenu(Player player) {
        for (Map.Entry<UUID, Menu> entry : menus.entrySet()) {
            if (entry.getValue().isPlayerViewing(player)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Forces a {@link Menu} with the given UUID to redraw itself
     * @param uuid the uuid of the menu
     */
    public static void redraw(UUID uuid) {
        Menu menu = getMenu(uuid);
        if (null != menu) {
            menu.redraw();
        }
    }
}
