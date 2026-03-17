package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.util.MessageHelper;

public class CommandCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        if (!AuthTG.loader.isAdmin(player.getUniqueId())) {
            MessageHelper.send(player, AuthTG.getMessage("cmdnoperm", "MC"));
            return false;
        }

        if (strings.length == 0) {
            MessageHelper.send(player, AuthTG.getMessage("cmdhelp", "MC"));
            return false;
        }

        if (strings[0].equals("add")) {
            if (strings.length != 3) {
                MessageHelper.send(player, AuthTG.getMessage("cmdhelpadd", "MC"));
                return false;
            }

            if (strings[2].equals("ban") || strings[2].equals("mute") || strings[2].equals("kick")) {
                User user = User.getUser(strings[1]);
                if (user == null) {
                    MessageHelper.send(player, AuthTG.getMessage("cmdusernotfound", "MC"));
                    return false;
                }

                if (user.commands != null && user.commands.contains(strings[2])) {
                    MessageHelper.send(player, AuthTG.getMessage("cmdaddalready", "MC"));
                    return false;
                }

                AuthTG.loader.addCommand(user.uuid, strings[2]);

                if (user.player != null) {
                    MessageHelper.send(user.player, AuthTG.getMessage("cmdadded", "MC").replace("{COMMAND}", strings[2]));
                }

                if (user.activetg) {
                    user.sendMessage(AuthTG.getMessage("cmdadded", "TG").replace("{COMMAND}", strings[2]));
                }

                MessageHelper.send(player, AuthTG.getMessage("cmdadd", "MC")
                        .replace("{COMMAND}", strings[2])
                        .replace("{PLAYER}", strings[1]));
            } else {
                MessageHelper.send(player, AuthTG.getMessage("cmdhelpadd", "MC"));
            }
        } else if (strings[0].equals("rem")) {
            if (strings.length != 3) {
                MessageHelper.send(player, AuthTG.getMessage("cmdhelprem", "MC"));
                return false;
            }

            if (strings[2].equals("ban") || strings[2].equals("mute") || strings[2].equals("kick")) {
                User user = User.getUser(strings[1]);
                if (user == null) {
                    MessageHelper.send(player, AuthTG.getMessage("cmdusernotfound", "MC"));
                    return false;
                }

                if (user.commands != null && !user.commands.contains(strings[2])) {
                    MessageHelper.send(player, AuthTG.getMessage("cmdremnot", "MC"));
                    return false;
                }

                AuthTG.loader.removeCommand(user.uuid, strings[2]);

                if (user.player != null) {
                    MessageHelper.send(user.player, AuthTG.getMessage("cmdrem", "MC").replace("{COMMAND}", strings[2]));
                }

                if (user.activetg) {
                    user.sendMessage(AuthTG.getMessage("cmdrem", "TG").replace("{COMMAND}", strings[2]));
                }

                MessageHelper.send(player, AuthTG.getMessage("cmdrem", "MC")
                        .replace("{COMMAND}", strings[2])
                        .replace("{PLAYER}", strings[1]));
            } else {
                MessageHelper.send(player, AuthTG.getMessage("cmdhelprem", "MC"));
            }
        } else if (strings[0].equals("list")) {
            if (strings.length != 2) {
                MessageHelper.send(player, AuthTG.getMessage("cmdhelplist", "MC"));
                return false;
            }

            User user = User.getUser(strings[1]);
            if (user == null) {
                MessageHelper.send(player, AuthTG.getMessage("cmdusernotfound", "MC"));
                return false;
            }

            if (user.commands == null || user.commands.isEmpty()) {
                MessageHelper.send(player, AuthTG.getMessage("cmdlistempty", "MC").replace("{PLAYER}", strings[1]));
                return false;
            }

            String commands = user.commands.toString().replace("[", "").replace("]", "");
            MessageHelper.send(player, AuthTG.getMessage("cmdlist", "MC")
                    .replace("{PLAYER}", strings[1])
                    .replace("{COMMANDS}", commands));
        } else {
            MessageHelper.send(player, AuthTG.getMessage("cmdhelp", "MC"));
            return false;
        }

        return true;
    }
}