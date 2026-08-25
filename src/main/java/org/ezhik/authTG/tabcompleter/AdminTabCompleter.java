package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class AdminTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            if (commandSender.hasPermission("authTG.admin")) {
                return TabCompleteHelper.filter(List.of("add", "rem", "list"), strings[0]);
            }
        }
        if (strings.length == 2 && (strings[0].equalsIgnoreCase("add") || strings[0].equalsIgnoreCase("rem"))) {
            return TabCompleteHelper.onlinePlayers(strings[1]);
        }
        return List.of();
    }
}
