package org.ezhik.authTG.migrates;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.PasswordHasher;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Imports AuthMe accounts directly from its configured SQL storage.
 * Passwords remain hashed and are upgraded to AuthTG's target hash after login.
 */
public final class AuthMeMigrate {

    public record Result(int imported, int skipped, int failed) {
    }

    private AuthMeMigrate() {
    }

    public static Result migrate() {
        File configFile = new File("plugins/AuthMe/config.yml");
        if (!configFile.isFile()) {
            throw new IllegalStateException("plugins/AuthMe/config.yml not found");
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String backend = value(config, "DataSource.backend", "dataSource.backend", "backend");
        if (backend == null) backend = "SQLITE";

        try (Connection connection = openConnection(config, backend);
             Statement statement = connection.createStatement()) {
            String table = identifier(valueOr(config, "authme",
                    "DataSource.mySQLTablename", "dataSource.mySQLTablename", "mySQLTablename"));

            try (ResultSet rows = statement.executeQuery("SELECT * FROM " + table)) {
                Map<String, String> columns = columns(rows.getMetaData());
                String nameColumn = selectColumn(columns,
                        value(config, "DataSource.mySQLColumnName", "DataSource.columnName",
                                "dataSource.mySQLColumnName", "dataSource.columnName"), "username", "name");
                String passwordColumn = selectColumn(columns,
                        value(config, "DataSource.mySQLColumnPassword", "DataSource.columnPassword",
                                "dataSource.mySQLColumnPassword", "dataSource.columnPassword"), "password");
                String uuidColumn = selectOptionalColumn(columns,
                        value(config, "DataSource.mySQLColumnUuid", "DataSource.columnUuid",
                                "dataSource.mySQLColumnUuid", "dataSource.columnUuid"), "uuid");
                String realNameColumn = selectOptionalColumn(columns,
                        value(config, "DataSource.mySQLRealName", "DataSource.columnRealName",
                                "dataSource.mySQLRealName", "dataSource.columnRealName"), "realname");
                String emailColumn = selectOptionalColumn(columns,
                        value(config, "DataSource.mySQLColumnEmail", "DataSource.columnEmail",
                                "dataSource.mySQLColumnEmail", "dataSource.columnEmail"), "email");

                int imported = 0;
                int skipped = 0;
                int failed = 0;

                while (rows.next()) {
                    String name = trimToNull(rows.getString(realNameColumn != null ? realNameColumn : nameColumn));
                    String hash = trimToNull(rows.getString(passwordColumn));
                    if (name == null || hash == null || !isSupportedHash(hash)) {
                        skipped++;
                        continue;
                    }

                    UUID uuid = parseUuid(uuidColumn == null ? null : rows.getString(uuidColumn));
                    if (uuid == null && !Bukkit.getOnlineMode()) {
                        uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
                    }
                    if (uuid == null) {
                        skipped++;
                        continue;
                    }

                    String email = emailColumn == null ? null : trimToNull(rows.getString(emailColumn));
                    try {
                        AuthTG.loader.importUser(uuid, name, hash, email);
                        imported++;
                    } catch (RuntimeException e) {
                        failed++;
                        AuthTG.logger.warning("[AuthTG] Cannot import AuthMe user " + name + ": " + e.getMessage());
                    }
                }
                return new Result(imported, skipped, failed);
            }
        } catch (Exception e) {
            throw new IllegalStateException("AuthMe migration failed: " + e.getMessage(), e);
        }
    }

    private static Connection openConnection(YamlConfiguration config, String backend) throws Exception {
        if (backend.toUpperCase(Locale.ROOT).contains("SQLITE")) {
            String fileName = valueOr(config, "authme.db",
                    "DataSource.sqliteFile", "dataSource.sqliteFile");
            if (!fileName.toLowerCase(Locale.ROOT).endsWith(".db")) fileName += ".db";
            File database = new File("plugins/AuthMe", fileName);
            String url = "jdbc:sqlite:" + database.getAbsolutePath();
            Plugin authMe = Bukkit.getPluginManager().getPlugin("AuthMe");
            ClassLoader loader = authMe == null
                    ? AuthMeMigrate.class.getClassLoader()
                    : authMe.getClass().getClassLoader();
            Driver driver = (Driver) Class.forName("org.sqlite.JDBC", true, loader)
                    .getDeclaredConstructor().newInstance();
            Connection connection = driver.connect(url, new Properties());
            if (connection == null) throw new IllegalStateException("SQLite driver rejected " + url);
            return connection;
        }

        String host = valueOr(config, "localhost", "DataSource.mySQLHost", "dataSource.mySQLHost");
        String port = valueOr(config, "3306", "DataSource.mySQLPort", "dataSource.mySQLPort");
        String database = valueOr(config, "authme", "DataSource.mySQLDatabase", "dataSource.mySQLDatabase");
        String user = valueOr(config, "root", "DataSource.mySQLUsername", "dataSource.mySQLUsername");
        String password = valueOr(config, "", "DataSource.mySQLPassword", "dataSource.mySQLPassword");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=utf8&useSSL=false";
        return java.sql.DriverManager.getConnection(url, user, password);
    }

    private static boolean isSupportedHash(String hash) {
        return PasswordHasher.isAuthMeSha256Hash(hash)
                || PasswordHasher.isLegacySha256Hash(hash)
                || hash.startsWith("$argon2id$");
    }

    private static UUID parseUuid(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) return null;
        try {
            if (normalized.length() == 32) {
                normalized = normalized.substring(0, 8) + "-" + normalized.substring(8, 12) + "-"
                        + normalized.substring(12, 16) + "-" + normalized.substring(16, 20) + "-"
                        + normalized.substring(20);
            }
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static Map<String, String> columns(ResultSetMetaData metadata) throws Exception {
        Map<String, String> result = new HashMap<>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            result.put(label.toLowerCase(Locale.ROOT), label);
        }
        return result;
    }

    private static String selectColumn(Map<String, String> columns, String configured, String... defaults) {
        String selected = selectOptionalColumn(columns, configured, defaults);
        if (selected == null) throw new IllegalStateException("Required AuthMe column not found");
        return selected;
    }

    private static String selectOptionalColumn(Map<String, String> columns, String configured, String... defaults) {
        if (configured != null) {
            String match = columns.get(configured.toLowerCase(Locale.ROOT));
            if (match != null) return match;
        }
        for (String candidate : defaults) {
            String match = columns.get(candidate.toLowerCase(Locale.ROOT));
            if (match != null) return match;
        }
        return null;
    }

    private static String identifier(String value) {
        if (value == null || !value.matches("[A-Za-z0-9_]+")) {
            throw new IllegalStateException("Unsafe AuthMe table name: " + value);
        }
        return value;
    }

    private static String valueOr(YamlConfiguration config, String fallback, String... paths) {
        String value = value(config, paths);
        return value == null ? fallback : value;
    }

    private static String value(YamlConfiguration config, String... paths) {
        for (String path : paths) {
            String value = trimToNull(config.getString(path));
            if (value != null) return value;
        }
        return null;
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }
}
