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
        if (!commandSender.hasPermission("authtg.admin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminnoperm", "MC")));
            return false;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminhelp", "MC")));
            return false;
        }
        Player player = (Player) commandSender;
        if (strings[0].equals("add")) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminhelpadd", "MC")));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminusernotfound", "MC")));
                return false;
            }
            if (user.isadmin) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminalreadyadmin", "MC")));
                return false;
            }
            AuthTG.loader.setAdmin(user.uuid);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminadded", "MC")));
            if (user.activetg) user.sendMessage(AuthTG.getMessage("adminadd", "TG"));
            if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminadd", "MC")));
        }
        else if (strings[0].equals("rem")) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminhelprem", "MC")));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminusernotfound", "MC")));
                return false;
            }
            if (!user.isadmin) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminnotadmin", "MC")));
                return false;
            }
            AuthTG.loader.removeAdmin(user.uuid);
            if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminrem", "MC")));
            if (user.activetg) user.sendMessage(AuthTG.getMessage("adminrem", "TG"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminremoved", "MC")));
        }
        else if (strings[0].equals("list")) {
            if (AuthTG.loader.getAdminList().isEmpty()) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminlistnotfound", "MC")));
                return false;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminlist", "MC")));
                for (String playername : AuthTG.loader.getAdminList()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminlistplayer", "MC").replace("{PLAYER}", playername)));
                }
                return true;
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("adminhelp", "MC")));
            return false;
        }
        return true;
    }
}
