package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.TwoFactorAuthService;
import org.ezhik.authTG.util.MessageHelper;

import java.util.logging.Level;

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (AuthTG.notRegAndLogin) {
            MessageHelper.send(commandSender, AuthTG.getMessage("loginoff", "MC"));
            return false;
        }

        if (strings.length != 1) {
            MessageHelper.send(commandSender, AuthTG.getMessage("loginnousage", "MC"));
            return false;
        }

        Player player = (Player) commandSender;

        if (!AuthTG.loader.passwordValid(player.getUniqueId(), strings[0])) {
            MessageHelper.send(player, AuthTG.getMessage("loginpassnovalid", "MC"));
            return false;
        }

        User user = User.getUser(player.getUniqueId());
        if (user == null) {
            MessageHelper.send(player, AuthTG.getMessage("loginpassnovalid", "MC"));
            return false;
        }

        if (!AuthTG.loader.containsIpRegistration(player.getUniqueId())) {
            AuthTG.loader.setIpRegistration(
                    player.getUniqueId(),
                    player.getAddress().getAddress().toString()
            );
        }

        if (TwoFactorAuthService.beginSecondFactorOrLogin(player, user)) {
            return true;
        }

        TwoFactorAuthService.completeLogin(player);
        return true;
    }
}