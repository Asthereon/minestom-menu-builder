package com.asthereon.menus.Menu;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Utils.Metadata;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton manager that controls the references to the various menus that are created, as well as registering the
 *  events to handle the inventory open and close events.
 */
public class MenuManager {

    // Instance
    private static final MenuManager instance = new MenuManager();

    // Variables
    public static HashMap<String, Menu> customMenus = new HashMap<>();

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
                MenuData menuData = menu.getMenuData();
                if (null != menuData) {
                    MenuView menuView = menuData.getMenuView(player);
                    if (menuView != null) {
                        if (menuView.getInventory().equals(inventory)) {
                            // Menu inventory closed, handle the menu cursor
                            CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultMenuOverflowType());
                            menuData.closeEvent(player);
                            return;
                        }
                    }
                }
            }

            // Non-menu inventory closed, handle the inventory cursor
            CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultInventoryOverflowType());
        } else {
            // Player inventory closed, handle the player inventory cursor
            CursorOverflow.handleCursorItem(player, CursorOverflow.getDefaultPlayerInventoryOverflowType());
        }
    }

    public static void register(Menu menu) {
        MenuManager.getInstance()._register(menu);
    }

    private void _register(Menu menu) {
        if (customMenus.containsKey(menu.getMenuID())) {
            AsthCore.console("[MenuManager] Creation of a menu with duplicate ID '" + menu.getMenuID() + "' has been cancelled.");
        } else {
            customMenus.put(menu.getMenuID(), menu);
        }
    }

    public static Menu unregister(Menu menu) {
        return MenuManager.getInstance()._unregister(menu.getMenuID());
    }

    public static Menu unregister(String menuID) {
        return MenuManager.getInstance()._unregister(menuID);
    }

    private Menu _unregister(String menuID) {
        return customMenus.remove(menuID);
    }

    public static void open(String menuID, Player player, Metadata metadata) {
        Menu menu = MenuManager.getMenu(menuID);
        if (null != menu) {
            menu.open(player, metadata);
        }
    }

    /**
     * Gets a {@link Menu} from a given menu ID
     * @param menuID the ID of the menu
     * @return the menu, or null if none was found with the given menu ID
     */
    public static Menu getMenu(String menuID) {
        return MenuManager.getInstance()._getMenu(menuID);
    }

    @Nullable
    private Menu _getMenu(String menuID) {
        return customMenus.getOrDefault(menuID, null);
    }

    /**
     * Gets a {@link MenuData} from a given player
     * @param player the player whose menu should be found
     * @return the menu, or null if none was found with the given player
     */
    @Nullable
    public static Menu getMenu(Player player) { return MenuManager.getInstance()._getMenu(player); }

    private Menu _getMenu(Player player) {
        for (Map.Entry<String, Menu> entry : customMenus.entrySet()) {
            if (entry.getValue().getMenuData().isPlayerViewing(player)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Forces a {@link Menu} with the given menu ID to redraw itself
     * @param menuID the ID of the menu
     */
    public static void redraw(String menuID) {
        Menu menu = getMenu(menuID);
        if (null != menu) {
            MenuData menuData = menu.getMenuData();
            if (menuData != null) {
                menuData.redraw();
            }
        }
    }
}
