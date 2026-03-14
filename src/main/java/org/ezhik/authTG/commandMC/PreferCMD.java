package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.TwoFactorMethod;
import org.ezhik.authTG.TwoFactorPreferenceRepository;

import java.util.logging.Level;

public class PreferCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (AuthTG.getDataSource() == null) {
            commandSender.sendMessage(color(mc("preferstorageunavailable",
                    "&cКоманда /prefer доступна только при включённом MySQL.")));
            return true;
        }

        Player player = (Player) commandSender;

        if (!AuthTG.loader.isActive(player.getUniqueId())) {
            player.sendMessage(color(mc("prefernotactive",
                    "&cСначала зарегистрируйтесь.")));
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("2fa")) {
            player.sendMessage(color(mc("preferusage",
                    "&cИспользование: /prefer 2fa <mail|tg|off>")));
            return true;
        }

        String methodRaw = args[1].toLowerCase();

        switch (methodRaw) {
            case "tg":
                return handleTelegram(player);
            case "mail":
                return handleMail(player);
            case "off":
            case "none":
                return handleOff(player);
            default:
                player.sendMessage(color(mc("preferusage",
                        "&cИспользование: /prefer 2fa <mail|tg|off>")));
                return true;
        }
    }

    private boolean handleTelegram(Player player) {
        if (!AuthTG.isTelegramEnabled()) {
            player.sendMessage(color(mc("prefertgdisabled",
                    "&cTelegram 2FA сейчас отключён в config.yml (tg: false).")));
            return true;
        }

        if (!AuthTG.loader.getActiveTG(player.getUniqueId())) {
            player.sendMessage(color(mc("prefertgnotlinked",
                    "&cТелеграм не привязан.")));
            return true;
        }

        AuthTG.loader.setTwofactor(player.getUniqueId(), true);
        TwoFactorPreferenceRepository.set(player.getUniqueId(), TwoFactorMethod.TG);

        player.sendMessage(color(mc("prefersettg",
                "&aТеперь предпочтительный метод 2FA: Telegram.")));
        return true;
    }

    private boolean handleMail(Player player) {
        if (!AuthTG.loader.isVerifiedEmail(player.getUniqueId())) {
            player.sendMessage(color(mc("prefermailnotverified",
                    "&cСначала привяжите и подтвердите почту через /mail link и /mail verify.")));
            return true;
        }

        String email = AuthTG.loader.getEmail(player.getUniqueId());
        if (email == null || email.isBlank()) {
            player.sendMessage(color(mc("prefermailnotverified",
                    "&cСначала привяжите и подтвердите почту через /mail link и /mail verify.")));
            return true;
        }

        AuthTG.loader.setTwofactor(player.getUniqueId(), false);
        TwoFactorPreferenceRepository.set(player.getUniqueId(), TwoFactorMethod.MAIL);

        player.sendMessage(color(mc("prefersetmail",
                "&aТеперь предпочтительный метод 2FA: почта &e{EMAIL}&a.")
                .replace("{EMAIL}", email)));
        return true;
    }

    private boolean handleOff(Player player) {
        if (AuthTG.authNecessarily) {
            player.sendMessage(color(mc("preferoffblocked",
                    "&cПри authNecessarily нельзя отключить 2FA.")));
            return true;
        }

        AuthTG.loader.setTwofactor(player.getUniqueId(), false);
        TwoFactorPreferenceRepository.set(player.getUniqueId(), TwoFactorMethod.OFF);

        player.sendMessage(color(mc("preferoff",
                "&aПредпочтительный метод 2FA сброшен.")));
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
