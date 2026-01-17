package org.ezhik.authTG.usersconfiguration;

import org.ezhik.authTG.AuthTG;

import javax.sql.DataSource;
import java.sql.*;
import java.util.logging.Level;

public final class MySQLSchemaMigrator {

    private static final int LATEST_VERSION = 3;

    private MySQLSchemaMigrator() {}

    public static void migrate(DataSource ds, String databaseName) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(true);

            ensureMetaTable(c);
            int current = getSchemaVersion(c);

            while (current < LATEST_VERSION) {
                int next = current + 1;
                AuthTG.logger.log(Level.INFO, "[AuthTG] DB migrate: " + current + " -> " + next);

                switch (next) {
                    case 1 -> migrateToV1(c);
                    case 2 -> migrateToV2(c, databaseName);
                    case 3 -> migrateToV3(c);
                    default -> throw new IllegalStateException("Unknown schema version: " + next);
                }

                setSchemaVersion(c, next);
                current = next;
            }

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] DB migrate failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void ensureMetaTable(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGMeta (" +
                            "k VARCHAR(32) NOT NULL," +
                            "v VARCHAR(64) NOT NULL," +
                            "PRIMARY KEY (k)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
        }
    }

    private static int getSchemaVersion(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT v FROM AuthTGMeta WHERE k='schema_version'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                try {
                    return Integer.parseInt(rs.getString("v"));
                } catch (NumberFormatException ignored) {}
            }
        }
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO AuthTGMeta(k, v) VALUES('schema_version', '0')")) {
            ps.executeUpdate();
        }
        return 0;
    }

    private static void setSchemaVersion(Connection c, int v) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE AuthTGMeta SET v=? WHERE k='schema_version'")) {
            ps.setString(1, String.valueOf(v));
            ps.executeUpdate();
        }
    }
    private static void migrateToV1(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGUsers (" +
                            "priKey INT NOT NULL AUTO_INCREMENT," +
                            "uuid varchar(36) NOT NULL," +
                            "playername varchar(120) NOT NULL," +
                            "password varchar(64)," +
                            "active BOOLEAN NOT NULL DEFAULT false," +
                            "twofactor BOOLEAN NOT NULL DEFAULT false," +
                            "activeTG BOOLEAN NOT NULL DEFAULT false," +
                            "chatid BIGINT," +
                            "username varchar(32)," +
                            "firstname varchar(120)," +
                            "lastname varchar(120)," +
                            "currentUUID BOOLEAN NOT NULL DEFAULT false," +
                            "admin BOOLEAN NOT NULL DEFAULT false," +
                            "ipSession varchar(72)," +
                            "timeSession varchar(120)," +
                            "ipRegistration varchar(72)," +
                            "PRIMARY KEY (priKey)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGFriends (" +
                            "priKey INT NOT NULL AUTO_INCREMENT," +
                            "uuid varchar(36) NOT NULL," +
                            "friend varchar(120) NOT NULL," +
                            "PRIMARY KEY (priKey)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGCommands (" +
                            "priKey INT NOT NULL AUTO_INCREMENT," +
                            "uuid varchar(36) NOT NULL," +
                            "command varchar(30) NOT NULL," +
                            "PRIMARY KEY (priKey)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGBans (" +
                            "priKey INT NOT NULL AUTO_INCREMENT," +
                            "uuid varchar(36) NOT NULL," +
                            "timeBan varchar(64) NOT NULL," +
                            "reason varchar(120) NOT NULL," +
                            "time varchar(36) NOT NULL," +
                            "admin varchar(90) NOT NULL," +
                            "PRIMARY KEY (priKey)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGMutes (" +
                            "priKey INT NOT NULL AUTO_INCREMENT," +
                            "uuid varchar(36) NOT NULL," +
                            "timeMute varchar(64) NOT NULL," +
                            "reason varchar(120) NOT NULL," +
                            "time varchar(36) NOT NULL," +
                            "admin varchar(90) NOT NULL," +
                            "PRIMARY KEY (priKey)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
        }
    }

    private static void migrateToV2(Connection c, String db) throws SQLException {
        if (!indexExists(c, db, "AuthTGUsers", "uq_authtgusers_uuid")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("ALTER TABLE AuthTGUsers ADD UNIQUE KEY uq_authtgusers_uuid (uuid)");
            } catch (SQLException e) {
                AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot create UNIQUE index on AuthTGUsers.uuid: " + e.getMessage());
            }
        }
        ensureFk(c, db, "AuthTGFriends", "fk_authtgfriends_users_uuid",
                "uuid", "AuthTGUsers", "uuid");

        ensureFk(c, db, "AuthTGCommands", "fk_authtgcommands_users_uuid",
                "uuid", "AuthTGUsers", "uuid");

        ensureFk(c, db, "AuthTGBans", "fk_authtgbans_users_uuid",
                "uuid", "AuthTGUsers", "uuid");

        ensureFk(c, db, "AuthTGMutes", "fk_authtgmutes_users_uuid",
                "uuid", "AuthTGUsers", "uuid");
    }

    private static void migrateToV3(Connection c) throws SQLException {

        try(Statement st = c.createStatement()) {
            st.executeUpdate("ALTER TABLE AuthTGUsers RENAME COLUMN ip TO ipSession");
            st.executeUpdate("ALTER TABLE AuthTGUsers RENAME COLUMN time TO timeSession");
            st.executeUpdate("ALTER TABLE AuthTGUsers ADD COLUMN ipRegistration VARCHAR(72) NULL");

            st.executeUpdate("CREATE INDEX idx_users_ip_registration ON AuthTGUsers(ipRegistration)");
        }
    }

    private static boolean indexExists(Connection c, String db, String table, String indexName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.statistics " +
                "WHERE table_schema=? AND table_name=? AND index_name=? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            ps.setString(3, indexName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean fkExists(Connection c, String db, String table, String fkName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.table_constraints " +
                "WHERE constraint_schema=? AND table_name=? AND constraint_name=? AND constraint_type='FOREIGN KEY' LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            ps.setString(3, fkName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void ensureFk(Connection c, String db,
                                 String childTable, String fkName,
                                 String childColumn, String parentTable, String parentColumn) throws SQLException {

        if (fkExists(c, db, childTable, fkName)) return;

        try (Statement st = c.createStatement()) {
            st.executeUpdate(
                    "ALTER TABLE " + childTable +
                            " ADD CONSTRAINT " + fkName +
                            " FOREIGN KEY (" + childColumn + ") REFERENCES " + parentTable + "(" + parentColumn + ")" +
                            " ON DELETE CASCADE ON UPDATE CASCADE"
            );
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "[AuthTG] Cannot add FK " + fkName + " on " + childTable + ": " + e.getMessage());
        }
    }
}
