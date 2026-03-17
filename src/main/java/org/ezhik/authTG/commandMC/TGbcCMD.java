package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.util.MessageHelper;

public class TGbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!AuthTG.isTelegramEnabled()) {
            if (commandSender instanceof Player) {
                MessageHelper.send(commandSender, "<red>Telegram integration is disabled in config.yml");
            } else {
                MessageHelper.send(Bukkit.getConsoleSender(), "<red>Telegram integration is disabled in config.yml");
            }
            return false;
        }

        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.tgbc")) {
                MessageHelper.send(commandSender, AuthTG.getMessage("tgbcnoperm", "MC"));
                return false;
            }

            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
            MessageHelper.send(commandSender, AuthTG.getMessage("tgbcsuccess", "MC"));
        } else {
            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            MessageHelper.send(console, AuthTG.getMessage("tgbcsuccess", "MC"));
        }

        return true;
    }
}