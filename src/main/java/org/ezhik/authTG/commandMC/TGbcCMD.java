package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

public class TGbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.tgbc")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("tgbcnoperm", "MC")));
                return false;
            }
            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("tgbcsuccess", "MC")));
        } else {
            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("tgbcsuccess", "MC")));
        }
        return true;
    }
}
