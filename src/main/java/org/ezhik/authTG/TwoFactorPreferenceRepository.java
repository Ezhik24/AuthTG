package org.ezhik.authTG;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public final class TwoFactorPreferenceRepository {

    private TwoFactorPreferenceRepository() {
    }

    public static TwoFactorMethod get(UUID uuid) {
        DataSource ds = AuthTG.getDataSource();
        if (ds == null || uuid == null) {
            return null;
        }

        String sql = "SELECT preferred2fa FROM AuthTGUsers WHERE uuid=?";

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return TwoFactorMethod.fromStoredValue(rs.getString("preferred2fa"));
                }
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot load preferred2fa: " + e.getMessage(), e);
        }

        return null;
    }

    public static void set(UUID uuid, TwoFactorMethod method) {
        DataSource ds = AuthTG.getDataSource();
        if (ds == null || uuid == null) {
            return;
        }

        if (method == null) {
            clear(uuid);
            return;
        }

        String sql = "UPDATE AuthTGUsers SET preferred2fa=? WHERE uuid=?";

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, method.name());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot save preferred2fa: " + e.getMessage(), e);
        }
    }

    public static void clear(UUID uuid) {
        DataSource ds = AuthTG.getDataSource();
        if (ds == null || uuid == null) {
            return;
        }

        String sql = "UPDATE AuthTGUsers SET preferred2fa=NULL WHERE uuid=?";

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot clear preferred2fa: " + e.getMessage(), e);
        }
    }
}
