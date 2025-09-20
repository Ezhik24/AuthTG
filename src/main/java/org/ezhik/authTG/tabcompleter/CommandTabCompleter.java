package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return List.of("add", "rem", "list");
        }
        if (strings.length == 3) {
            if (strings[0].equals("add") || strings[0].equals("rem")) {
                return List.of("ban", "mute", "kick");
            }
        }
        return null;
    }
}
