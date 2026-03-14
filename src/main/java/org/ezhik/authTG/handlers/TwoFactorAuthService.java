package org.ezhik.authTG.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.IPManager;
import org.ezhik.authTG.TwoFactorMethod;
import org.ezhik.authTG.TwoFactorPreferenceRepository;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.mail.MailDeliveryService;
import org.ezhik.authTG.mail.MailTwoFactorCodeStore;

import java.time.LocalDateTime;
import java.util.UUID;

public final class TwoFactorAuthService {

    private TwoFactorAuthService() {
    }

    public static boolean beginSecondFactorOrLogin(Player player, User user) {
        if (player == null || user == null) {
            return false;
        }

        UUID uuid = player.getUniqueId();
        boolean telegramAvailable = canUseTelegram(user);
        boolean mailAvailable = canUseMail(uuid);

        TwoFactorMethod preferred = TwoFactorPreferenceRepository.get(uuid);

        if (preferred != null) {
            switch (preferred) {
                case TG:
                    if (telegramAvailable) {
                        return beginTelegramChallenge(player, user);
                    }
                    if (mailAvailable) {
                        return beginMailChallenge(player);
                    }
                    return blockUnavailableConfiguredTwoFactor(player);

                case MAIL:
                    if (mailAvailable) {
                        return beginMailChallenge(player);
                    }
                    if (telegramAvailable) {
                        return beginTelegramChallenge(player, user);
                    }
                    return blockUnavailableConfiguredTwoFactor(player);

                case OFF:
                    if (AuthTG.authNecessarily) {
                        if (telegramAvailable) {
                            return beginTelegramChallenge(player, user);
                        }
                        if (mailAvailable) {
                            return beginMailChallenge(player);
                        }
                        return blockRequiredTwoFactorWithoutMethod(player);
                    }
                    return false;
            }
        }

        if (AuthTG.authNecessarily) {
            if (telegramAvailable) {
                return beginTelegramChallenge(player, user);
            }
            if (mailAvailable) {
                return beginMailChallenge(player);
            }
            return blockRequiredTwoFactorWithoutMethod(player);
        }

        if (user.twofactor) {
            if (telegramAvailable) {
                return beginTelegramChallenge(player, user);
            }
            if (mailAvailable) {
                return beginMailChallenge(player);
            }
            return blockUnavailableConfiguredTwoFactor(player);
        }

        return false;
    }

