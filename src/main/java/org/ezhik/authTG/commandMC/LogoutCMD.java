package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.IPManager;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.util.MessageHelper;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class LogoutCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        Player player = (Player) commandSender;
        Handler.kick(player.getName(), MessageHelper.legacySection(AuthTG.getMessage("logout", "MC")));
        IPManager.deleteAuthorized(player.getUniqueId());
        return true;
    }
}