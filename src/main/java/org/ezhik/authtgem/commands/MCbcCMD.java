package org.ezhik.authtgem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MCbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("minetelegram.mcbc")) {
            String text = String.join(" ", strings);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&b&lMT&f&l] &a&l" + text));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&b&lMT&f&l] &a&lУспешно отправлено!"));
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&b&lMT&f&l] &c&lУ вас нет прав для использования этой команды"));
        }

        return true;
    }
}
