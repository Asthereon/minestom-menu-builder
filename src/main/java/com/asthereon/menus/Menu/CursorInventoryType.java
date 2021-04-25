package com.asthereon.menus.Menu;

/**
 * This enum represents which inventory the cursor is stored on
 */
public enum CursorInventoryType {
    MENU,               // A menu handled by this menu system
    PLAYER_INVENTORY,   // A player's personal inventory
    INVENTORY,          // An inventory not handled by this menu system
    NONE,               // Indicates neither the open inventory or player's inventory has a cursor item
    UNKNOWN             // Indicates lack of information about which type of cursor is active
}
