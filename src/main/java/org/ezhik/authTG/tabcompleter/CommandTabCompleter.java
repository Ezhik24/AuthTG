package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length == 1) {
                if (AuthTG.loader.isAdmin(player.getUniqueId())) {
                    return TabCompleteHelper.filter(List.of("add", "rem", "list"), strings[0]);
                } else {
                    return List.of();
                }
            }
            if (strings.length == 2 && (strings[0].equalsIgnoreCase("add") || strings[0].equalsIgnoreCase("rem"))) {
                return TabCompleteHelper.onlinePlayers(strings[1]);
            }
            if (strings.length == 3) {
                if (AuthTG.loader.isAdmin(player.getUniqueId())) {
                    if (strings[0].equals("add") || strings[0].equals("rem")) {
                        return TabCompleteHelper.filter(List.of("ban", "mute", "kick"), strings[2]);
                    }
                } else {
                    return List.of();
                }
            }
        } else {
            if (strings.length == 1) {
                return TabCompleteHelper.filter(List.of("add", "rem", "list"), strings[0]);
            }
            if (strings.length == 2 && (strings[0].equalsIgnoreCase("add") || strings[0].equalsIgnoreCase("rem"))) {
                return TabCompleteHelper.onlinePlayers(strings[1]);
            }
            if (strings.length == 3) {
                if (strings[0].equals("add") || strings[0].equals("rem")) {
                    return TabCompleteHelper.filter(List.of("ban", "mute", "kick"), strings[2]);
                }
            }
        }
        return List.of();
    }
}
