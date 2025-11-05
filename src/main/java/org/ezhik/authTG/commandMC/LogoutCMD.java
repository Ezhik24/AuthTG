package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.jetbrains.annotations.NotNull;

public class LogoutCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Handler.kick(commandSender.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("logout", "MC")));
        AuthTG.sessionManager.deleteAuthorized(((Player) commandSender).getUniqueId());
        return true;
    }
}
