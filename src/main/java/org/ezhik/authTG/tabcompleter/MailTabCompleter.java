package org.ezhik.authTG.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ezhik.authTG.mail.MailDeliveryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MailTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "link",
            "verify",
            "unlink",
            "status"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!MailDeliveryService.isEnabled()) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return filterStartsWith(SUBCOMMANDS, args[0]);
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();

            if (sub.equals("link")) {
                return filterStartsWith(Collections.singletonList("<email>"), args[1]);
            }

            if (sub.equals("verify")) {
                return filterStartsWith(Collections.singletonList("<code>"), args[1]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filterStartsWith(List<String> source, String input) {
        List<String> result = new ArrayList<>();
        String lowerInput = input == null ? "" : input.toLowerCase();

        for (String value : source) {
            if (value.toLowerCase().startsWith(lowerInput)) {
                result.add(value);
            }
        }

        return result;
    }
}