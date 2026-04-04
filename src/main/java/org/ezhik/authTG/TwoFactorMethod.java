package org.ezhik.authTG;

public enum TwoFactorMethod {
    OFF,
    TG,
    MAIL,
    VK;

    public static TwoFactorMethod fromStoredValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return TwoFactorMethod.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
