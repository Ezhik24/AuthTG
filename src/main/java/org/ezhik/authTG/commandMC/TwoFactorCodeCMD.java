package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.TwoFactorAuthService;
import org.ezhik.authTG.mail.MailCodeSession;
import org.ezhik.authTG.mail.MailTwoFactorCodeStore;
import org.ezhik.authTG.util.MessageHelper;

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
            MessageHelper.send(player, mc("mail2fausage",
                    "<red>Использование: /2fa <код>"));
            return true;
        }

        MailCodeSession session = MailTwoFactorCodeStore.get(player.getUniqueId());
        if (session == null) {
            MessageHelper.send(player, mc("mail2faexpired",
                    "<red>Код 2FA истёк или не был запрошен."));
            return true;
        }

        if (!MailTwoFactorCodeStore.verify(player.getUniqueId(), args[0].trim())) {
            MessageHelper.send(player, mc("mail2fawrong",
                    "<red>Неверный код 2FA."));
            return true;
        }

        TwoFactorAuthService.completeLogin(player);
        return true;
    }

    private String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return (value == null || value.isBlank()) ? fallback : value;
    }
}