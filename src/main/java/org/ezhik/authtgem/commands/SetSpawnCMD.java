package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.User;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class SetSpawnCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("minetelegram.setspawn")) {
            if (strings.length == 0) {
                Player player = (Player) commandSender;
                User.setSpawnLocation(player.getLocation());
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &a&lТочка спавна установлена"));
            } else {
                if (strings[0].equals("none")) {
                    User.setSpawnLocation(null);
                }
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lУ вас нет прав для использования этой команды"));
        }
        return true;
    }
}
