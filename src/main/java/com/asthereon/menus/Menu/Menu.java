package com.asthereon.menus.Menu;

import com.asthereon.menus.Utils.Metadata;
import net.minestom.server.entity.Player;

import javax.annotation.Nullable;

/**
 * All Menus that use this system extend must extend this class, which provides some boilerplate code such as registering
 *  the new Menu with the MenuManager and the MenuData that provides the data for the Menu.
 */
abstract public class Menu {

    // The menudata that provides the server-side data that is needed for menus
    private MenuData menuData;

    /**
     * Opens the menu to the player with the supplied metadata
     * @param player the player to open the menu to
     * @param metadata the metadata to apply to the menu view
     */
    public void open(Player player, @Nullable Metadata metadata) {
        menuData.open(player, metadata);
    }

    /**
     * Default constructor that initializes the menu by adding it to the MenuManager and building the {@link MenuData}
     */
    public Menu() {
        initialize();
    }

    // Initialization that builds the MenuData
    private void initialize() {
        if (menuData == null) {
            menuData = buildMenu();
            MenuManager.register(this);
        }
    }

    /**
     * This method is used to generate the {@link MenuData} for this menu, including buttons, placeholders, save and load
     *  functionality, and cursor item overflow handling.
     * @return the MenuData for the menu
     */
    abstract protected MenuData buildMenu();

    /**
     * This is the string value that is used with {@link MenuManager#getMenu(String)} to fetch the appropriate menu
     * @return the menu ID
     */
    abstract public String getMenuID();

    /**
     * Gets the {@link MenuData} associated with this menu.  Generally you should avoid touching this unless you have a
     *  good reason to, as it can break some of the internals if not careful.
     * @return the MenuData for this menu
     */
    public MenuData getMenuData() {
        return menuData;
    }
}
