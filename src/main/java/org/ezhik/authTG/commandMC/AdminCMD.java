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
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminnoperm")));
            return false;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminhelp")));
            return false;
        }
        Player player = (Player) commandSender;
        if (strings[0].equals("add")) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminhelpadd")));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminusernotfound")));
                return false;
            }
            if (user.isadmin) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminalreadyadmin")));
                return false;
            }
            AuthTG.loader.setAdmin(user.uuid);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminadded")));
            if (user.activetg) user.sendMessage(AuthTG.config.getString("messages.telegram.adminadd"));
            if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminadd")));
        }
        else if (strings[0].equals("rem")) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminhelprem")));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminusernotfound")));
                return false;
            }
            if (!user.isadmin) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminnotadmin")));
                return false;
            }
            AuthTG.loader.removeAdmin(user.uuid);
            if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminrem")));
            if (user.activetg) user.sendMessage(AuthTG.config.getString("messages.telegram.adminrem"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminremoved")));
        }
        else if (strings[0].equals("list")) {
            if (AuthTG.loader.getAdminList().isEmpty()) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminlistnotfound")));
                return false;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminlist")));
                for (String playername : AuthTG.loader.getAdminList()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminlistplayer").replace("{PLAYER}", playername)));
                }
                return true;
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.adminhelp")));
            return false;
        }
        return true;
    }
}
