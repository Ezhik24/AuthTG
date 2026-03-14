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

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.authtg")) {
                commandSender.sendMessage(color(AuthTG.getMessage("authtgnoperm", "MC")));
                return false;
            }

            if (strings.length < 1) {
                commandSender.sendMessage(AuthTG.getMessage("authtgusage", "MC"));
                return false;
            }

            switch (strings[0].toLowerCase()) {
                case "reload":
                    AuthTG.getInstance().reloadPluginRuntime();
                    commandSender.sendMessage(color(AuthTG.getMessage("authtgnobotmysql", "MC")));
                    commandSender.sendMessage(color(AuthTG.getMessage("authtgsuccess", "MC")));
                    commandSender.sendMessage(color(AuthTG.isTelegramEnabled()
                            ? "&aTelegram integration: ENABLED"
                            : "&eTelegram integration: DISABLED"));
                    return true;
                default:
                    commandSender.sendMessage(AuthTG.getMessage("authtgusage", "MC"));
                    return false;
            }
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            if (strings.length < 1) {
                console.sendMessage(AuthTG.getMessage("authtgusage", "MC"));
                return false;
            }

            switch (strings[0].toLowerCase()) {
                case "reload":
                    AuthTG.getInstance().reloadPluginRuntime();
                    console.sendMessage(color(AuthTG.getMessage("authtgnobotmysql", "MC")));
                    console.sendMessage(color(AuthTG.getMessage("authtgsuccess", "MC")));
                    console.sendMessage(color(AuthTG.isTelegramEnabled()
                            ? "&aTelegram integration: ENABLED"
                            : "&eTelegram integration: DISABLED"));
                    return true;
                default:
                    console.sendMessage(AuthTG.getMessage("authtgusage", "MC"));
                    return false;
            }
        }
    }
}
