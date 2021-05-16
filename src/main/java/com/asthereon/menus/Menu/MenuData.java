package com.asthereon.menus.Menu;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Enums.CursorOverflowType;
import com.asthereon.menus.Utils.Metadata;
import com.asthereon.menus.Utils.MetadataContainer;
import net.kyori.adventure.text.Component;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A {@link MenuData} handles the assembly of {@link MenuButton MenuButtons} and {@link MenuSection MenuSections} in
 *  a {@link MenuInventory}. Menus also allow for binding code to run when a menu is loaded (opened), saved (closed),
 *  as well as how to handle items on the cursor when the menu is closed and the itemscannot be returned to the player's
 *  inventory.
 */
public class MenuData extends MetadataContainer {

    private final HashMap<UUID, MenuView> views = new HashMap<>(); // A map of player UUIDs to the player's view of the menu
    private MenuInventory inventory;
    private final String menuID;
    private final InventoryCondition readOnly;
    private final List<MenuButton> buttons;
    private final List<MenuButton> menuPlaceholders;
    private final HashMap<String, MenuSection> sections;
    private final List<BiConsumer<MenuView, String>> onSave;
    private final List<Consumer<MenuView>> onLoad;
    private BiConsumer<MenuView, ItemStack> menuCursorItemOverflow = null;
    private BiConsumer<Player, ItemStack> defaultCursorItemOverflow = CursorOverflow.getCursorOverflowHandler(CursorOverflowType.DEFAULT);

    // TODO: 4/19/2021 Maybe add an error that's thrown when trying to build a menu with read only slots that just have air?
    // TODO: 4/19/2021 Try making all buttons have a StackingRule max stack size of 1, see if it still lets you have larger stacks by direct setting
    public MenuData(String menuID, Metadata metadata, MenuInventory inventory,
                    InventoryCondition readOnly, List<MenuButton> buttons,
                    HashMap<String, MenuSection> sections, List<MenuButton> menuPlaceholders,
                    List<BiConsumer<MenuView, String>> onSave, List<Consumer<MenuView>> onLoad) {
        this.menuID = menuID;
        this.metadata = metadata;
        this.inventory = inventory;
        this.readOnly = readOnly;
        this.buttons = buttons;
        this.sections = sections;
        this.sections.forEach((name,section) -> section.setMenu(this.getMenuID()));
        this.menuPlaceholders = menuPlaceholders;
        this.onSave = onSave;
        this.onLoad = onLoad;
    }

    public void redraw() {
        for (MenuView view : views.values()) {
            view.redraw();
        }
    }

    public void redraw(Player player) {
        MenuView menuView = getOrCreateMenuView(player);
        menuView.redraw();
    }

    public MenuView getMenuView(Player player) {
        return views.get(player.getUuid());
    }

    public MenuView getOrCreateMenuView(Player player) {
        return views.getOrDefault(player.getUuid(), new MenuView(this, player));
    }

    private void setUpView(Player player, @Nullable Metadata metadata) {
        MenuView menuView = this.getOrCreateMenuView(player);

        // Reset the tab info back to the defaults if the metadata is null
        AsthCore.console(this.getMetadata().toString());
        Metadata original = this.getMetadata();
        original.set("test", "1");
        menuView.setMetadata(Objects.requireNonNullElseGet(metadata, () -> this.getMetadata().clone()));
        AsthCore.console(menuView.getMetadata().toString());
        menuView.setMetadata("test", "2");
        AsthCore.console(original.get("test"));


        views.put(player.getUuid(), menuView);
    }

    /**
     * Opens the {@link MenuInventory} to the player, triggering a redraw if the Menu has never been drawn
     * @param player the player to open the menu to
     */
    public void open(Player player, @Nullable Metadata metadata) {
        setUpView(player, metadata);
        redraw(player);
        player.openInventory(getMenuView(player).getInventory());
        load(player);
    }

    /**
     * Closes this menu
     * @param player the player
     * @return the serialized persistent data
     */
    public String close(Player player) {
        Inventory openInventory = player.getOpenInventory();
        String serializedData = null;

        MenuView menuView = views.get(player.getUuid());
        if (menuView != null) {
            MenuInventory menuInventory = menuView.getInventory();
            if (openInventory != null) {
                if (openInventory.equals(menuInventory)) {
                    if (menuInventory.hasPersistentData()) {
                        serializedData = menuInventory.serialize();
                        save(player, serializedData);
                    }
                    player.closeInventory();
                }
            }
        }
        return serializedData;
    }

    public void bindToSave(BiConsumer<MenuView, String> saveFunction) {
        onSave.add(saveFunction);
    }

    private void save(Player player, String serializedData) {
        MenuView menuView = getMenuView(player);
        if (menuView != null) {
            for (BiConsumer<MenuView, String> saveFunction : onSave) {
                saveFunction.accept(menuView, serializedData);
            }
        }
    }

    public void bindToLoad(Consumer<MenuView> loadFunction) {
        onLoad.add(loadFunction);
    }

    private void load(Player player) {
        MenuView menuView = getMenuView(player);
        if (menuView != null) {
            for (Consumer<MenuView> loadFunction : onLoad) {
                loadFunction.accept(menuView);
            }
        }
    }

    // Sets the cursor item overflow handler to be menu specific
    public void bindToCursorItemOverflow(BiConsumer<MenuView, ItemStack> cursorItemOverflowFunction) {
        defaultCursorItemOverflow = null;
        menuCursorItemOverflow = cursorItemOverflowFunction;
    }

    // Sets the cursor item overflow handler to be a generic type that can't access menu specific data
    public void bindToCursorItemOverflow(CursorOverflowType cursorOverflowType) {
        menuCursorItemOverflow = null;
        defaultCursorItemOverflow = CursorOverflow.getCursorOverflowHandler(cursorOverflowType);
    }

    protected void closeEvent(Player player) {
        MenuView menuView = views.get(player.getUuid());
        if (menuView != null) {
            MenuInventory menuInventory = menuView.getInventory();
            if (menuInventory.hasPersistentData()) {
                String serializedData = menuInventory.serialize();
                save(player, serializedData);
            }
        }
    }

    public boolean isPlayerViewing(Player player) {
        MenuView menuView = getMenuView(player);
        if (menuView != null) {
            return menuView.getInventory().getViewers().contains(player);
        }
        return false;
    }

    protected boolean isMenuCursorItemOverflow() {
        return menuCursorItemOverflow != null;
    }

    protected boolean isDefaultCursorItemOverflow() {
        return defaultCursorItemOverflow != null;
    }

    protected BiConsumer<MenuView, ItemStack> getMenuCursorItemOverflow() {
        return menuCursorItemOverflow;
    }

    protected BiConsumer<Player, ItemStack> getDefaultCursorItemOverflow() {
        return defaultCursorItemOverflow;
    }

    public Component getTitle() {
        return inventory.getTitle();
    }

    public MenuInventory getInventory() {
        return inventory;
    }

    public InventoryType getInventoryType() { return this.inventory.getInventoryType(); }

    public String getMenuID() {
        return menuID;
    }

    public InventoryCondition getReadOnly() {
        return readOnly;
    }

    public List<MenuButton> getButtons() {
        return buttons;
    }

    public List<MenuButton> getMenuPlaceholders() {
        return menuPlaceholders;
    }

    public HashMap<String, MenuSection> getSections() {
        return sections;
    }

    public MenuSection getSection(String sectionName) {
        return sections.get(sectionName);
    }
}

