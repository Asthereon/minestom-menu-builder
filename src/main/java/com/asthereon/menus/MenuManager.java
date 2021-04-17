package com.asthereon.menus;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

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
    private MenuManager() { }

    // Get Instance of class
    public static MenuManager getInstance() {
        return instance;
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
