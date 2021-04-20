package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.time.TimeUnit;

public class EnderChestCommand extends Command {

    public EnderChestCommand() {

        super("enderchest");

        setDefaultExecutor(this::usage);

        addSyntax(this::executeOnSelf);
    }

    private void usage(CommandSender sender, CommandContext context) {
        AsthCore.sendMessage(sender, "<white>Usage: /test");
    }

    private void executeOnSelf(CommandSender sender, CommandContext context) {
        if (!sender.isPlayer()) {
            AsthCore.sendMessage(sender, "<red>The command is only available for player");
            return;
        }

        Player player = (Player) sender;

        new EnderChest().open(player);

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            new EnderChest().open(player);
        }).delay(5, TimeUnit.SECOND).schedule();
    }
}
