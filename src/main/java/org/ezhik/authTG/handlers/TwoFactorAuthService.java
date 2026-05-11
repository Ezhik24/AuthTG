package org.ezhik.authTG.handlers;

import com.sun.mail.imap.protocol.BODY;
import org.bukkit.Bukkit;
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
import org.ezhik.authTG.util.MessageHelper;

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
        boolean vkAvailable = canUseVK(user);
        boolean hasAnyAvailableMethod = telegramAvailable || mailAvailable || vkAvailable;

        if (!hasAnyAvailableMethod) {
            if (AuthTG.authNecessarily) {
                return blockRequiredTwoFactorWithoutMethod(player);
            }
            return false;
        }

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
                    if (vkAvailable) {
                        return beginVKChallenge(player, user);
                    }
                    break;

                case MAIL:
                    if (mailAvailable) {
                        return beginMailChallenge(player);
                    }
                    if (telegramAvailable) {
                        return beginTelegramChallenge(player, user);
                    }
                    if (vkAvailable) {
                        return beginVKChallenge(player, user);
                    }
                    break;
                case VK:
                    if (vkAvailable) {
                        return beginVKChallenge(player, user);
                    }
                    if (telegramAvailable) {
                        return beginTelegramChallenge(player, user);
                    }
                    if (mailAvailable) {
                        return beginMailChallenge(player);
                    }
                    break;

                case OFF:
                    if (AuthTG.authNecessarily) {
                        if (telegramAvailable) {
                            return beginTelegramChallenge(player, user);
                        }
                        if (mailAvailable) {
                            return beginMailChallenge(player);
                        }
                        if (vkAvailable) {
                            return beginVKChallenge(player, user);
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
            if (vkAvailable) {
                return beginVKChallenge(player, user);
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
            if (vkAvailable) {
                return beginVKChallenge(player, user);
            }
            return false;
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
            MessageHelper.send(player, mc("mail2faunavailable",
                    "<red>Почтовая 2FA сейчас недоступна."));
            return true;
        }

        if (!MailDeliveryService.isEnabled() || !MailDeliveryService.hasValidProvider()) {
            MessageHelper.send(player, mc("mail2faunavailable",
                    "<red>Почтовая 2FA сейчас недоступна."));
            return true;
        }

        MuterEvent.mute(player.getName(), MessageHelper.legacySection(mc("mail2fawaittext",
                "<green>Подтвердите вход кодом из письма.")));

        MessageHelper.showTitle(
                player,
                mc("mail2fawaittitle", "<red><bold>Подтвердите вход"),
                mc("mail2fawaitsubtitle", "<gray>/2fa <код из письма>")
        );

        String code = MailTwoFactorCodeStore.generateCode(MailDeliveryService.getCodeLength());

        if (MailDeliveryService.isLocalMode()) {
            MailTwoFactorCodeStore.create(uuid, email, code, MailDeliveryService.getCodeExpireSeconds());
            MessageHelper.send(player, mc("mail2fasent",
                    "<green>Код 2FA отправлен на <yellow>{EMAIL}<green>.").replace("{EMAIL}", email));
            MessageHelper.send(player, mc("mail2fainput",
                    "<green>Введите <yellow>/2fa <код><green>."));
            MessageHelper.send(player, mc("mail2falocalcode",
                    "<yellow>[LOCAL] Код 2FA: {CODE}").replace("{CODE}", code));
            return true;
        }

        String playerName = player.getName();
        String ip = getPlayerIp(player);

        MessageHelper.send(player, mc("mail2fasending",
                "<green>Отправляем код 2FA на <yellow>{EMAIL}<green>...").replace("{EMAIL}", email));

        Bukkit.getScheduler().runTaskAsynchronously(AuthTG.getInstance(), () -> {
            boolean sent = MailDeliveryService.sendTwoFactorCode(playerName, uuid, ip, email, code);

            Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
                Player online = Bukkit.getPlayer(uuid);
                if (online == null) {
                    return;
                }

                if (sent) {
                    MailTwoFactorCodeStore.create(uuid, email, code, MailDeliveryService.getCodeExpireSeconds());
                    MessageHelper.send(online, mc("mail2fasent",
                            "<green>Код 2FA отправлен на <yellow>{EMAIL}<green>.").replace("{EMAIL}", email));
                    MessageHelper.send(online, mc("mail2fainput",
                            "<green>Введите <yellow>/2fa <код><green>."));
                } else {
                    MailTwoFactorCodeStore.remove(uuid);
                    MessageHelper.send(online, mc("mail2fasenderror",
                            "<red>Не удалось отправить письмо с кодом 2FA."));
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

        String waitText = mc("joininaccounttext", "<green>Подтвердите вход в Telegram.");
        MuterEvent.mute(player.getName(), MessageHelper.legacySection(waitText));
        MessageHelper.send(player, waitText);
        MessageHelper.showTitle(
                player,
                mc("joininaccounts1", "<red><bold>Подтвердите вход"),
                mc("joininaccounts2", "<gray>Откройте Telegram")
        );
        return true;
    }

    public static boolean beginVKChallenge(Player player, User user) {
        if (player == null || user == null) {
            return false;
        }
        AuthTG.vk.sendLoginAccept(user.peerid, AuthTG.getMessage("vkloginaccepted", "VK").replace("{PLAYER}", user.playername), player.getUniqueId());

        String waitText = mc("joininaccounttextvk", "<green>Подтвердите вход в VK.");
        MuterEvent.mute(player.getName(), MessageHelper.legacySection(waitText));
        MessageHelper.send(player, waitText);
        MessageHelper.showTitle(
                player,
                mc("joininaccounts1vk", "<red><bold>Подтвердите вход"),
                mc("joininaccounts2vk", "<gray>Откройте VK")
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
                    friendUser.sendMessage(AuthTG.getMessage("friendjoin", "TG")
                            .replace("{PLAYER}", user.playername));
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

        MessageHelper.send(player, mc("loginsuccess", "<green>Вы успешно вошли."));

        FreezerEvent.unfreezeplayer(player.getName());

        if (FreezerEvent.beforeFreeze.containsKey(player.getName())) {
            Handler.teleport(player.getName(), FreezerEvent.beforeFreeze.get(player.getName()));
            FreezerEvent.beforeFreeze.remove(player.getName());
        }

        MuterEvent.unmute(player.getName());
        player.clearTitle();
        MailTwoFactorCodeStore.remove(player.getUniqueId());

        if (AuthTG.kickTimeout != 0) {
            AuthHandler.removeTimeout(player.getUniqueId());
        }
    }

    private static boolean blockRequiredTwoFactorWithoutMethod(Player player) {
        String text = mc("twofactorrequirednomethod",
                "<red>На сервере требуется 2FA, но у вас не настроен ни Telegram, ни подтверждённая почта.");

        MuterEvent.mute(player.getName(), MessageHelper.legacySection(text));
        MessageHelper.send(player, text);
        MessageHelper.showTitle(
                player,
                mc("twofactorrequiredtitle", "<red><bold>Требуется 2FA"),
                mc("twofactorrequiredsubtitle", "<gray>Привяжите Telegram или подтвердите почту")
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
    private static boolean canUseVK(User user) {
        return AuthTG.isVKEnabled() && user != null && user.activevk;
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
}