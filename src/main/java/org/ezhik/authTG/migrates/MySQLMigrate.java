package org.ezhik.authTG.migrates;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authTG.AuthTG;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLMigrate {

    public MySQLMigrate(String database, String host, String user, String password) {
        String jdbcUrl = "jdbc:mysql://" + host + "/" + database;

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {
            File[] files = new File("plugins/AuthTG/users/").listFiles();
            if (files == null) {
                return;
            }

            String insertUserSql =
                    "INSERT INTO AuthTGUsers " +
                            "(uuid, password, playername, active, activeTG, chatid, twofactor, username, firstname, lastname, currentUUID, admin, ipRegistration, email, isVerifiedEmail) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String insertBanSql =
                    "INSERT INTO AuthTGBans (uuid, timeBan, reason, time, admin) VALUES (?, ?, ?, ?, ?)";

            String insertMuteSql =
                    "INSERT INTO AuthTGMutes (uuid, timeMute, reason, time, admin) VALUES (?, ?, ?, ?, ?)";

            String insertFriendSql =
                    "INSERT INTO AuthTGFriends (uuid, friend) VALUES (?, ?)";

            String insertCommandSql =
                    "INSERT INTO AuthTGCommands (uuid, command) VALUES (?, ?)";

            try (PreparedStatement insertUser = conn.prepareStatement(insertUserSql);
                 PreparedStatement insertBan = conn.prepareStatement(insertBanSql);
                 PreparedStatement insertMute = conn.prepareStatement(insertMuteSql);
                 PreparedStatement insertFriend = conn.prepareStatement(insertFriendSql);
                 PreparedStatement insertCommand = conn.prepareStatement(insertCommandSql)) {

                for (File file : files) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));

                    insertUser.setString(1, uuid.toString());
                    setNullableString(insertUser, 2, config.getString("password"));
                    setNullableString(insertUser, 3, config.getString("playername"));
                    insertUser.setBoolean(4, config.getBoolean("active"));
                    insertUser.setBoolean(5, config.getBoolean("activetg"));

                    if (config.contains("chatid")) {
                        insertUser.setLong(6, config.getLong("chatid"));
                    } else {
                        insertUser.setNull(6, Types.BIGINT);
                    }

                    insertUser.setBoolean(7, config.getBoolean("twofactor"));
                    setNullableString(insertUser, 8, config.getString("username"));
                    setNullableString(insertUser, 9, config.getString("firstname"));
                    setNullableString(insertUser, 10, config.getString("lastname"));
                    insertUser.setBoolean(11, true);
                    insertUser.setBoolean(12, config.getBoolean("admin"));
                    setNullableString(insertUser, 13, config.getString("ipRegistration"));
                    setNullableString(insertUser, 14, config.getString("email"));
                    insertUser.setBoolean(15, config.getBoolean("isVerifiedEmail"));
                    insertUser.executeUpdate();

                    if (config.contains("ban")) {
                        insertBan.setString(1, uuid.toString());
                        insertBan.setString(2, config.getString("ban.timeBan"));
                        insertBan.setString(3, config.getString("ban.reason"));
                        insertBan.setString(4, config.getString("ban.time"));
                        insertBan.setString(5, config.getString("ban.admin"));
                        insertBan.executeUpdate();
                    }

                    if (config.contains("mute")) {
                        insertMute.setString(1, uuid.toString());
                        insertMute.setString(2, config.getString("mute.timeMute"));
                        insertMute.setString(3, config.getString("mute.reason"));
                        insertMute.setString(4, config.getString("mute.time"));
                        insertMute.setString(5, config.getString("mute.admin"));
                        insertMute.executeUpdate();
                    }

                    List<String> friends = config.getStringList("friends");
                    for (String friend : friends) {
                        insertFriend.setString(1, uuid.toString());
                        insertFriend.setString(2, friend);
                        insertFriend.executeUpdate();
                    }

                    if (config.contains("commands")) {
                        for (String command : config.getStringList("commands")) {
                            insertCommand.setString(1, uuid.toString());
                            insertCommand.setString(2, command);
                            insertCommand.executeUpdate();
                        }
                    }

                    file.delete();
                }
            }

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQL Exception: " + e.getMessage(), e);
        }
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }
    }
}