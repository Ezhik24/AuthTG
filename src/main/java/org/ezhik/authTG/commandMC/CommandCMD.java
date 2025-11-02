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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdnoperm", "MC")));
            return false;
        }
        if (strings.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelp", "MC")));
            return false;
        }
        if (strings[0].equals("add")) {
            if (strings.length != 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelpadd", "MC")));
                return false;
            }
            if (strings[2].equals("ban") || strings[2].equals("mute") || strings[2].equals("kick")) {
                User user = User.getUser(strings[1]);
                if (user == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdusernotfound", "MC")));
                    return false;
                }
                if (user.commands != null && user.commands.contains(strings[2])) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdaddalready", "MC")));
                    return false;
                }
                AuthTG.loader.addCommand(user.uuid, strings[2]);
                if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdadded", "MC").replace("{COMMAND}", strings[2])));
                if (user.activetg) user.sendMessage(AuthTG.getMessage("cmdadded", "TG").replace("{COMMAND}", strings[2]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdadd", "MC").replace("{COMMAND}", strings[2]).replace("{PLAYER}", strings[1])));
                } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelpadd", "MC")));
        }
        else if (strings[0].equals("rem")) {
            if (strings.length != 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelprem", "MC")));
                return false;
            }
            if (strings[2].equals("ban") || strings[2].equals("mute") || strings[2].equals("kick")) {
                User user = User.getUser(strings[1]);
                if (user == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdusernotfound", "MC")));
                    return false;
                }
                if (user.commands != null && !user.commands.contains(strings[2])) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdremnot", "MC")));
                    return false;
                }
                AuthTG.loader.removeCommand(user.uuid, strings[2]);
                if (user.player != null) user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdrem", "MC").replace("{COMMAND}", strings[2])));
                if (user.activetg) user.sendMessage(AuthTG.getMessage("cmdrem", "TG").replace("{COMMAND}", strings[2]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdrem", "MC").replace("{COMMAND}", strings[2]).replace("{PLAYER}", strings[1])));
            } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelprem", "MC")));
        }
        else if (strings[0].equals("list")) {
            if (strings.length != 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelplist", "MC")));
                return false;
            }
            User user = User.getUser(strings[1]);
            if (user == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdusernotfound", "MC")));
                return false;
            }
            if (user.commands != null && user.commands.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdlistempty", "MC").replace("{PLAYER}", strings[1])));
                return false;
            }
            String commands = user.commands.toString().replace("[", "").replace("]", "");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdlist", "MC").replace("{PLAYER}", strings[1]).replace("{COMMANDS}", commands)));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdhelp", "MC")));
            return false;
        }
        return true;
    }
}
