package me.manraj514.seedbasket.commands;

import me.manraj514.seedbasket.SeedBasket;
import me.manraj514.seedbasket.utils.MessageUtils;
import me.manraj514.seedbasket.utils.SkullUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SeedBasketCommand implements CommandExecutor {
    private final SeedBasket plugin;

    public SeedBasketCommand(SeedBasket plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            sender.sendMessage(MessageUtils.colorize("&cYou are not a player!"));
            return true;
        }
        if (!player.hasPermission("SeedBasket.seedbasket")){
            player.sendMessage(MessageUtils.colorize("&cYou don't have the permission to use this command"));
            return true;
        }

        if (command.getName().equalsIgnoreCase("giveseedbasket")){
            player.getInventory().addItem(SkullUtils.getSeedBasket());

            player.sendMessage(MessageUtils.colorize("&aYou have been given a seed of baskets"));
            return true;
        }
        return false;
    }
}