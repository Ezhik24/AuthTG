package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

public class TGbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.tgbc")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.tgbcnoperm")));
                return false;
            }
            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.tgbcsuccess")));
        } else {
            String text = String.join(" ", strings);
            User.sendBroadcastMessage(text);
            System.out.println(AuthTG.config.get("messages.console.tgbcsuccess"));
        }
        return true;
    }
}
