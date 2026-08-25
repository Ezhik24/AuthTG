package org.ezhik.authTG.tabcompleter;

import org.bukkit.Bukkit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

final class TabCompleteHelper {
    private TabCompleteHelper() {}

    static List<String> filter(Collection<String> suggestions, String input) {
        String prefix = input == null ? "" : input.toLowerCase(Locale.ROOT);
        return suggestions.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix))
                .sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    static List<String> onlinePlayers(String input) {
        String prefix = input == null ? "" : input.toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream().map(player -> player.getName())
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                .sorted(Comparator.comparing(name -> name.toLowerCase(Locale.ROOT))).toList();
    }
}
