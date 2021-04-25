package com.asthereon.menus.Menu;

/**
 * This enum indicates what should be done with items on the cursor that cannot fit in the player's inventory
 */
public enum CursorOverflowType {
    DESTROY,    // Destroys the item on the cursor completely
    DROP,       // Drops the item in front of the player
    MENU,       // Defers the handling of the item to the menu's cursor handling
    CUSTOM,     // Re-usable custom handler
    DEFAULT     // Default item handler (defaults to DROP but can be changed)
}
