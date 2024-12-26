package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.User;

public class TgbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("minetelegram.tgbc")) {
            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tgbc_nopermission")));
        }
        return true;
    }
}