    public static boolean beginMailChallenge(Player player) {
        if (player == null) {
            return false;
        }

        UUID uuid = player.getUniqueId();
        String email = AuthTG.loader.getEmail(uuid);

        if (email == null || email.isBlank()) {
            player.sendMessage(color(mc("mail2faunavailable",
                    "&cПочтовая 2FA сейчас недоступна.")));
            return true;
        }

        if (!MailDeliveryService.isEnabled() || !MailDeliveryService.hasValidProvider()) {
            player.sendMessage(color(mc("mail2faunavailable",
                    "&cПочтовая 2FA сейчас недоступна.")));
            return true;
        }

        MuterEvent.mute(player.getName(), color(mc("mail2fawaittext",
                "&aПодтвердите вход кодом из письма.")));
        player.sendTitle(
                color(mc("mail2fawaittitle", "&c&lПодтвердите вход")),
                mc("mail2fawaitsubtitle", "/2fa <код из письма>"),
                0,
                1000000000,
                0
        );

        String code = MailTwoFactorCodeStore.generateCode(MailDeliveryService.getCodeLength());

        if (MailDeliveryService.isLocalMode()) {
            MailTwoFactorCodeStore.create(uuid, email, code, MailDeliveryService.getCodeExpireSeconds());
            player.sendMessage(color(mc("mail2fasent",
                    "&aКод 2FA отправлен на &e{EMAIL}&a.").replace("{EMAIL}", email)));
            player.sendMessage(color(mc("mail2fainput",
                    "&aВведите &e/2fa <код>&a.")));
            player.sendMessage(color(mc("mail2falocalcode",
                    "&e[LOCAL] Код 2FA: {CODE}").replace("{CODE}", code)));
            return true;
        }

        String playerName = player.getName();
        String ip = getPlayerIp(player);

        player.sendMessage(color(mc("mail2fasending",
                "&aОтправляем код 2FA на &e{EMAIL}&a...").replace("{EMAIL}", email)));

        Bukkit.getScheduler().runTaskAsynchronously(AuthTG.getInstance(), () -> {
            boolean sent = MailDeliveryService.sendTwoFactorCode(playerName, uuid, ip, email, code);

            Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
                Player online = Bukkit.getPlayer(uuid);
                if (online == null) {
                    return;
                }

                if (sent) {
                    MailTwoFactorCodeStore.create(uuid, email, code, MailDeliveryService.getCodeExpireSeconds());
                    online.sendMessage(color(mc("mail2fasent",
                            "&aКод 2FA отправлен на &e{EMAIL}&a.").replace("{EMAIL}", email)));
                    online.sendMessage(color(mc("mail2fainput",
                            "&aВведите &e/2fa <код>&a.")));
                } else {
                    MailTwoFactorCodeStore.remove(uuid);
                    online.sendMessage(color(mc("mail2fasenderror",
                            "&cНе удалось отправить письмо с кодом 2FA.")));
                }
            });
        });

        return true;
    }

    public static boolean beginTelegramChallenge(Player player, User user) {
        if (player == null || user == null) {
            return false;
        }

        user.sendLoginAcceptedAsync(buildTelegramMessage(player, user));

        MuterEvent.mute(player.getName(),
                color(mc("joininaccounttext", "&aПодтвердите вход в Telegram.")));
        player.sendMessage(color(mc("joininaccounttext", "&aПодтвердите вход в Telegram.")));
        player.sendTitle(
                color(mc("joininaccounts1", "&c&lПодтвердите вход")),
                mc("joininaccounts2", "&7Откройте Telegram"),
                0,
                1000000000,
                0
        );
        return true;
    }

    public static void completeLogin(Player player) {
        if (player == null) {
            return;
        }

        User user = User.getUser(player.getUniqueId());

        if (user != null && user.activetg && user.friends != null) {
            for (String friend : user.friends) {
                User friendUser = User.getUser(friend);
                if (friendUser != null && friendUser.activetg) {
                    friendUser.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("friendjoin", "TG")
                                    .replace("{PLAYER}", user.playername)));
                } else if (friendUser != null) {
                    AuthTG.loader.removeFriend(friendUser.uuid, user.playername);
                    AuthTG.loader.removeFriend(user.uuid, friendUser.playername);
                }
            }
        }

        LocalDateTime time = LocalDateTime.now().plusMinutes(AuthTG.timeoutSession);
        IPManager.addAuthorized(
                player.getUniqueId(),
                player.getAddress().getAddress().toString(),
                time
        );

        player.sendMessage(color(mc("loginsuccess", "&aВы успешно вошли.")));

        FreezerEvent.unfreezeplayer(player.getName());

        if (FreezerEvent.beforeFreeze.containsKey(player.getName())) {
            Handler.teleport(player.getName(), FreezerEvent.beforeFreeze.get(player.getName()));
            FreezerEvent.beforeFreeze.remove(player.getName());
        }

        MuterEvent.unmute(player.getName());
        player.resetTitle();
        MailTwoFactorCodeStore.remove(player.getUniqueId());

        if (AuthTG.kickTimeout != 0) {
            AuthHandler.removeTimeout(player.getUniqueId());
        }
    }

    private static boolean blockUnavailableConfiguredTwoFactor(Player player) {
        player.sendMessage(color(mc("prefer2faunavailable",
                "&cВыбранный метод 2FA сейчас недоступен, и запасного метода тоже нет.")));
        return true;
    }

    private static boolean blockRequiredTwoFactorWithoutMethod(Player player) {
        String text = color(mc("twofactorrequirednomethod",
                "&cНа сервере требуется 2FA, но у вас не настроен ни Telegram, ни подтверждённая почта."));

        MuterEvent.mute(player.getName(), text);
        player.sendMessage(text);
        player.sendTitle(
                color(mc("twofactorrequiredtitle", "&c&lТребуется 2FA")),
                mc("twofactorrequiredsubtitle", "&7Привяжите Telegram или подтвердите почту"),
                0,
                1000000000,
                0
        );
        return true;
    }

    private static boolean canUseTelegram(User user) {
        return AuthTG.isTelegramEnabled() && user != null && user.activetg;
    }

    private static boolean canUseMail(UUID uuid) {
        if (!MailDeliveryService.isEnabled() || !MailDeliveryService.hasValidProvider()) {
            return false;
        }

        if (!AuthTG.loader.isVerifiedEmail(uuid)) {
            return false;
        }

        String email = AuthTG.loader.getEmail(uuid);
        return email != null && !email.isBlank();
    }

    private static String buildTelegramMessage(Player player, User user) {
        String ip = "unknown";
        if (player.getAddress() != null && player.getAddress().getAddress() != null) {
            ip = player.getAddress().getAddress().getHostAddress();
        }

        return AuthTG.getMessage("loginaccept", "TG")
                .replace("{PLAYER}", user.playername)
                .replace("{IP}", ip);
    }

    private static String getPlayerIp(Player player) {
        if (player.getAddress() != null && player.getAddress().getAddress() != null) {
            return player.getAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }

    private static String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
