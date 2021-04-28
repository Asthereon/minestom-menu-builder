package com.asthereon.menus.Menu;

import net.minestom.server.inventory.InventoryType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A MenuSchema allows for simplified visual layouts to be created for a {@link Menu}. A MenuSchema may contain any
 *  characters within the masks, and those characters will represent a group of slots. {@link MenuSchema#getSlots(char)}
 *  accepts a character to search for, and will return all slot indexes that match the given character. A single
 *  MenuSchema can provide the slot indexes for each character used to build it, making it a convenient way to lay out
 *  all {@link com.asthereon.menus.Buttons.MenuButton MenuButtons} which will be displayed
 */
public class MenuSchema {

    private final int slotCount;    // The number of slots in the inventory type
    private int markerSlot = 0;     // The slot of the next marker to be added
    private final HashMap<Character, List<Integer>> schemas = new HashMap<>();

    public MenuSchema(InventoryType inventoryType) {
        slotCount = inventoryType.getSize();
    }

    // This method will only mask the provided slots if they do not exceed the maximum size of the inventory
    private void mask(String slots) {
        // Get all the markers as a character array
        char[] markers = slots.replace(" ", "").toCharArray();

        // Get the remaining slots to be masked
        int remainingMaskSlots = slotCount - markerSlot;

        // IF the number of markers supplied is valid
        if (markers.length > 0 && markers.length <= slotCount && markers.length <= remainingMaskSlots) {
            // FOR each marker
            for (char marker : markers) {
                // Get the slot list for this character
                List<Integer> slotList = schemas.getOrDefault(marker, new ArrayList<>());

                // Add the new marker slot to the list
                slotList.add(markerSlot);

                // Store the updated list for the character
                schemas.put(marker, slotList);

                // Increment the marker slot
                markerSlot++;
            }
        }
    }

    /**
     * This method takes one or more strings and splits them up into their individual characters, then marks the
     *  represented slots as represented by that character using the order they are provided and starting at slot index
     *  0 for the specified inventory type.
     * @param maskArray the mask strings
     * @return this MenuSchema
     */
    public MenuSchema mask(String... maskArray) {
        for (String slots : maskArray) {
            mask(slots);
        }
        return this;
    }

    /**
     * Gets the slot indexes associated with a specific marker character
     * @param marker the character that represents the slots desired
     * @return the list of the slots associated with the marker, or any empty ArrayList if none
     */
    public List<Integer> getSlots(char marker) {
        return schemas.getOrDefault(marker, new ArrayList<>());
    }

    /**
     * Gets the slot indexes associated with each marker character in a string
     * @param markers the string containing the marker characters
     * @return the list of the slots associated with the markers, or any empty ArrayList if none
     */
    public List<Integer> getSlots(String markers) {
        ArrayList<Integer> slots = new ArrayList<>();
        char[] markerArray = markers.toCharArray();
        for (char marker : markerArray) {
            slots.addAll(schemas.getOrDefault(marker, new ArrayList<>()));
        }
        return slots;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder(StringUtils.repeat("0",slotCount));

        schemas.forEach((character,slotList) -> {
            for (int slot : slotList) {
                message.setCharAt(slot, character);
            }
        });

        return message.toString();
    }
}
