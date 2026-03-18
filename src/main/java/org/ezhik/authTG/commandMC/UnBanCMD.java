package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

import java.util.UUID;

public class UnBanCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!commandSender.hasPermission("authtg.unban")) {
                MessageHelper.send(player, AuthTG.getMessage("unbannoperm", "MC"));
                return false;
            }
            if (strings.length == 0) {
                MessageHelper.send(player, AuthTG.getMessage("unbanusage", "MC"));
                return false;
            }
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(player, AuthTG.getMessage("unbannotfound", "MC"));
                return false;
            }
            if (!AuthTG.loader.isBanned(targetuuid)) {
                MessageHelper.send(player, AuthTG.getMessage("unbannotbanned", "MC"));
                return false;
            }
            AuthTG.loader.deleteBan(targetuuid);
            MessageHelper.send(player, AuthTG.getMessage("unban", "MC").replace("{PLAYER}", strings[0]));
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length == 0) {
                MessageHelper.send(console, AuthTG.getMessage("unbanusage", "MC"));
                return false;
            }
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(console, AuthTG.getMessage("unbannotfound", "MC"));
                return false;
            }
            if (!AuthTG.loader.isBanned(targetuuid)) {
                MessageHelper.send(console, AuthTG.getMessage("unbannotbanned", "MC"));
                return false;
            }
            AuthTG.loader.deleteBan(targetuuid);
            MessageHelper.send(console, AuthTG.getMessage("unban", "MC").replace("{PLAYER}", strings[0]));
        }
        return true;
    }
}
