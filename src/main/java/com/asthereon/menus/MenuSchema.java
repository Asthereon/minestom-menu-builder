package com.asthereon.menus;

import com.asthereon.asthcore.AsthCore;
import net.minestom.server.inventory.InventoryType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuSchema {

    private int slotCount = 0;
    private int markerSlot = 0;
    private HashMap<Character, List<Integer>> schemas = new HashMap<>();

    public MenuSchema(InventoryType inventoryType) {
        slotCount = inventoryType.getSize();
    }

    public MenuSchema mask(String slots) {
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
        return this;
    }

    public List<Integer> getSlots(char marker) {
        return schemas.getOrDefault(marker, new ArrayList<>());
    }

    public List<Integer> getSlots(String marker) {
        return schemas.getOrDefault(marker.charAt(0), new ArrayList<>());
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
