package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Menu.MenuManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class BankCommand extends Command {

    public BankCommand() {

        super("bank");

        setDefaultExecutor(this::usage);

        addSyntax(this::executeOnSelf);
    }

    private void usage(CommandSender sender, CommandContext context) {
        AsthCore.sendMessage(sender, "<white>Usage: /bank");
    }

    private void executeOnSelf(CommandSender sender, CommandContext context) {
        if (!sender.isPlayer()) {
            AsthCore.sendMessage(sender, "<red>The command is only available for player");
            return;
        }

        Player player = (Player) sender;

        MenuManager.open("Bank", player, null);
    }
}
