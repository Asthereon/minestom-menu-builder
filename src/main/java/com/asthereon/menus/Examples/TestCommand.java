package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Menu.MenuManager;
import com.asthereon.menus.Utils.Metadata;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.data.DataImpl;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;
import org.apache.commons.lang3.ClassUtils;

import static net.minestom.server.command.builder.arguments.ArgumentType.ItemStack;

public class TestCommand extends Command {
    public TestCommand() {
        super("test");

        setDefaultExecutor((sender, context) -> AsthCore.sendMessage(sender, "<white>Usage: /test"));

        addSyntax(TestCommand::test);
    }

    private static void test(CommandSender sender, CommandContext context) {
        DataImpl first = new DataImpl();
        DataImpl second = first.clone();
        System.out.println(first.toString());
        System.out.println(second.toString());
    }
}
