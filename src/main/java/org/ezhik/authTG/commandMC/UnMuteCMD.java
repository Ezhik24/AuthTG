package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.util.UUID;

public class UnMuteCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!commandSender.hasPermission("authtg.unmute")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutenoperm", "MC")));
                return false;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmuteusage", "MC")));
                return false;
            }
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutenotfound", "MC")));
                return false;
            }
            if (!AuthTG.loader.isMuted(targetuuid)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutenotmuted", "MC")));
                return false;
            }
            AuthTG.loader.deleteMute(targetuuid);
            if (Bukkit.getPlayer(targetuuid) != null) Bukkit.getPlayer(targetuuid).sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutepl", "MC")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmute", "MC").replace("{PLAYER}", strings[0])));
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length == 0) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmuteusage", "MC")));
                return false;
            }
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutenotfound", "MC")));
                return false;
            }
            if (!AuthTG.loader.isMuted(targetuuid)) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutenotmuted", "MC")));
                return false;
            }
            if (Bukkit.getPlayer(targetuuid) != null) Bukkit.getPlayer(targetuuid).sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmutepl", "MC")));
            AuthTG.loader.deleteMute(targetuuid);
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unmute", "MC").replace("{PLAYER}", strings[0])));
        }
        return true;
    }
}
