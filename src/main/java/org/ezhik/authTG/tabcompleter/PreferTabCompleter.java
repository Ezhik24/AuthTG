package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PreferTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("2fa"), args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("2fa")) {
            return filter(Arrays.asList("mail", "tg", "off"), args[1]);
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> source, String input) {
        List<String> result = new ArrayList<>();
        String lowerInput = input == null ? "" : input.toLowerCase();

        for (String value : source) {
            if (value.startsWith(lowerInput)) {
                result.add(value);
            }
        }

        return result;
    }
}
