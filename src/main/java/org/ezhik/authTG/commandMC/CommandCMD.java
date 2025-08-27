package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

public class CommandCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!AuthTG.loader.isAdmin(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cУ вас нет прав для выполнения этой команды!"));
            return false;
        }
        if (strings.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command <add | rem | list>"));
            return false;
        }
        if (strings[0].equals("add")) {
            if (strings.length != 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command add <ник> <ban | mute | kick>"));
                return false;
            }
            if (strings[2].equals("ban") || strings[2].equals("mute") || strings[2].equals("kick")) {
                User user = User.getUser(strings[1]);
                if (user == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь не найден!"));
                    return false;
                }
                if (user.commands != null && user.commands.contains(strings[2])) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда уже добавлена!"));
                    return false;
                }
                AuthTG.loader.addCommand(user.uuid, strings[2]);
                if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВам выдали доступ к команде: " + strings[2]));
                if (user.activetg) user.sendMessage("Вым выдали доступ к команде: " + strings[2]);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВы выдали доступ к команде: " + strings[2] + " игроку: " + strings[1]));
            } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command add <ник> <ban | mute | kick>"));
        }
        else if (strings[0].equals("rem")) {
            if (strings.length != 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command rem <ник> <ban | mute | kick>"));
                return false;
            }
            if (strings[2].equals("ban") || strings[2].equals("mute") || strings[2].equals("kick")) {
                User user = User.getUser(strings[1]);
                if (user == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь не найден!"));
                    return false;
                }
                if (user.commands != null && !user.commands.contains(strings[2])) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда и так не выдана!"));
                    return false;
                }
                AuthTG.loader.removeCommand(user.uuid, strings[2]);
                if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВам удалена команда: " + strings[2]));
                if (user.activetg) user.sendMessage("Вым удалена команда: " + strings[2]);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВы удалили доступ к команде: " + strings[2] + " игроку: " + strings[1]));
            } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command rem <ник> <ban | mute | kick>"));
        }
        else if (strings[0].equals("list")) {
            if (strings.length != 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command list <ник>"));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь не найден!"));
                return false;
            }
            if (user.commands != null && user.commands.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cУ игрока: " + strings[1] + " нет доступных команд!"));
                return false;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cДоступные команды для игрока: " + strings[1] + ": " + user.commands.toString().replace("[", "").replace("]", "")));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cКоманда введена неверно! Введите: /command <add | rem | list>"));
            return false;
        }
        return true;
    }
}
