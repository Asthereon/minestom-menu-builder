package com.asthereon.menus;

import net.minestom.server.entity.Player;

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

    public <T> T getMetadata(String key, T defaultValue) {
        return this.menu.getMetadata(key, defaultValue);
    }

    public <T> void setMetadata(String key, T value, Class<T> type) { this.menu.setMetadata(key, value, type); }

    public <T> void setMetadata(String key, T value) { this.menu.setMetadata(key, value); }
}
