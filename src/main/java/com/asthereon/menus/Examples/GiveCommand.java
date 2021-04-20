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

public class GiveCommand extends Command {
    public GiveCommand() {
        super("give");

        setDefaultExecutor((sender, context) -> AsthCore.sendMessage(sender, "<white>Usage: /entity give <target> <item> [<count>]"));

        addSyntax((sender, context) -> {
            final EntityFinder entityFinder = context.get("target");
            final int count = context.get("count");
            final ItemStack itemStack = ItemStack.builder(((ItemStack) context.get("item")).getMaterial()).amount((byte) count).build();
            // FIXME: support count > 64

            final List<Entity> targets = entityFinder.find(sender);
            for (Entity target : targets) {
                if (target instanceof Player) {
                    Player player = (Player) target;
                    player.getInventory().addItemStack(itemStack);
                }
            }

            AsthCore.sendMessage(sender, "<white>Items have been given successfully!");

        }, Entity("target").onlyPlayers(true), ItemStack("item"), Integer("count").setDefaultValue(() -> 1));
    }
}
