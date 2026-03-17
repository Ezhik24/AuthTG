package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

import java.util.UUID;

public class UnMuteCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (!commandSender.hasPermission("authtg.unmute")) {
                MessageHelper.send(player, AuthTG.getMessage("unmutenoperm", "MC"));
                return false;
            }

            if (strings.length == 0) {
                MessageHelper.send(player, AuthTG.getMessage("unmuteusage", "MC"));
                return false;
            }

            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(player, AuthTG.getMessage("unmutenotfound", "MC"));
                return false;
            }

            if (!AuthTG.loader.isMuted(targetuuid)) {
                MessageHelper.send(player, AuthTG.getMessage("unmutenotmuted", "MC"));
                return false;
            }

            AuthTG.loader.deleteMute(targetuuid);

            Player target = Bukkit.getPlayer(targetuuid);
            if (target != null) {
                MessageHelper.send(target, AuthTG.getMessage("unmutepl", "MC"));
            }

            MessageHelper.send(player, AuthTG.getMessage("unmute", "MC").replace("{PLAYER}", strings[0]));
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            if (strings.length == 0) {
                MessageHelper.send(console, AuthTG.getMessage("unmuteusage", "MC"));
                return false;
            }

            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(console, AuthTG.getMessage("unmutenotfound", "MC"));
                return false;
            }

            if (!AuthTG.loader.isMuted(targetuuid)) {
                MessageHelper.send(console, AuthTG.getMessage("unmutenotmuted", "MC"));
                return false;
            }

            AuthTG.loader.deleteMute(targetuuid);

            Player target = Bukkit.getPlayer(targetuuid);
            if (target != null) {
                MessageHelper.send(target, AuthTG.getMessage("unmutepl", "MC"));
            }

            MessageHelper.send(console, AuthTG.getMessage("unmute", "MC").replace("{PLAYER}", strings[0]));
        }

        return true;
    }
}