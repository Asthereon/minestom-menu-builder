package com.asthereon.menus.Menu;

import com.asthereon.menus.Buttons.MenuButton;
import com.asthereon.menus.Buttons.MenuPlaceholder;
import com.asthereon.menus.Buttons.PageButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Menu {

    private MenuInventory inventory;
    private final UUID uuid;
    private final InventoryCondition readOnly;
    private final List<MenuButton> buttons;
    private final List<MenuPlaceholder> menuPlaceholders;
    private final HashMap<String, MenuSection> sections;
    private final List<BiConsumer<MenuView, String>> onSave = new ArrayList<>();
    private final List<Consumer<MenuView>> onLoad = new ArrayList<>();
    private BiConsumer<MenuView, ItemStack> menuCursorItemOverflow = null;
    private BiConsumer<Player, ItemStack> defaultCursorItemOverflow = CursorOverflow.getCursorOverflowHandler(CursorOverflowType.DEFAULT);
    private final Data metadata;

    // TODO: 4/19/2021 Maybe add an error that's thrown when trying to build a menu with read only slots that just have air?
    // TODO: 4/19/2021 Try making all buttons have a StackingRule max stack size of 1, see if it still lets you have larger stacks by direct setting
    public Menu(UUID uuid, Data metadata, MenuInventory inventory, InventoryCondition readOnly, List<MenuButton> buttons, HashMap<String, MenuSection> sections, List<MenuPlaceholder> menuPlaceholders, String storageData) {
        this.uuid = uuid;
        this.metadata = metadata;
        this.inventory = inventory;
        this.readOnly = readOnly;
        this.buttons = buttons;
        this.sections = sections;
        this.sections.forEach((name,section) -> section.setMenu(this.getUuid()));
        this.menuPlaceholders = menuPlaceholders;
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            if (storageData != null) {
                this.inventory.deserialize(storageData);
            }
        }).delay(1, TimeUnit.TICK).schedule();
        MenuManager.register(this);
    }

    public void drawButton(MenuButton menuButton) {
        for (int slot : menuButton.getSlots()) {
            inventory.setItemStack(slot, menuButton.getItemStack());
        }

        for (InventoryCondition inventoryCondition : menuButton.getInventoryConditions()) {
            inventory.addInventoryCondition(inventoryCondition);
        }
    }

    public void drawButton(PageButton pageButton) {
        for (int slot : pageButton.getSlots()) {
            inventory.setItemStack(slot, pageButton.getItemStack());
        }

        for (InventoryCondition inventoryCondition : pageButton.getInventoryConditions()) {
            inventory.addInventoryCondition(inventoryCondition);
        }
    }

    public void drawPlaceholder(MenuPlaceholder menuPlaceholder) {
        for (int slot : menuPlaceholder.getSlots()) {
            if (inventory.getItemStack(slot).isAir()) {
                inventory.setItemStack(slot, menuPlaceholder.getItemStack());
            }
        }
    }

    public void clearSlot(int slot) {
        inventory.setItemStack(slot, ItemStack.AIR);
    }

    public void clearSlots(Collection<Integer> slots) {
        for (int slot : slots) {
            inventory.setItemStack(slot, ItemStack.AIR);
        }
    }

    public void reset() {
        inventory.clear();
        inventory.clearInventoryConditions();
    }

    public void redraw() {
        Set<Player> players = inventory.getViewers();
        List<Integer> storageSlots = inventory.storageSlots;
        String serializedData = inventory.serialize();

        // TODO: 4/20/2021 It looks like this close inventory event combined with the one in Menu.open() are causing double saves
//        for (Player player : players) {
//            MenuManager.closeInventoryEvent(player, player.getOpenInventory());
//        }

        // Make sure non-button items are copied over to the new inventory or stuff like banks will be impossible
        inventory = new MenuInventory(inventory.getInventoryType(), inventory.getTitle());
        inventory.storageSlots(storageSlots);
        inventory.deserialize(serializedData);

        if (readOnly != null) {
            inventory.addInventoryCondition(readOnly);
        }

        for (MenuButton button : buttons) {
            drawButton(button);
        }

        for (MenuSection section : sections.values()) {
            section.draw();
        }

        // Change the drawing of placeholders to be after menu sections, and check for air,
        // so they can be used to replace the page buttons when they aren't displayed
        for (MenuPlaceholder menuPlaceholder : menuPlaceholders) {
            drawPlaceholder(menuPlaceholder);
        }

        for (Player player : players) {
            player.openInventory(inventory);
        }
    }

    public void open(Player player) {
        //MenuManager.closeInventoryEvent(player, player.getOpenInventory());
        // TODO: 4/20/2021 Need to differentiate between a close due to another menu opening, and closing current menu, so that closing the current menu will save the current tab
        load(player);
        redraw();
        player.openInventory(inventory);
    }

    public String close(Player player) {
        Inventory openInventory = player.getOpenInventory();
        String serializedData = null;
        if (openInventory != null) {
            if (openInventory.getTitle().equals(inventory.getTitle())) {
                if (inventory.hasPersistentData()) {
                    serializedData = inventory.serialize();
                    save(player, serializedData);
                }
                player.closeInventory();
            }
        }
        return serializedData;
    }

    public void bindToSave(BiConsumer<MenuView, String> saveFunction) {
        onSave.add(saveFunction);
    }

    private void save(Player player, String serializedData) {
        MenuView menuView = new MenuView(this, player);
        for (BiConsumer<MenuView, String> saveFunction : onSave) {
            saveFunction.accept(menuView, serializedData);
        }
    }

    public void bindToLoad(Consumer<MenuView> loadFunction) {
        onLoad.add(loadFunction);
    }

    private void load(Player player) {
        MenuView menuView = new MenuView(this, player);
        for (Consumer<MenuView> loadFunction : onLoad) {
            loadFunction.accept(menuView);
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

    public boolean isPlayerViewing(Player player) {
        return inventory.getViewers().contains(player);
    }

    public String closeEvent(Player player) {
        String serializedData = null;
        if (inventory.hasPersistentData()) {
            serializedData = inventory.serialize();
            save(player, serializedData);
        }
        return serializedData;
    }

    public MenuInventory getInventory() {
        return inventory;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Data getMetadata() {
        return metadata;
    }

    public <T> T getMetadata(String key, T defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }

    public <T> void setMetadata(String key, T value, Class<T> type) { metadata.set(key, value, type); }

    public <T> void setMetadata(String key, T value) { metadata.set(key, value); }
}

