package com.asthereon.menus;

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

public class MenuManager {

    // Instance
    private static MenuManager instance = new MenuManager();

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

    public static void init() {

    }

    public static void closeInventoryEvent(Player player, Inventory inventory) {
        if (inventory != null) {
            if (MenuManager.getInstance().isMenu(player,inventory.getTitle())) {
                //System.out.println("MENU CLOSED");
                Menu menu = MenuManager.getInstance().getMenu(player);
                if (null != menu) {
                    String serializedData = menu.closeEvent(player);
                    //System.out.println("CLOSE EVENT: "+serializedData);
                }
            } else {
                //System.out.println("NON-MENU CLOSED");
            }
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

    public Menu _unregister(UUID uuid) {
        return menus.remove(uuid);
    }

    public boolean isMenu(Player player, Component menuName) {
        AtomicBoolean isMenu = new AtomicBoolean(false);
        menus.forEach((uuid, menu) -> {
            if (menu.getInventory().getViewers().contains(player)) {
                if (menu.getTitle().equals(menuName)) {
                    isMenu.set(true);
                }
            }
        });

        return isMenu.get();
    }

    @Nullable
    public Menu getMenu(UUID uuid) {
        return menus.getOrDefault(uuid, null);
    }

    @Nullable
    public Menu getMenu(Player player) {
        for (Map.Entry<UUID, Menu> entry : menus.entrySet()) {
            if (entry.getValue().isPlayerViewing(player)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void redraw(UUID uuid) {
        Menu menu = getInstance().getMenu(uuid);
        if (null != menu) {
            menu.redraw();
        }
    }
}
