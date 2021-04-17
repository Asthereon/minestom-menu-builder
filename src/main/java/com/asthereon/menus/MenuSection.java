package com.asthereon.menus;

import com.asthereon.menus.Buttons.MenuButton;
import com.asthereon.menus.Buttons.PageButton;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MenuSection {

    private String name;
    private UUID menuID;
    private List<Integer> slots = new ArrayList<>();
    private List<MenuButton> buttons = new ArrayList<>();
    private List<PageButton> pageButtons = new ArrayList<>();
    private List<Integer> pageButtonSlots = new ArrayList<>();
    private int offset = 0;
    private int minimumVisible = 1;

    private MenuSection(String name) {
        this.name = name;
    }

    static public MenuSection named(String name) {
        return new MenuSection(name);
    }

    public MenuSection slot(int slot) {
        this.slots.add(slot);
        return this;
    }

    public MenuSection slots(Collection<Integer> slots) {
        this.slots.addAll(slots);
        return this;
    }

    public MenuSection slotRange(int minSlotID, int maxSlotID) {
        for (int slotID = minSlotID; slotID <= maxSlotID; slotID++) {
            this.slots.add(slotID);
        }
        return this;
    }

    public MenuSection minimumVisible(int minimumVisible) {
        this.minimumVisible = minimumVisible;
        return this;
    }

    public MenuSection button(MenuButton menuButton) {
        this.buttons.add(menuButton);
        return this;
    }

    public MenuSection offset(int offset) {
        setOffset(offset);
        return this;
    }

    public void setOffset(int offset) {
        this.offset = Math.min(0, Math.max(offset, buttons.size() - minimumVisible));
        MenuManager.redraw(menuID);
    }

    public MenuSection pageButton(int slot, int offset, ItemStack itemStack) {
        if (pageButtonSlots.contains(slot)) {
            System.out.println("[Menu] MenuSection " + name + " tried to bind a page button to an existing slot");
            return this;
        }
        // Mark this slot as used by a page button
        pageButtonSlots.add(slot);
        this.pageButtons.add(PageButton
                .slot(slot)
                .itemStack(itemStack)
                .offset(offset)
                .click(() -> {
                    setOffset(this.offset + offset);
                })
        );
        return this;
    }

    public void reset(Menu menu) {
        for (int slot : slots) {
            menu.clearSlot(slot);
        }
    }

    public void draw() {
        Menu menu = MenuManager.getInstance().getMenu(menuID);
        if (null != menu) {
            if (slots.size() > 0) {
                if (buttons.size() > 0) {
                    int index = offset;
                    for (int slot : slots) {
                        if (index < buttons.size()) {
                            MenuButton menuButton = buttons.get(index);
                            menuButton.setSlot(slot);
                            menu.drawButton(menuButton);
                            index++;
                        } else {
                            menu.clearSlot(slot);
                        }
                    }
                }
            }
            for (PageButton pageButton : this.pageButtons) {
                // IF the button is to decrement and there's more stuff to show
                if (pageButton.isDecrement()) {
                    if (offset > 0) {
                        menu.drawButton(pageButton);
                    } else {
                        menu.clearSlots(pageButton.getSlots());
                    }
                }
                // ELSE IF the button is to increment and there's more stuff to show
                else if (pageButton.isIncrement()) {
                    if (offset < buttons.size() - minimumVisible) {
                        menu.drawButton(pageButton);
                    } else {
                        menu.clearSlots(pageButton.getSlots());
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    public void setMenu(UUID menuID) {
        this.menuID = menuID;
    }

    public List<Integer> getPageButtonSlots() {
        return pageButtonSlots;
    }
}
