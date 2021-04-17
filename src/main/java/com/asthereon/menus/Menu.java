package com.asthereon.menus;

import com.asthereon.menus.Buttons.MenuButton;
import com.asthereon.menus.Buttons.MenuPlaceholder;
import com.asthereon.menus.Buttons.PageButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;

import java.util.*;
import java.util.function.Consumer;

public class Menu {

    private MenuInventory inventory;
    private final UUID uuid;
    private InventoryCondition readOnly;
    private List<MenuButton> buttons;
    private List<MenuPlaceholder> menuPlaceholders;
    private HashMap<String, MenuSection> sections;
    private List<Consumer<String>> onSave = new ArrayList<>();
    private List<Consumer<Player>> onLoad = new ArrayList<>();
    private boolean dirty = true;


    public Menu(MenuInventory inventory, InventoryCondition readOnly, List<MenuButton> buttons, HashMap<String, MenuSection> sections, List<MenuPlaceholder> menuPlaceholders, String storageData) {
        this.uuid = UUID.randomUUID();
        this.inventory = inventory;
        this.readOnly = readOnly;
        this.buttons = buttons;
        this.sections = sections;
        this.sections.forEach((name,section) -> {
            section.setMenu(this.getUuid());
        });
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

        for (Player player : players) {
            MenuManager.closeInventoryEvent(player, player.getOpenInventory());
        }

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

        dirty = false;
    }

    public void open(Player player) {
        MenuManager.closeInventoryEvent(player, player.getOpenInventory());
        load(player);
        if (dirty) {
            redraw();
        }
        player.openInventory(inventory);
    }

    public String close(Player player) {
        Inventory openInventory = player.getOpenInventory();
        String serializedData = null;
        if (openInventory != null) {
            if (openInventory.getTitle().equals(inventory.getTitle())) {
                if (inventory.hasPersistentData()) {
                    serializedData = inventory.serialize();
                    save(serializedData);
                }
                player.closeInventory();
            }
        }
        return serializedData;
    }

    public void bindToSave(Consumer<String> saveFunction) {
        onSave.add(saveFunction);
    }

    private void save(String serializedData) {
        for (Consumer<String> saveFunction : onSave) {
            saveFunction.accept(serializedData);
        }
    }

    public void bindToLoad(Consumer<Player> loadFunction) {
        onLoad.add(loadFunction);
    }

    private void load(Player player) {
        for (Consumer<Player> loadFunction : onLoad) {
            loadFunction.accept(player);
        }
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
            save(serializedData);
        }
        return serializedData;
    }

    public MenuInventory getInventory() {
        return inventory;
    }

    public UUID getUuid() {
        return uuid;
    }
}

