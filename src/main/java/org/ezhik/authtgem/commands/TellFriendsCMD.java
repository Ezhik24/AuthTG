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
            commandSender.sendMessage(ChatColor.GREEN + "[MT] Команда введена неверною Введите команду так: /tellfriend <ник> <сообщение>");
        } else {
            User user = User.getUser(commandSender.getName());
            if (!user.friends.contains(strings[0])) {
                commandSender.sendMessage(ChatColor.RED + "[MT] Такого игрока нет в друзьях");
            } else {
                User friend = User.getUser(strings[0]);
                if (friend == null) {
                    commandSender.sendMessage(ChatColor.RED + "[MT] Ваш друг отвязал телеграмм аккаунт");
                } else {
                    String message = String.join(" ", strings).replace(strings[0], " сообщение от " + user.playername + " :");
                    friend.sendMessageB(message, commandSender.getName());
                    commandSender.sendMessage(ChatColor.GREEN + "[MT] Сообщение успешно отправлено!");
                }
            }
        }
        return true;
    }
}
