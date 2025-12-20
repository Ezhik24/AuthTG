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

public class AuthTGCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.authtg")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("authtgnoperm", "MC")));
                return false;
            }
            if (strings.length < 1) {
                commandSender.sendMessage(AuthTG.getMessage("authtgusage", "MC"));
                return false;
            }
            switch (strings[0]) {
                case "reload":
                    AuthTG.loadConfigParameters();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgnobotmysql", "MC")));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgsuccess", "MC")));
            }
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length < 1) {
                console.sendMessage(AuthTG.getMessage("authtgusage", "MC"));
                return false;
            }
            switch (strings[0]) {
                case "reload":
                    AuthTG.loadConfigParameters();
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgnobotmysql", "MC")));
                    console.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgsuccess", "MC")));
            }
        }
        return true;
    }
}
