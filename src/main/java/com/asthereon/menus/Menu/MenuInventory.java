package com.asthereon.menus.Menu;

import com.asthereon.asthcore.Interfaces.Serializable;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.binary.BinaryReader;
import net.minestom.server.utils.binary.BinaryWriter;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A MenuInventory adds a concept of storage slots, which contain data that should persist.  Persistence for that data
 *  is facilitated by {@link MenuInventory#serialize()} to convert the items in the storage slots into a string, and
 *  {@link MenuInventory#deserialize(String)} to convert that string back into items placed in the correct storage slots
 */
public class MenuInventory extends Inventory implements Serializable {

    // The slots in the inventory that can store items that should persist
    private final List<Integer> storageSlots = new ArrayList<>();

    protected MenuInventory(@NotNull InventoryType inventoryType, @NotNull Component title) {
        super(inventoryType, title);
    }

    protected MenuInventory(@NotNull InventoryType inventoryType, @NotNull Component title, String serializedData) {
        super(inventoryType, title);
        deserialize(serializedData);
    }

    // Adds a storage slot to the storage slot list
    protected MenuInventory storageSlot(int slot) {
        this.storageSlots.add(slot);
        return this;
    }

    // Adds a collection of storage slots to the storage slot list
    protected MenuInventory storageSlots(Collection<Integer> slots) {
        this.storageSlots.addAll(slots);
        return this;
    }

    // Adds all the slots between min and max inclusive to the storage slot list
    protected MenuInventory storageSlotRange(int min, int max) {
        for (int i = min; i <= max; i++) {
            this.storageSlots.add(i);
        }
        return this;
    }

    // Clears all inventory conditions of this MenuInventory
    protected void clearInventoryConditions() {
        getInventoryConditions().clear();
    }

    /**
     * Converts the ItemStacks in the storage slots, as well as their slot indexes into a single Base64 encoded string,
     *  which can be passed to {@link MenuInventory#deserialize(String)} to restore those items to their slots.
     * @return the Base64 string containing all the storage slot items
     */
    @Override
    public String serialize() {
        BinaryWriter binaryWriter = new BinaryWriter();
        for (int storageSlot : storageSlots) {
            binaryWriter.writeInt(storageSlot);
            binaryWriter.writeItemStack(getItemStack(storageSlot));
        }
        return Base64.encodeBase64String(binaryWriter.toByteArray());
    }

    /**
     * Converts a Base64 string provided by {@link MenuInventory#serialize()} into items and their associated slots to
     *  restore the storage slots to a previous state.
     * @param data the Base64 string containing all the storage slot items
     */
    @Override
    public void deserialize(String data) {
        if (data != null) {
            BinaryReader binaryReader = new BinaryReader(Base64.decodeBase64(data));
            try {
                int slot = binaryReader.readInt();
                ItemStack itemStack = binaryReader.readItemStack();
                while (null != itemStack) {
                    setItemStack(slot, itemStack);
                    slot = binaryReader.readInt();
                    itemStack = binaryReader.readItemStack();
                }
            } catch (Exception ignored) {

            }
        }
    }

    /**
     * A method that checks if there are any storage slots to determine if there is persistent data in this MenuInventory
     * @return whether there are storage slots in this MenuInventory
     */
    public boolean hasPersistentData() {
        return storageSlots.size() > 0;
    }

    @Override
    public @NotNull Set<Player> getViewers() { return super.getViewers(); }
    public List<Integer> getStorageSlots() { return storageSlots; }
}
