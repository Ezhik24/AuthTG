package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class PreferTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return TabCompleteHelper.filter(List.of("2fa"), args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("2fa")) {
            return TabCompleteHelper.filter(List.of("mail", "tg", "vk", "off"), args[1]);
        }

        return Collections.emptyList();
    }
}
