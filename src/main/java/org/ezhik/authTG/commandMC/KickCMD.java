package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.util.MessageHelper;

import java.util.UUID;

public class KickCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (!player.hasPermission("authtg.kick")) {
                MessageHelper.send(player, AuthTG.getMessage("kicknoperm", "MC"));
                return false;
            }
            if (strings.length == 0) {
                MessageHelper.send(player, AuthTG.getMessage("kickusage", "MC"));
                return false;
            }
            if (strings.length < 2) {
                MessageHelper.send(player, AuthTG.getMessage("kickusage", "MC"));
                return false;
            }

            String reason = String.join(" ", strings).substring(strings[0].length() + 1);
            UUID uuidtarget = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (uuidtarget == null) {
                MessageHelper.send(player, AuthTG.getMessage("kicknotfound", "MC"));
                return false;
            }

            Player target = Bukkit.getPlayer(uuidtarget);
            if (target == null) {
                MessageHelper.send(player, AuthTG.getMessage("kicknotonline", "MC"));
                return false;
            }

            if (reason.isEmpty()) {
                MessageHelper.send(player, AuthTG.getMessage("kicknotreason", "MC"));
                return false;
            }

            Handler.kick(target.getName(), MessageHelper.legacySection(reason));
            MessageHelper.send(player, AuthTG.getMessage("kicksuccess", "MC").replace("{PLAYER}", target.getName()));
            return true;
        } else {
            ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();

            if (strings.length == 0) {
                MessageHelper.send(consoleCommandSender, AuthTG.getMessage("kickusage", "MC"));
                return false;
            }
            if (strings.length < 2) {
                MessageHelper.send(consoleCommandSender, AuthTG.getMessage("kickusage", "MC"));
                return false;
            }

            String reason = String.join(" ", strings).substring(strings[0].length() + 1);
            UUID uuidtarget = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (uuidtarget == null) {
                MessageHelper.send(consoleCommandSender, AuthTG.getMessage("kicknotfound", "MC"));
                return false;
            }

            Player target = Bukkit.getPlayer(uuidtarget);
            if (target == null) {
                MessageHelper.send(consoleCommandSender, AuthTG.getMessage("kicknotonline", "MC"));
                return false;
            }

            if (reason.isEmpty()) {
                MessageHelper.send(consoleCommandSender, AuthTG.getMessage("kicknotreason", "MC"));
                return false;
            }

            Handler.kick(target.getName(), MessageHelper.legacySection(reason));
            MessageHelper.send(consoleCommandSender, AuthTG.getMessage("kicksuccess", "MC").replace("{PLAYER}", target.getName()));
            return true;
        }
    }
}