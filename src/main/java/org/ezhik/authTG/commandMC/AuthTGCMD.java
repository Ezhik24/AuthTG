package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;
import org.jetbrains.annotations.NotNull;

public class AuthTGCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.authtg")) {
                MessageHelper.send(commandSender, AuthTG.getMessage("authtgnoperm", "MC"));
                return false;
            }

            if (strings.length < 1) {
                MessageHelper.send(commandSender, AuthTG.getMessage("authtgusage", "MC"));
                return false;
            }

            switch (strings[0].toLowerCase()) {
                case "reload":
                    AuthTG.getInstance().reloadPluginRuntime();
                    MessageHelper.send(commandSender, AuthTG.getMessage("authtgnobotmysql", "MC"));
                    MessageHelper.send(commandSender, AuthTG.getMessage("authtgsuccess", "MC"));
                    MessageHelper.send(commandSender, AuthTG.isTelegramEnabled()
                            ? "<green>Telegram integration: ENABLED"
                            : "<yellow>Telegram integration: DISABLED");
                    return true;
                default:
                    MessageHelper.send(commandSender, AuthTG.getMessage("authtgusage", "MC"));
                    return false;
            }
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            if (strings.length < 1) {
                MessageHelper.send(console, AuthTG.getMessage("authtgusage", "MC"));
                return false;
            }

            switch (strings[0].toLowerCase()) {
                case "reload":
                    AuthTG.getInstance().reloadPluginRuntime();
                    MessageHelper.send(console, AuthTG.getMessage("authtgnobotmysql", "MC"));
                    MessageHelper.send(console, AuthTG.getMessage("authtgsuccess", "MC"));
                    MessageHelper.send(console, AuthTG.isTelegramEnabled()
                            ? "<green>Telegram integration: ENABLED"
                            : "<yellow>Telegram integration: DISABLED");
                    return true;
                default:
                    MessageHelper.send(console, AuthTG.getMessage("authtgusage", "MC"));
                    return false;
            }
        }
    }
}