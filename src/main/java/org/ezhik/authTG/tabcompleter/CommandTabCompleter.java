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
                    return List.of("add", "rem", "list");
                } else {
                    return null;
                }
            }
            if (strings.length == 3) {
                if (AuthTG.loader.isAdmin(player.getUniqueId())) {
                    if (strings[0].equals("add") || strings[0].equals("rem")) {
                        return List.of("ban", "mute", "kick");
                    }
                } else {
                    return null;
                }
            }
        } else {
            if (strings.length == 1) {
                return List.of("add", "rem", "list");
            }
            if (strings.length == 3) {
                if (strings[0].equals("add") || strings[0].equals("rem")) {
                    return List.of("ban", "mute", "kick");
                }
            }
        }
        return null;
    }
}
