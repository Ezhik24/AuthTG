package org.ezhik.authtgem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ezhik.authtgem.AuthTGEM;

public class MCbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("minetelegram.mcbc")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("mcbc_nopermission")));
            return false;
        }
        String text = String.join(" ", strings);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("mcbc") + text));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("mcbc_success")));
        return true;
    }
}
