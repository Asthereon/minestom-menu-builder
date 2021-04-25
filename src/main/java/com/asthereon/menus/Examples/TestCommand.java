package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;

import static net.minestom.server.command.builder.arguments.ArgumentType.ItemStack;

public class TestCommand extends Command {
    public TestCommand() {
        super("test");

        setDefaultExecutor((sender, context) -> AsthCore.sendMessage(sender, "<white>Usage: /test"));

        addSyntax((sender, context) -> {
            Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, "Test");
            Player player = sender.asPlayer();
            player.openInventory(inventory);
            inventory.setCursorItem(sender.asPlayer(), ItemStack.of(Material.DIAMOND));
            inventory.addInventoryCondition((player1, slot, clickType, inventoryConditionResult) -> {
                inventoryConditionResult.setCancel(true);
                Inventory inventory1 = new Inventory(InventoryType.CHEST_6_ROW, "Test 2");
                player.openInventory(inventory1);
            });
        });
    }
}
