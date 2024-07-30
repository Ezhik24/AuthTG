package org.ezhik.authtgem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ezhik.authtgem.User;

public class TgbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String text = String.join(" ", strings);
        User.sendBroadcastMessage(text);
        return true;
    }
}
