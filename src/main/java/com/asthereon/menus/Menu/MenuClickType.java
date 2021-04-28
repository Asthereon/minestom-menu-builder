package com.asthereon.menus.Menu;

import java.util.function.BiConsumer;

/**
 * This enum indicates types of clicks that are supported by {@link com.asthereon.menus.Buttons.MenuButton#click(MenuClickType, BiConsumer)}
 *  to apply specific actions to specific click types.
 */
public enum MenuClickType {
    ALL,
    LEFT_CLICK,
    RIGHT_CLICK,
    CHANGE_HELD,
    START_SHIFT_CLICK,
    START_DOUBLE_CLICK,
    DROP
}