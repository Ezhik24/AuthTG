package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.captcha.Captcha;

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
            player.sendMessage(color(mc("captchanotpending",
                    "&cСейчас капча не требуется.")));
            return true;
        }

        Captcha.openFor(player);
        player.sendMessage(color(mc("captchaopened",
                "&aКапча открыта.")));
        return true;
    }

    private String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null || value.isBlank() ? fallback : value;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
