package org.ezhik.authTG;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class PasswordHasher {
    public enum HashAlgorithm {
        ARGON2ID,
        SHA256
    }

    private static final int ARGON2_MEMORY_KIB = 19456;
    private static final int ARGON2_ITERATIONS = 2;
    private static final int ARGON2_PARALLELISM = 1;
    private static final int ARGON2_HASH_LENGTH_BYTES = 32;
    private static final int ARGON2_SALT_LENGTH_BYTES = 16;

    private static final Pattern LEGACY_SHA256_PATTERN = Pattern.compile("^[a-fA-F0-9]{64}$");
    private static final Pattern AUTHME_SHA256_PATTERN = Pattern.compile("^\\$SHA\\$([^$]+)\\$([a-fA-F0-9]{64})$");

    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }

        if (getTargetAlgorithm() == HashAlgorithm.SHA256) {
            return hashLegacySha256(password);
        }

        return hashArgon2id(password);
    }

    public static HashAlgorithm parseHashAlgorithm(String algorithm) {
        if (algorithm == null) {
            return HashAlgorithm.ARGON2ID;
        }

        String normalized = algorithm.trim()
                .toUpperCase()
                .replace("-", "")
                .replace("_", "");

        return switch (normalized) {
            case "SHA256" -> HashAlgorithm.SHA256;
            case "ARGON", "ARGON2", "ARGON2ID" -> HashAlgorithm.ARGON2ID;
            default -> {
                AuthTG.logger.log(Level.WARNING,
                        "[AuthTG] Unknown passwordHashAlgorithm '" + algorithm + "'. Using ARGON2ID.");
                yield HashAlgorithm.ARGON2ID;
            }
        };
    }

    private static String hashArgon2id(String password) {
        Argon2 argon2 = null;
        char[] passwordChars = password.toCharArray();

        try {
            argon2 = createArgon2();
            return argon2.hash(ARGON2_ITERATIONS, ARGON2_MEMORY_KIB, ARGON2_PARALLELISM, passwordChars);
        } catch (RuntimeException | LinkageError e) {
            AuthTG.logger.log(Level.SEVERE, "Error hashing password with Argon2id", e);
            throw new IllegalStateException("Cannot hash password with Argon2id", e);
        } finally {
            wipePassword(argon2, passwordChars);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        if (isArgon2idHash(storedHash)) {
            return verifyArgon2id(password, storedHash);
        }

        if (isLegacySha256Hash(storedHash)) {
            return verifyLegacySha256(password, storedHash);
        }

        if (isAuthMeSha256Hash(storedHash)) {
            return verifyAuthMeSha256(password, storedHash);
        }

        return false;
    }

    public static boolean needsRehash(String storedHash) {
        if (storedHash == null) {
            return false;
        }

        HashAlgorithm targetAlgorithm = getTargetAlgorithm();
        if (targetAlgorithm == HashAlgorithm.SHA256) {
            return isArgon2idHash(storedHash);
        }

        if (isLegacySha256Hash(storedHash) || isAuthMeSha256Hash(storedHash)) {
            return true;
        }

        if (!isArgon2idHash(storedHash)) {
            return false;
        }

        try {
            return createArgon2().needsRehash(storedHash, ARGON2_ITERATIONS, ARGON2_MEMORY_KIB, ARGON2_PARALLELISM);
        } catch (RuntimeException | LinkageError e) {
            AuthTG.logger.log(Level.SEVERE, "Error checking Argon2id hash parameters", e);
            return false;
        }
    }

    public static boolean isLegacySha256Hash(String storedHash) {
        return storedHash != null && LEGACY_SHA256_PATTERN.matcher(storedHash).matches();
    }

    public static boolean isAuthMeSha256Hash(String storedHash) {
        return storedHash != null && AUTHME_SHA256_PATTERN.matcher(storedHash).matches();
    }

    private static HashAlgorithm getTargetAlgorithm() {
        return AuthTG.passwordHashAlgorithm == null ? HashAlgorithm.ARGON2ID : AuthTG.passwordHashAlgorithm;
    }

    private static boolean isArgon2idHash(String storedHash) {
        return storedHash.startsWith("$argon2id$");
    }

    private static Argon2 createArgon2() {
        return Argon2Factory.create(
                Argon2Factory.Argon2Types.ARGON2id,
                ARGON2_SALT_LENGTH_BYTES,
                ARGON2_HASH_LENGTH_BYTES
        );
    }

    private static boolean verifyArgon2id(String password, String storedHash) {
        Argon2 argon2 = null;
        char[] passwordChars = password.toCharArray();

        try {
            argon2 = createArgon2();
            return argon2.verify(storedHash, passwordChars);
        } catch (RuntimeException | LinkageError e) {
            AuthTG.logger.log(Level.SEVERE, "Error verifying Argon2id password hash", e);
            return false;
        } finally {
            wipePassword(argon2, passwordChars);
        }
    }

    private static boolean verifyLegacySha256(String password, String storedHash) {
        try {
            byte[] expected = hexToBytes(storedHash);
            byte[] actual = sha256(password);
            return MessageDigest.isEqual(actual, expected);
        } catch (RuntimeException e) {
            AuthTG.logger.log(Level.SEVERE, "Error verifying legacy password hash", e);
            return false;
        }
    }

    private static boolean verifyAuthMeSha256(String password, String storedHash) {
        var matcher = AUTHME_SHA256_PATTERN.matcher(storedHash);
        if (!matcher.matches()) return false;
        String innerHash = bytesToHex(sha256(password));
        byte[] actual = sha256(innerHash + matcher.group(1));
        byte[] expected = hexToBytes(matcher.group(2));
        return MessageDigest.isEqual(actual, expected);
    }

    private static String hashLegacySha256(String password) {
        return bytesToHex(sha256(password));
    }

    private static byte[] sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            AuthTG.logger.log(Level.SEVERE, "SHA-256 is not available", e);
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int high = Character.digit(hex.charAt(i * 2), 16);
            int low = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (high < 0 || low < 0) {
                throw new IllegalArgumentException("Invalid hex password hash");
            }
            bytes[i] = (byte) ((high << 4) + low);
        }
        return bytes;
    }

    private static void wipePassword(Argon2 argon2, char[] passwordChars) {
        if (argon2 != null) {
            argon2.wipeArray(passwordChars);
        } else {
            Arrays.fill(passwordChars, '\0');
        }
    }
}
