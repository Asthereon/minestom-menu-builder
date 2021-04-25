package com.asthereon.menus.Examples;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.menus.Menu.Menu;

import net.minestom.server.command.builder.Command;

public class SchemaCommand extends Command {
	
	private Menu menu = Schema.get();
	
    public SchemaCommand() {
        super("schema");

        setDefaultExecutor((sender, context) -> AsthCore.sendMessage(sender, "<white>Usage: /schema"));

        addSyntax((sender, context) -> menu.open(sender.asPlayer()));
    }
}
