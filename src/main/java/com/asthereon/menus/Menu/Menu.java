package com.asthereon.menus.Menu;

import com.asthereon.menus.Buttons.MenuButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A Menu handles the assembly of the display of {@link MenuButton MenuButtons} and {@link MenuSection MenuSections} in
 *  a {@link MenuInventory}. Menus also allow for binding code to run when a menu is loaded (opened), saved (closed),
 *  as well as how to handle items on the cursor when the menu is closed and the itemscannot be returned to the player's
 *  inventory.
 */
public class Menu {

    private MenuInventory inventory;
    private final UUID uuid;
    private final InventoryCondition readOnly;
    private final List<MenuButton> buttons;
    private final List<MenuButton> menuPlaceholders;
    private final HashMap<String, MenuSection> sections;
    private final List<BiConsumer<MenuView, String>> onSave = new ArrayList<>();
    private final List<Consumer<MenuView>> onLoad = new ArrayList<>();
    private BiConsumer<MenuView, ItemStack> menuCursorItemOverflow = null;
    private BiConsumer<Player, ItemStack> defaultCursorItemOverflow = CursorOverflow.getCursorOverflowHandler(CursorOverflowType.DEFAULT);
    private final Data metadata;

    // TODO: 4/19/2021 Maybe add an error that's thrown when trying to build a menu with read only slots that just have air?
    // TODO: 4/19/2021 Try making all buttons have a StackingRule max stack size of 1, see if it still lets you have larger stacks by direct setting
    public Menu(UUID uuid, Data metadata, MenuInventory inventory, InventoryCondition readOnly, List<MenuButton> buttons, HashMap<String, MenuSection> sections, List<MenuButton> menuPlaceholders, String storageData) {
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

    protected void drawButton(MenuButton menuButton) {
        for (int slot : menuButton.getSlots()) {
            inventory.setItemStack(slot, menuButton.getItemStack());
        }

        for (InventoryCondition inventoryCondition : menuButton.getInventoryConditions()) {
            inventory.addInventoryCondition(inventoryCondition);
        }
    }

    protected void drawPlaceholder(MenuButton menuPlaceholder) {
        ItemStack itemStack = menuPlaceholder.getMetadata("placeholder",ItemStack.AIR);
        for (int slot : menuPlaceholder.getSlots()) {
            if (inventory.getItemStack(slot).isAir()) {
                inventory.setItemStack(slot, itemStack);
            }
        }
    }

    protected void clearSlot(int slot) {
        inventory.setItemStack(slot, ItemStack.AIR);
    }

    protected void clearSlots(Collection<Integer> slots) {
        for (int slot : slots) {
            inventory.setItemStack(slot, ItemStack.AIR);
        }
    }

    public void redraw() {
        Set<Player> players = inventory.getViewers();
        List<Integer> storageSlots = inventory.getStorageSlots();
        String serializedData = inventory.serialize();

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

        // Draw placeholders last to avoid stomping on buttons
        for (MenuButton menuPlaceholder : menuPlaceholders) {
            drawPlaceholder(menuPlaceholder);
        }

        for (Player player : players) {
            player.openInventory(inventory);
        }
    }

    public void open(Player player) {
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

    protected void closeEvent(Player player) {
        if (inventory.hasPersistentData()) {
            String serializedData = inventory.serialize();
            save(player, serializedData);
        }
    }

    public boolean isPlayerViewing(Player player) {
        return inventory.getViewers().contains(player);
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

