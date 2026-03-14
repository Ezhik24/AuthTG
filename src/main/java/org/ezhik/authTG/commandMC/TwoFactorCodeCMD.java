package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.TwoFactorAuthService;
import org.ezhik.authTG.mail.MailCodeSession;
import org.ezhik.authTG.mail.MailTwoFactorCodeStore;

import java.util.logging.Level;

public class TwoFactorCodeCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        Player player = (Player) commandSender;

        if (args.length != 1) {
            player.sendMessage(color(mc("mail2fausage",
                    "&cИспользование: /2fa <код>")));
            return true;
        }

        MailCodeSession session = MailTwoFactorCodeStore.get(player.getUniqueId());
        if (session == null) {
            player.sendMessage(color(mc("mail2faexpired",
                    "&cКод 2FA истёк или не был запрошен.")));
            return true;
        }

        if (!MailTwoFactorCodeStore.verify(player.getUniqueId(), args[0].trim())) {
            player.sendMessage(color(mc("mail2fawrong",
                    "&cНеверный код 2FA.")));
            return true;
        }

        TwoFactorAuthService.completeLogin(player);
        return true;
    }

    private String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
