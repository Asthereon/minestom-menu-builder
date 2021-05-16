package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Menu.MenuManager;
import net.minestom.server.command.builder.Command;

public class SchemaCommand extends Command {
	
    public SchemaCommand() {
        super("schema");

        setDefaultExecutor((sender, context) -> AsthCore.sendMessage(sender, "<white>Usage: /schema"));

        addSyntax((sender, context) -> MenuManager.open("Schema", sender.asPlayer(), null));
    }
}
