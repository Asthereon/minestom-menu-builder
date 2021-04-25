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

public class MenuInventory extends Inventory implements Serializable {

    List<Integer> storageSlots = new ArrayList<>();

    public MenuInventory(@NotNull InventoryType inventoryType, @NotNull Component title) {
        super(inventoryType, title);
    }

    public MenuInventory(@NotNull InventoryType inventoryType, @NotNull Component title, String serializedData) {
        super(inventoryType, title);
        deserialize(serializedData);
    }

    public MenuInventory storageSlot(int slot) {
        this.storageSlots.add(slot);
        return this;
    }

    public MenuInventory storageSlots(Collection<Integer> slots) {
        this.storageSlots.addAll(slots);
        return this;
    }

    public MenuInventory storageSlotRange(int min, int max) {
        for (int i = min; i <= max; i++) {
            this.storageSlots.add(i);
        }
        return this;
    }

    public void clearInventoryConditions() {
        getInventoryConditions().clear();
    }

    @Override
    public @NotNull Set<Player> getViewers() {
        return super.getViewers();
    }

    public boolean hasPersistentData() {
        return storageSlots.size() > 0;
    }

    @Override
    public String serialize() {
        BinaryWriter binaryWriter = new BinaryWriter();
        for (int storageSlot : storageSlots) {
            binaryWriter.writeInt(storageSlot);
            binaryWriter.writeItemStack(getItemStack(storageSlot));
        }
        return Base64.encodeBase64String(binaryWriter.toByteArray());
    }

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
}
