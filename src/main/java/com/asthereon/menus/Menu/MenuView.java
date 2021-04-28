package com.asthereon.menus.Menu;

import net.minestom.server.entity.Player;

/**
 * This class represents a player and the {@link Menu} they are viewing, as a simplified argument to pass to Menu
 *  events like onLoad(), onSave() and onCursorItemOverflow()
 */
public class MenuView {

    private final Menu menu;
    private final Player player;

    public MenuView(Menu menu, Player player) {
        this.menu = menu;
        this.player = player;
    }

    public Menu getMenu() {
        return menu;
    }

    public Player getPlayer() {
        return player;
    }

    // Pass-through methods to make Menu metadata easier to access
    public <T> T getMetadata(String key, T defaultValue) { return this.menu.getMetadata(key, defaultValue); }
    public <T> void setMetadata(String key, T value, Class<T> type) { this.menu.setMetadata(key, value, type); }
    public <T> void setMetadata(String key, T value) { this.menu.setMetadata(key, value); }
}
