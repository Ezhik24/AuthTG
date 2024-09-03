package org.ezhik.authtgem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ezhik.authtgem.User;

public class TellFriendsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /tellfriends <ник> <сообщение>"));
        } else {
            User user = User.getUser(commandSender.getName());
            if (!user.friends.contains(strings[0])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lВаш друг отвязал телеграмм аккаунт"));
            } else {
                User friend = User.getUser(strings[0]);
                if (friend == null) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lВаш друг отвязал телеграмм аккаунт"));
                } else {
                    String message = String.join(" ", strings).replace(strings[0], " сообщение от " + user.playername + " :");
                    friend.sendMessageB(message, commandSender.getName());
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &a&lСообщение отправлено"));
                }
            }
        }
        return true;
    }
}
