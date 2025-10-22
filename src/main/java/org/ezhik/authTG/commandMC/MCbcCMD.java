package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

public class MCbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.mcbc")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mcbcnoperm", "MC")));
                return false;
            }
            String text = String.join(" ", strings);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mcbc", "MC") + text));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mcbcsuccess", "MC")));
        } else {
            String text = String.join(" ", strings);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mcbc", "MC") + text));
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mcbcsuccess", "MC")));
        }
        return true;
    }
}
