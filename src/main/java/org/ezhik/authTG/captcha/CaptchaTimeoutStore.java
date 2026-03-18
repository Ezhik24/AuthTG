package org.ezhik.authTG.captcha;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authTG.AuthTG;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;

public final class CaptchaTimeoutStore {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    private CaptchaTimeoutStore() {
    }

    public static boolean shouldShowCaptcha(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        LocalDateTime timeout = getTimeout(uuid);
        return timeout == null || !LocalDateTime.now().isBefore(timeout);
    }

    public static void setTimeout(UUID uuid, LocalDateTime time) {
        if (uuid == null) {
            return;
        }

        DataSource ds = AuthTG.getDataSource();
        if (ds != null) {
            setMySqlTimeout(ds, uuid, time);
            return;
        }

        setYamlTimeout(uuid, time);
    }

    public static LocalDateTime getTimeout(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        DataSource ds = AuthTG.getDataSource();
        if (ds != null) {
            return getMySqlTimeout(ds, uuid);
        }

        return getYamlTimeout(uuid);
    }

    private static void setMySqlTimeout(DataSource ds, UUID uuid, LocalDateTime time) {
        String sql = "UPDATE AuthTGUsers SET captchaTimeout=? WHERE uuid=?";

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, time == null ? null : time.format(FORMATTER));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot save captchaTimeout: " + e.getMessage(), e);
        }
    }

    private static LocalDateTime getMySqlTimeout(DataSource ds, UUID uuid) {
        String sql = "SELECT captchaTimeout FROM AuthTGUsers WHERE uuid=?";

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String raw = rs.getString("captchaTimeout");
                    if (raw == null || raw.isBlank()) {
                        return null;
                    }
                    return LocalDateTime.parse(raw, FORMATTER);
                }
            }

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot load captchaTimeout: " + e.getMessage(), e);
        }

        return null;
    }

    private static void setYamlTimeout(UUID uuid, LocalDateTime time) {
        File file = userFile(uuid);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("captchaTimeout", time == null ? null : time.format(FORMATTER));

        try {
            config.save(file);
        } catch (IOException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot save captchaTimeout in YAML: " + e.getMessage(), e);
        }
    }

    private static LocalDateTime getYamlTimeout(UUID uuid) {
        File file = userFile(uuid);
        if (!file.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String raw = config.getString("captchaTimeout");
        if (raw == null || raw.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(raw, FORMATTER);
    }

    private static File userFile(UUID uuid) {
        File usersDir = new File(AuthTG.getInstance().getDataFolder(), "users");
        if (!usersDir.exists()) {
            usersDir.mkdirs();
        }
        return new File(usersDir, uuid + ".yml");
    }
}
