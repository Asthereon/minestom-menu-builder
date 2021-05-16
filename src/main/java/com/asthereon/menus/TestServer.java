package com.asthereon.menus;

import com.asthereon.asthcore.AsthCore;
import com.asthereon.asthcore.TestServer.AsthCoreCommand;
import com.asthereon.menus.Examples.*;
import com.asthereon.menus.Menu.MenuManager;
import com.asthereon.placeholders.PlaceholderManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.PlacementRules;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.storage.StorageManager;
import net.minestom.server.utils.Position;

public class TestServer {

    public static void main(String[] args) {
        MinecraftServer minecraftServer = AsthCore.createTestServer();
        registerCommands();
        registerMenus();
        PlaceholderManager.registerDefaultPlaceholders();
        AsthCore.startTestServer(minecraftServer);
    }

    private static void registerCommands() {
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new BankCommand());
        commandManager.register(new EnderChestCommand());
        commandManager.register(new GiveCommand());
        commandManager.register(new FillCommand());
        commandManager.register(new TestCommand());
        commandManager.register(new SchemaCommand());
    }

    private static void registerMenus() {
        new Bank();
    }
}
