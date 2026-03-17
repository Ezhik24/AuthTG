package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.TwoFactorMethod;
import org.ezhik.authTG.TwoFactorPreferenceRepository;
import org.ezhik.authTG.util.MessageHelper;

import java.util.logging.Level;

public class PreferCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (AuthTG.getDataSource() == null) {
            MessageHelper.send(commandSender, mc("preferstorageunavailable",
                    "<red>Команда /prefer доступна только при включённом MySQL."));
            return true;
        }

        Player player = (Player) commandSender;

        if (!AuthTG.loader.isActive(player.getUniqueId())) {
            MessageHelper.send(player, mc("prefernotactive",
                    "<red>Сначала зарегистрируйтесь."));
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("2fa")) {
            MessageHelper.send(player, mc("preferusage",
                    "<red>Использование: /prefer 2fa <mail|tg|off>"));
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
                MessageHelper.send(player, mc("preferusage",
                        "<red>Использование: /prefer 2fa <mail|tg|off>"));
                return true;
        }
    }

    private boolean handleTelegram(Player player) {
        if (!AuthTG.isTelegramEnabled()) {
            MessageHelper.send(player, mc("prefertgdisabled",
                    "<red>Telegram 2FA сейчас отключён в config.yml (tg: false)."));
            return true;
        }

        if (!AuthTG.loader.getActiveTG(player.getUniqueId())) {
            MessageHelper.send(player, mc("prefertgnotlinked",
                    "<red>Телеграм не привязан."));
            return true;
        }

        AuthTG.loader.setTwofactor(player.getUniqueId(), true);
        TwoFactorPreferenceRepository.set(player.getUniqueId(), TwoFactorMethod.TG);

        MessageHelper.send(player, mc("prefersettg",
                "<green>Теперь предпочтительный метод 2FA: Telegram."));
        return true;
    }

    private boolean handleMail(Player player) {
        if (!AuthTG.loader.isVerifiedEmail(player.getUniqueId())) {
            MessageHelper.send(player, mc("prefermailnotverified",
                    "<red>Сначала привяжите и подтвердите почту через <yellow>/mail link</yellow> и <yellow>/mail verify</yellow>."));
            return true;
        }

        String email = AuthTG.loader.getEmail(player.getUniqueId());
        if (email == null || email.isBlank()) {
            MessageHelper.send(player, mc("prefermailnotverified",
                    "<red>Сначала привяжите и подтвердите почту через <yellow>/mail link</yellow> и <yellow>/mail verify</yellow>."));
            return true;
        }

        AuthTG.loader.setTwofactor(player.getUniqueId(), false);
        TwoFactorPreferenceRepository.set(player.getUniqueId(), TwoFactorMethod.MAIL);

        MessageHelper.send(player, mc("prefersetmail",
                "<green>Теперь предпочтительный метод 2FA: почта <yellow>{EMAIL}<green>.")
                .replace("{EMAIL}", email));
        return true;
    }

    private boolean handleOff(Player player) {
        if (AuthTG.authNecessarily) {
            MessageHelper.send(player, mc("preferoffblocked",
                    "<red>При authNecessarily нельзя отключить 2FA."));
            return true;
        }

        AuthTG.loader.setTwofactor(player.getUniqueId(), false);
        TwoFactorPreferenceRepository.set(player.getUniqueId(), TwoFactorMethod.OFF);

        MessageHelper.send(player, mc("preferoff",
                "<green>Предпочтительный метод 2FA сброшен."));
        return true;
    }

    private String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null || value.isBlank() ? fallback : value;
    }
}