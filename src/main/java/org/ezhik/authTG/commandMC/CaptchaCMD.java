package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.captcha.Captcha;
import org.ezhik.authTG.util.MessageHelper;

import java.util.logging.Level;

public class CaptchaCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        Player player = (Player) commandSender;

        if (!Captcha.isPending(player.getUniqueId())) {
            MessageHelper.send(player, mc("captchanotpending",
                    "<red>Сейчас капча не требуется."));
            return true;
        }

<<<<<<< HEAD
        Captcha.openFor(player);
        MessageHelper.send(player, mc("captchaopened",
                "<green>Открываем капчу..."));
=======
        if (Captcha.isTextMode() && args.length > 0) {
            Captcha.checkText(player, args[0]);
        } else {
            Captcha.openFor(player);
            MessageHelper.send(player, mc("captchaopened", "<green>Открываем капчу..."));
        }
>>>>>>> 1d55b6e (3.2)
        return true;
    }

    private String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null || value.isBlank() ? fallback : value;
    }
}