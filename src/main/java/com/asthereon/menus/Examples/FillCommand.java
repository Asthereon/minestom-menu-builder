package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class FillCommand extends Command {
    public FillCommand() {
        super("fill");

        setDefaultExecutor((sender, context) -> AsthCore.sendMessage(sender, "<white>Usage: /fill <item>"));

        addSyntax((sender, context) -> {
            final ItemStack itemStack = ItemStack.builder(((ItemStack) context.get("item")).getMaterial()).amount(64).build();
            // FIXME: support count > 64
            Player player = sender.asPlayer();
            boolean filled = false;
            while (!filled) {
                filled = !player.getInventory().addItemStack(itemStack);
            }

            AsthCore.sendMessage(sender, "<white>Items have been given successfully!");

        }, ItemStack("item"));
    }
}
