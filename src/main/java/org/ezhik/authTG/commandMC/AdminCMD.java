package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.util.MessageHelper;

public class AdminCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("authtg.admin")) {
            MessageHelper.send(commandSender, AuthTG.getMessage("adminnoperm", "MC"));
            return false;
        }

        if (strings.length == 0) {
            MessageHelper.send(commandSender, AuthTG.getMessage("adminhelp", "MC"));
            return false;
        }

        Player player = commandSender instanceof Player ? (Player) commandSender : null;

        if (strings[0].equalsIgnoreCase("add")) {
            if (strings.length < 2) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminhelpadd", "MC"));
                return false;
            }

            User user = User.getUser(strings[1]);
            if (user == null) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminusernotfound", "MC"));
                return false;
            }

            if (user.isadmin) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminalreadyadmin", "MC"));
                return false;
            }

            AuthTG.loader.setAdmin(user.uuid);

            MessageHelper.send(commandSender, AuthTG.getMessage("adminadded", "MC"));

            if (user.activetg) {
                user.sendMessage(AuthTG.getMessage("adminadd", "TG"));
            }

            if (user.player != null) {
                MessageHelper.send(user.player, AuthTG.getMessage("adminadd", "MC"));
            }

            return true;
        }

        if (strings[0].equalsIgnoreCase("rem")) {
            if (strings.length < 2) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminhelprem", "MC"));
                return false;
            }

            User user = User.getUser(strings[1]);
            if (user == null) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminusernotfound", "MC"));
                return false;
            }

            if (!user.isadmin) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminnotadmin", "MC"));
                return false;
            }

            AuthTG.loader.removeAdmin(user.uuid);

            if (user.player != null) {
                MessageHelper.send(user.player, AuthTG.getMessage("adminrem", "MC"));
            }

            if (user.activetg) {
                user.sendMessage(AuthTG.getMessage("adminrem", "TG"));
            }

            MessageHelper.send(commandSender, AuthTG.getMessage("adminremoved", "MC"));
            return true;
        }

        if (strings[0].equalsIgnoreCase("list")) {
            if (AuthTG.loader.getAdminList().isEmpty()) {
                MessageHelper.send(commandSender, AuthTG.getMessage("adminlistnotfound", "MC"));
                return false;
            }

            MessageHelper.send(commandSender, AuthTG.getMessage("adminlist", "MC"));
            for (String playername : AuthTG.loader.getAdminList()) {
                MessageHelper.send(commandSender,
                        AuthTG.getMessage("adminlistplayer", "MC").replace("{PLAYER}", playername));
            }
            return true;
        }

        MessageHelper.send(commandSender, AuthTG.getMessage("adminhelp", "MC"));
        return false;
    }
}