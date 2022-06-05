package me.manraj514.seedbasket.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
