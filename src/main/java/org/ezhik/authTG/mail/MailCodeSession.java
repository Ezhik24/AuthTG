package org.ezhik.authTG.mail;

public class MailCodeSession {

    private final String email;
    private final String code;
    private final long expiresAt;

    public MailCodeSession(String email, String code, long expiresAt) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}