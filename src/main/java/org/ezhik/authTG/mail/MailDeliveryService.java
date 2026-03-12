package org.ezhik.authTG.mail;

import org.ezhik.authTG.AuthTG;

import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public final class MailDeliveryService {

    public enum Provider {
        LOCAL,
        API,
        SMTP,
        INVALID
    }

    private MailDeliveryService() {
    }

    public static boolean isEnabled() {
        return AuthTG.getInstance().getConfig().getBoolean("mail.enabled", false);
    }

    public static int getCodeLength() {
        return Math.max(4, AuthTG.getInstance().getConfig().getInt("mail.codeLength", 6));
    }

    public static int getCodeExpireSeconds() {
        return Math.max(30, AuthTG.getInstance().getConfig().getInt("mail.codeExpireSeconds", 300));
    }

    public static Provider getProvider() {
        String providerRaw = AuthTG.getInstance().getConfig().getString("mail.provider", "LOCAL");

        switch (providerRaw.trim().toUpperCase(Locale.ROOT)) {
            case "LOCAL":
                return Provider.LOCAL;
            case "API":
                return Provider.API;
            case "SMTP":
                return Provider.SMTP;
            default:
                return Provider.INVALID;
        }
    }

    public static boolean hasValidProvider() {
        return getProvider() != Provider.INVALID;
    }

    public static boolean isLocalMode() {
        return getProvider() == Provider.LOCAL;
    }

    public static boolean sendLinkCode(String playerName, UUID uuid, String ip, String email, String code) {
        Provider provider = getProvider();

        switch (provider) {
            case LOCAL:
                return true;
            case API:
                return MailApiService.sendLinkCode(playerName, uuid, ip, email, code);
            case SMTP:
                return MailSmtpService.sendLinkCode(playerName, uuid, ip, email, code);
            case INVALID:
            default:
                AuthTG.logger.log(Level.WARNING,
                        "[AuthTG] Invalid mail.provider in config.yml. Allowed values: LOCAL, API, SMTP");
                return false;
        }
    }
}