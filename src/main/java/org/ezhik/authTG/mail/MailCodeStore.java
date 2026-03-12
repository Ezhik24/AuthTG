package org.ezhik.authTG.mail;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MailCodeStore {

    private static final String SYMBOLS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<UUID, MailCodeSession> PENDING = new ConcurrentHashMap<>();

    private MailCodeStore() {
    }

    public static String generateCode(int length) {
        int realLength = Math.max(length, 4);
        StringBuilder builder = new StringBuilder(realLength);

        for (int i = 0; i < realLength; i++) {
            builder.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        }

        return builder.toString();
    }

    public static void create(UUID uuid, String email, String code, int expireSeconds) {
        long expiresAt = System.currentTimeMillis() + (Math.max(expireSeconds, 30) * 1000L);
        PENDING.put(uuid, new MailCodeSession(email, code, expiresAt));
    }

    public static MailCodeSession get(UUID uuid) {
        MailCodeSession session = PENDING.get(uuid);
        if (session == null) {
            return null;
        }

        if (session.isExpired()) {
            PENDING.remove(uuid);
            return null;
        }

        return session;
    }

    public static boolean verify(UUID uuid, String inputCode) {
        MailCodeSession session = get(uuid);
        if (session == null) {
            return false;
        }

        if (!session.getCode().equalsIgnoreCase(inputCode)) {
            return false;
        }

        PENDING.remove(uuid);
        return true;
    }

    public static void remove(UUID uuid) {
        PENDING.remove(uuid);
    }
}