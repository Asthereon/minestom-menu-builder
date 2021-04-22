package com.asthereon.menus;

import com.asthereon.menus.Buttons.MenuButton;
import com.asthereon.menus.Buttons.MenuPlaceholder;
import net.kyori.adventure.text.Component;
import net.minestom.server.data.Data;
import net.minestom.server.data.DataImpl;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.condition.InventoryCondition;
import net.minestom.server.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class MenuBuilder {

    private final MenuInventory inventory;
    private String storageData = null;
    private boolean readOnly = false;
    private UUID uuid;
    private List<Integer> readOnlySlots = new ArrayList<>();
    private List<MenuButton> buttons = new ArrayList<>();
    private List<MenuPlaceholder> menuPlaceholders = new ArrayList<>();
    private HashMap<String, MenuSection> sections = new HashMap<>();
    private Data metadata = new DataImpl();

    public MenuBuilder(InventoryType inventoryType, Component title) {
        this.inventory = new MenuInventory(inventoryType, title);
        this.uuid = UUID.randomUUID();
    }

    public static MenuBuilder of(InventoryType inventoryType, Component title) {
        return new MenuBuilder(inventoryType, title);
    }

    public MenuBuilder readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public MenuBuilder readOnlySlot(int slot) {
        this.readOnlySlots.add(slot);
        return this;
    }

    public MenuBuilder readOnlySlots(Integer... slots) {
        this.readOnlySlots.addAll(Arrays.asList(slots));
        return this;
    }

    public MenuBuilder readOnlySlots(Collection<Integer> slots) {
        this.readOnlySlots.addAll(slots);
        return this;
    }

    public MenuBuilder readOnlyRange(int min, int max) {
        for (int i = min; i <= max; i++) {
            this.readOnlySlots.add(i);
        }
        return this;
    }

    public MenuBuilder metadata(Data metadata) {
        if (metadata != null) {
            this.metadata = metadata;
        }
        return this;
    }

    public MenuBuilder bindToSlot(int slotID, Consumer<Menu> callback) {
        this.inventory.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            if (slot == slotID) {
                player.sendMessage(Component.text(clickType.toString()));
                Menu menu = MenuManager.getMenu(uuid);
                if (null != menu) {
                    callback.accept(menu);
                }
            }
        });
        return this;
    }

    public MenuBuilder bindToSlot(int slotID, MenuClickType menuClickType, Consumer<Menu> callback) {
        this.inventory.addInventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slot == slotID) {
                if (menuClickType.toString().equals(clickType.toString())) {
                    Menu menu = MenuManager.getMenu(uuid);
                    if (null != menu) {
                        callback.accept(menu);
                    }
                }
            }
        }));
        return this;
    }

    public MenuBuilder bindToSlotRange(int minimumSlotID, int maximumSlotID, Consumer<Menu> callback) {
        this.inventory.addInventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slot >= minimumSlotID && slot <= maximumSlotID) {
                Menu menu = MenuManager.getMenu(uuid);
                if (null != menu) {
                    callback.accept(menu);
                }
            }
        }));
        return this;
    }

    public MenuBuilder bindToSlotRange(int minimumSlotID, int maximumSlotID, MenuClickType menuClickType, Consumer<Menu> callback) {
        this.inventory.addInventoryCondition(((player, slot, clickType, inventoryConditionResult) -> {
            if (slot >= minimumSlotID && slot <= maximumSlotID) {
                if (menuClickType.toString().equals(clickType.toString())) {
                    Menu menu = MenuManager.getMenu(uuid);
                    if (null != menu) {
                        callback.accept(menu);
                    }
                }
            }
        }));
        return this;
    }

    public MenuBuilder setItem(int slotID, ItemStack itemStack) {
        this.inventory.setItemStack(slotID, itemStack);
        return this;
    }

    public MenuBuilder button(int slotID, Runnable callback) {
        this.inventory.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            if (slot == slotID) {
                player.sendMessage(Component.text(clickType.toString()));
                callback.run();
            }
        });
        return this;
    }

    public MenuBuilder button(MenuButton menuButton) {
        buttons.add(menuButton);
        return this;
    }

    public MenuBuilder placeholder(MenuPlaceholder menuPlaceholder) {
        menuPlaceholders.add(menuPlaceholder);
        return this;
    }

    public MenuBuilder section(MenuSection menuSection) {
        sections.put(menuSection.getName(), menuSection);
        return this;
    }

    public MenuBuilder storageData(String storageData) {
        this.storageData = storageData;
        return this;
    }

    public MenuButton createMenuButton() {
        return MenuButton.on(uuid);
    }

    public Menu build() {
        InventoryCondition readOnlyCondition = null;

        if (readOnly) {
            readOnlyCondition = (player, slot, clickType, inventoryConditionResult) -> {
                inventoryConditionResult.setCancel(true);
            };
        } else {
            if (readOnlySlots.size() > 0) {
                readOnlyCondition = (player, slot, clickType, inventoryConditionResult) -> {
                    if (readOnlySlots.contains(slot)) {
                        inventoryConditionResult.setCancel(true);
                    }
                };
            }
        }

        Menu menu = new Menu(uuid, metadata, inventory, readOnlyCondition, buttons, sections, menuPlaceholders, storageData);

        if (!readOnly) {
            // IF there are read only slots, that means there should be storage slots so set those on the menu
            MenuInventory menuInventory = menu.getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (!readOnlySlots.contains(i)) {
                    menuInventory.storageSlot(i);
                }
            }
        }

        return menu;
    }
}
