package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

public class AdminCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("authtg.setadmin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cУ вас нет прав на использование этой команды!"));
            return false;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cИспользование: /admin add | rem | list <ник>"));
            return false;
        }
        Player player = (Player) commandSender;
        if (strings[0].equals("add")) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cИспользование: /admin add <ник>"));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь не зарегистрирован!"));
                return false;
            }
            if (user.isadmin) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь уже является администратором!"));
                return false;
            }
            AuthTG.loader.setAdmin(user.uuid);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l[AuthTG] &aВы успешно сделали пользователя администратором!"));
            if (user.activetg) user.sendMessage("Вы теперь стали администратором!");
            if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l[AuthTG] &aВы стали администратором!"));
        }
        else if (strings[0].equals("rem")) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cИспользование: /admin rem <ник>"));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь не зарегистрирован!"));
                return false;
            }
            if (!user.isadmin) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cПользователь не является администратором!"));
                return false;
            }
            AuthTG.loader.removeAdmin(user.uuid);
            if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l[AuthTG] &aВы больше не являетесь администратором!"));
            if (user.activetg) user.sendMessage("Вы больше не являетесь администратором!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l[AuthTG] &aВы успешно убрали пользователя из администраторов!"));
        }
        else if (strings[0].equals("list")) {
            if (AuthTG.loader.getAdminList().isEmpty()) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cСписок администраторов пуст!"));
                return false;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l[AuthTG] &aСписок администраторов:"));
                for (String playername : AuthTG.loader.getAdminList()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + playername));
                }
                return true;
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cИспользование: /admin add | rem | list <ник>"));
            return false;
        }
        return true;
    }
}
