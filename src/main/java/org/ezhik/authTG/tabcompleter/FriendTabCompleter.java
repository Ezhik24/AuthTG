package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class FriendTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            return TabCompleteHelper.filter(List.of("add", "rem", "list", "tell"), strings[0]);
        }
        if (strings.length == 2 && !strings[0].equalsIgnoreCase("list")) return TabCompleteHelper.onlinePlayers(strings[1]);
        return List.of();
    }
}
