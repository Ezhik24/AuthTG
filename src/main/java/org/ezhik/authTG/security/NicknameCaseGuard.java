package org.ezhik.authTG.security;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authTG.AuthTG;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public final class NicknameCaseGuard {

    public CheckResult check(String requestedName, UUID loginUuid) {
        if (requestedName == null || requestedName.isBlank()) {
            return CheckResult.allowed();
        }

        try {
            DataSource dataSource = AuthTG.getDataSource();
            if (dataSource != null) {
                return checkMysql(dataSource, requestedName, loginUuid);
            }

            return checkYaml(requestedName, loginUuid);
        } catch (Exception exception) {
            AuthTG.logger.log(Level.SEVERE,
                    "[AuthTG] Cannot check nickname case for " + requestedName + ": " + exception.getMessage(),
                    exception);
            return CheckResult.storageError();
        }
    }

    private CheckResult checkMysql(DataSource dataSource, String requestedName, UUID loginUuid) throws SQLException {
        String requestedNormalized = normalize(requestedName);
        String firstDifferentCaseName = null;
        UUID firstDifferentCaseUuid = null;

        String sql = "SELECT uuid, playername FROM AuthTGUsers " +
                "WHERE active=true AND LOWER(playername)=LOWER(?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, requestedName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String registeredName = resultSet.getString("playername");
                    if (registeredName == null || registeredName.isBlank()) {
                        continue;
                    }

                    String registeredNormalized = normalize(registeredName);
                    if (!registeredNormalized.equals(requestedNormalized)) {
                        continue;
                    }

                    UUID registeredUuid = parseUuid(resultSet.getString("uuid"));

                    if (registeredName.equals(requestedName)) {
                        return CheckResult.allowed();
                    }

                    if (firstDifferentCaseName == null) {
                        firstDifferentCaseName = registeredName;
                        firstDifferentCaseUuid = registeredUuid;
                    }
                }
            }
        }

        if (firstDifferentCaseName != null) {
            return CheckResult.caseMismatch(firstDifferentCaseName, firstDifferentCaseUuid);
        }

        return CheckResult.allowed();
    }

    private CheckResult checkYaml(String requestedName, UUID loginUuid) {
        String requestedNormalized = normalize(requestedName);
        String firstDifferentCaseName = null;
        UUID firstDifferentCaseUuid = null;

        File usersDirectory = new File(AuthTG.getInstance().getDataFolder(), "users");
        File[] files = usersDirectory.listFiles((directory, name) -> name.endsWith(".yml"));

        if (files == null || files.length == 0) {
            return CheckResult.allowed();
        }

        Arrays.sort(files);

        for (File file : files) {
            YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);

            if (!userConfig.getBoolean("active", false)) {
                continue;
            }

            String registeredName = userConfig.getString("playername");
            if (registeredName == null || registeredName.isBlank()) {
                continue;
            }

            String registeredNormalized = normalize(registeredName);
            if (!registeredNormalized.equals(requestedNormalized)) {
                continue;
            }

            UUID registeredUuid = uuidFromUserFile(file);

            if (registeredName.equals(requestedName)) {
                return CheckResult.allowed();
            }

            if (firstDifferentCaseName == null) {
                firstDifferentCaseName = registeredName;
                firstDifferentCaseUuid = registeredUuid;
            }
        }

        if (firstDifferentCaseName != null) {
            return CheckResult.caseMismatch(firstDifferentCaseName, firstDifferentCaseUuid);
        }

        return CheckResult.allowed();
    }

    public static String normalize(String nickname) {
        return nickname == null ? "" : nickname.toLowerCase(Locale.ROOT);
    }

    private static UUID uuidFromUserFile(File file) {
        String fileName = file.getName();
        if (!fileName.endsWith(".yml")) {
            return null;
        }

        return parseUuid(fileName.substring(0, fileName.length() - 4));
    }

    private static UUID parseUuid(String rawUuid) {
        if (rawUuid == null || rawUuid.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(rawUuid);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public enum Status {
        ALLOWED,
        CASE_MISMATCH,
        STORAGE_ERROR
    }

    public record CheckResult(Status status, String registeredName, UUID registeredUuid) {
        public static CheckResult allowed() {
            return new CheckResult(Status.ALLOWED, null, null);
        }

        public static CheckResult caseMismatch(String registeredName, UUID registeredUuid) {
            return new CheckResult(Status.CASE_MISMATCH, registeredName, registeredUuid);
        }

        public static CheckResult storageError() {
            return new CheckResult(Status.STORAGE_ERROR, null, null);
        }
    }
}