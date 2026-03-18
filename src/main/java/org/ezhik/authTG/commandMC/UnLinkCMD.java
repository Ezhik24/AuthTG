package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;
import org.jetbrains.annotations.NotNull;

public class UnLinkCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.unlink")) {
                MessageHelper.send(commandSender, AuthTG.getMessage("unlinknoperm", "MC"));
                return false;
            }

            if (strings.length != 1) {
                MessageHelper.send(commandSender, AuthTG.getMessage("unlinkusage", "MC"));
                return false;
            }

            AuthTG.loader.setActiveTG(AuthTG.loader.getUUIDbyPlayerName(strings[0]), false);

            Player target = Bukkit.getPlayer(strings[0]);
            if (target != null) {
                MessageHelper.send(target, AuthTG.getMessage("unlinkpl", "MC"));
            }

            MessageHelper.send(commandSender, AuthTG.getMessage("unlinksuccess", "MC"));
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            if (strings.length != 1) {
                MessageHelper.send(console, AuthTG.getMessage("unlinkusage", "MC"));
                return false;
            }

            AuthTG.loader.setActiveTG(AuthTG.loader.getUUIDbyPlayerName(strings[0]), false);
            MessageHelper.send(console, AuthTG.getMessage("unlinksuccess", "MC"));
        }

        return true;
    }
}