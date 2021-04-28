package com.asthereon.menus.Menu;

import com.asthereon.menus.Buttons.MenuButton;
import com.asthereon.menus.Buttons.PageButton;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A MenuSection is an automatically paginated group of slots on a {@link Menu} which takes a list of {@link MenuButton}
 *  that will be displayed in the list of slots assigned to this MenuSection. The MenuButtons displayed in the slots are
 *  controlled by an offset which is modified by adding page buttons that increment and decrement the offset.
 */
public class MenuSection {

    private final String name;
    private UUID menuID;
    private final List<Integer> slots = new ArrayList<>();
    private final List<MenuButton> buttons = new ArrayList<>();
    private final List<PageButton> pageButtons = new ArrayList<>();
    private final List<Integer> pageButtonSlots = new ArrayList<>();
    private int offset = 0;
    private int minimumVisible = 1;

    private MenuSection(String name) {
        this.name = name;
    }

    static public MenuSection named(String name) {
        return new MenuSection(name);
    }

    /**
     * Add a single slot to the list of slots for this MenuSection
     * @param slot the slot to add
     * @return this MenuSection
     */
    public MenuSection slot(int slot) {
        this.slots.add(slot);
        return this;
    }

    /**
     * Add a collection of slots to the list of slots for this MenuSection
     * @param slots the slot to add
     * @return this MenuSection
     */
    public MenuSection slots(Collection<Integer> slots) {
        this.slots.addAll(slots);
        return this;
    }

    /**
     * Add all slots between minSlotID and maxSlotID inclusive to the list of slots for this MenuSection
     * @param minSlotID the minimum slot of the range to add
     * @param maxSlotID the maximum slot of the range to add
     * @return this MenuSection
     */
    public MenuSection slotRange(int minSlotID, int maxSlotID) {
        for (int slotID = minSlotID; slotID <= maxSlotID; slotID++) {
            this.slots.add(slotID);
        }
        return this;
    }

    /**
     * The minimum numbers of MenuButtons to require be shown when the MenuSection is offset to the end of the list of
     *  MenuButtons. Set to 0 to allow scrolling until no buttons are shown, set to the number of total slots to
     *  prevent any empty slots from being shown.
     * @param minimumVisible the minimum number of buttons to show
     * @return this MenuSection
     */
    public MenuSection minimumVisible(int minimumVisible) {
        this.minimumVisible = minimumVisible;
        return this;
    }

    /**
     * Add a {@link MenuButton} to the list of buttons that will be shown in this MenuSection.
     * @param menuButton the button to add to the list
     * @return this MenuSection
     */
    public MenuSection button(MenuButton menuButton) {
        this.buttons.add(menuButton);
        return this;
    }

    /**
     * Set the default number of buttons to offset the MenuSection by, with the minimum value being 0 for the beginning
     *  of the MenuSection, and a maximum value of the number of buttons minus the minimum number of visible buttons.
     * @param offset the number of buttons to offset by
     * @return this MenuSection
     */
    public MenuSection offset(int offset) {
        setOffset(offset);
        return this;
    }

    /**
     * Set the number of buttons to offset the MenuSection by, with the minimum value being 0 for the beginning
     *  of the MenuSection, and a maximum value of the number of buttons minus the minimum number of visible buttons.
     * @param offset the number of buttons to offset by
     */
    public void setOffset(int offset) {
        this.offset = Math.min(0, Math.max(offset, buttons.size() - minimumVisible));
        MenuManager.redraw(menuID);
    }

    /**
     * Adds a button that will increment or decrement the offset to change what items are displayed within the
     *  MenuSection
     * @param slot the slot to turn into a button
     * @param offset the amount to change the offset by, negative to display earlier items, positive to display later items
     * @param itemStack the item stack the button should display as
     * @return this MenuSection
     */
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
                .click(() -> setOffset(this.offset + offset))
        );
        return this;
    }

    /**
     * Sets all the slots associated with the MenuSection display to AIR
     */
    public void clearSlots() {
        Menu menu = MenuManager.getMenu(menuID);
        if (null != menu) {
            menu.clearSlots(slots);
        }
    }

    /**
     * Draws the MenuButtons for the MenuSection, as well as the buttons for incrementing and decrementing the offset
     */
    protected void draw() {
        Menu menu = MenuManager.getMenu(menuID);
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
                        // TODO: 4/27/2021 Update this to check the button's metadata for "placeholder" 
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

    public String getName() { return name; }
    public int getOffset() { return offset; }
    public void setMenu(UUID menuID) { this.menuID = menuID; }
    public List<Integer> getPageButtonSlots() { return pageButtonSlots; }
}
