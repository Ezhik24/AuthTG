package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.jetbrains.annotations.NotNull;

public class UnLinkCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.unlink")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unlinknoperm", "MC")));
                return false;
            }
            if (strings.length != 1) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unlinkusage", "MC")));
                return false;
            }
            AuthTG.loader.setActiveTG(AuthTG.loader.getUUIDbyPlayerName(strings[0]), false);
            if (Bukkit.getPlayer(strings[0]) != null) Bukkit.getPlayer(strings[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unlinkpl", "MC")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unlinksuccess", "MC")));
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length != 1) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unlinkusage", "MC")));
                return false;
            }
            AuthTG.loader.setActiveTG(AuthTG.loader.getUUIDbyPlayerName(strings[0]), false);
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("unlinksuccess", "MC")));
        }
        return true;
    }
}
