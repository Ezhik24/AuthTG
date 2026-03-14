package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.TwoFactorAuthService;

import java.util.logging.Level;

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (AuthTG.notRegAndLogin) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    AuthTG.getMessage("loginoff", "MC")));
            return false;
        }

        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    AuthTG.getMessage("loginnousage", "MC")));
            return false;
        }

        Player player = (Player) commandSender;

        if (!AuthTG.loader.passwordValid(player.getUniqueId(), strings[0])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    AuthTG.getMessage("loginpassnovalid", "MC")));
            return false;
        }

        User user = User.getUser(player.getUniqueId());
        if (user == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    AuthTG.getMessage("loginpassnovalid", "MC")));
            return false;
        }

        if (!AuthTG.loader.containsIpRegistration(player.getUniqueId())) {
            AuthTG.loader.setIpRegistration(
                    player.getUniqueId(),
                    player.getAddress().getAddress().toString()
            );
        }

        // Если тут вернулся true - значит уже запущен второй фактор
        // или вход заблокирован из-за обязательного 2FA без доступного метода.
        if (TwoFactorAuthService.beginSecondFactorOrLogin(player, user)) {
            return true;
        }

        // Иначе логиним сразу.
        TwoFactorAuthService.completeLogin(player);
        return true;
    }
}
