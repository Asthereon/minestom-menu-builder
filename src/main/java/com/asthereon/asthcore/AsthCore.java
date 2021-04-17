package com.asthereon.asthcore;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.UUID;

public class AsthCore {

    public static Component getComponent(String text) {
        return MiniMessage.get().parse(text);
    }

    // Get random integer between two integers
    public static int getRandomInteger(int minimum, int maximum) {
        return (int)(Math.random() * ((maximum - minimum) + 1)) + minimum;
    }

    // Get random double between two doubles
    public static double getRandomDouble(double minimum, double maximum) {
        return minimum + (maximum - minimum) * Math.random();
    }

    // Clamps an input between a lower and upper bound
    public static int clamp(int lowerBound, int input, int upperBound) {
        return Math.max(lowerBound, Math.min(input, upperBound));
    }

    // Broadcast to everyone on server
    public static void broadcast(String message) {
        Audiences.players().sendMessage(getComponent(message));
    }

    // Sends a chat message using MiniMessage
    public static void sendMessage(Player player, String message) {
        player.sendMessage(getComponent(message));
    }

    // Sends a chat message using MiniMessage to sender
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(getComponent(message));
    }

    // Sends a message to console
    public static void console(String message) {
        System.out.println("[main] " + message);
    }

    // Sends a message to console with a custom label
    public static void console(String label, String message) {
        System.out.println("[" + label + "] " + message);
    }

    // Force a player to use a command
    public static void forceCommand(Player player, String command) {
        player.chat("/" + command);
    }

    // Send player a title
    public static void sendTitle(Player player, Component title, Component subtitle, long fadeIn, long duration, long fadeOut) {
        player.showTitle(Title.title(title, subtitle, Title.Times.of(Duration.ofSeconds(fadeIn), Duration.ofSeconds(duration), Duration.ofSeconds(fadeOut))));
    }

    // Send player a action bar
    public static void sendActionBar(Player player, Component text) {
        player.sendActionBar(text);
    }

    // Check if a itemstack is empty
    public static boolean isEmpty(ItemStack itemStack) {
        if (itemStack == null) return true;
        return itemStack.getMaterial() == Material.AIR;
    }

    // Checks is a inventory slot is empty
    public static boolean isEmptySlot(Player player, int slotID) {
        return isEmpty(player.getInventory().getItemStack(slotID));
    }

    // Formats a integer with commas
    public static String formatNumber(int toFormat) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(toFormat);
    }

    // Formats a float with commas
    public static String formatNumber(float toFormat) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(toFormat);
    }

    // Format a float with commas
    public static String formatNumber(double toFormat) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(toFormat);
    }

    // Play a sound to player
    public static void playSound(Player player, String sound, String source, float volume, float pitch) {
        player.playSound(Sound.sound(Key.key(sound), Sound.Source.valueOf(source.toUpperCase()), volume, pitch), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
    }

    // Get empty slot of player
    public static int getEmptySlot(Inventory inventory) {
        ItemStack[] contents = inventory.getItemStacks();
        for (int slotID = 1; slotID < contents.length; slotID++) {
            if (contents[slotID] == null) {
                return slotID;
            }
        }
        return -1;
    }

    // Clear player cursor
    public static void clearCursor(Player player) {
        player.getInventory().setCursorItem(ItemStack.AIR);
    }

    // Create player head (NEEDS A REWRITE TO NOT STALL MAIN THREAD)
    // TODO: Update this to use the new item builder (I just can't be bothered right now)
    public static ItemStack createPlayerHead(UUID uuid) {
//        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
//        PlayerHeadMeta skullMeta = (PlayerHeadMeta)itemStack.getItemMeta();
//        skullMeta.setSkullOwner(uuid);
//        itemStack.setItemMeta(skullMeta);
//        return itemStack;
        return ItemStack.AIR;
    }

    // Replace in array
    public static String[] replaceInArray(String[] array, String toReplace, String replaceWith) {
        String[] newArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i].replace(toReplace, replaceWith);
        }
        return newArray;
    }

    // Format time segment
    public static String formatTimeSegment(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return Integer.toString(time);
        }
    }

    // Format time
    public static String formatTime(int totalSeconds) {
        int hours = (int) Math.floor(totalSeconds / 3600.0);
        int minutes = (int) Math.floor((totalSeconds - (hours * 3600)) / 60.0);
        int seconds = totalSeconds % 60;
        String formattedSeconds = formatTimeSegment(seconds);
        if (hours > 0) {
            String formattedMinutes = formatTimeSegment(minutes);
            return hours + ":" + formattedMinutes + ":" + formattedSeconds;
        } else {
            return minutes + ":" + formattedSeconds;
        }
    }

    public static <T> T randomFrom(T... items) {
        return items[AsthCore.getRandomInteger(0, items.length - 1)];
    }

    public static boolean chanceSucceeds(double chance) {
        return AsthCore.getRandomDouble(0, 100) < chance;
    }

}
