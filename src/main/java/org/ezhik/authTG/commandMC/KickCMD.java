package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;

import java.util.UUID;

public class KickCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("authtg.kick")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknoperm")));
                return false;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kickusage")));
                return false;
            }
            if (strings.length < 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kickusage")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + 1);
            UUID uuidtarget = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (uuidtarget == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknotfound")));
                return false;
            }
            Player target = Bukkit.getPlayer(uuidtarget);
            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknotonline")));
                return false;
            }
            if (reason.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknotreason")));
                return false;
            }
            Handler.kick(target.getName(), reason);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicksuccess").replace("{PLAYER}", target.getName())));
            return true;
        }else {
            ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
            if (strings.length == 0) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kickusage")));
                return false;
            }
            if (strings.length < 2) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kickusage")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + 1);
            UUID uuidtarget = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (uuidtarget == null) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknotfound")));
                return false;
            }
            Player target = Bukkit.getPlayer(uuidtarget);
            if (target == null) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknotonline")));
                return false;
            }
            if (reason.isEmpty()) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicknotreason")));
                return false;
            }
            Handler.kick(target.getName(), reason);
            consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.kicksuccess").replace("{PLAYER}", target.getName())));
            return true;
        }
    }
}
