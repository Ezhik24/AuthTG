package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

public class MCbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.mcbc")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mcbcnoperm")));
                return false;
            }
            String text = String.join(" ", strings);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mcbc") + text));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mcbcsuccess")));
        } else {
            String text = String.join(" ", strings);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mcbc") + text));
            System.out.println(AuthTG.config.get("messages.console.mcbcsuccess"));
        }
        return true;
    }
}
