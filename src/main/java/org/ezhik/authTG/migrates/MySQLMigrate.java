package org.ezhik.authTG.migrates;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class MySQLMigrate {
    Connection conn;
    Statement st = null;
    public MySQLMigrate(String database, String host, String user, String password) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database,
                    user,
                    password
            );
            st = conn.createStatement();
            File[] file = new File("plugins/AuthTG/users/").listFiles();
            if (file != null) {
                for (File f : file) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
                    UUID uuid = UUID.fromString(f.getName().replace(".yml", ""));
                    String passworduser = config.getString("password");
                    String playername = config.getString("playername");
                    boolean active = config.getBoolean("active");
                    boolean activetg = config.getBoolean("activetg");
                    long chatid = config.getLong("chatid");
                    boolean twofactor = config.getBoolean("twofactor");
                    String username = config.getString("username");
                    String firstname = config.getString("firstname");
                    String lastname = config.getString("lastname");
                    boolean isAdmin = config.getBoolean("admin");
                    st.executeUpdate("INSERT INTO AuthTGUsers (uuid, password, playername, active, activetg, chatid, twofactor, username, firstname, lastname, currentUUID, admin) VALUES ('" + uuid + "', '" + passworduser + "', '" + playername + "', " + active + ", " + activetg + ", " + chatid + ", " + twofactor + ", '" + username + "', '" + firstname + "', '" + lastname + "', true ," + isAdmin + ")");
                    if (config.contains("ban")) {
                        st.executeUpdate("INSERT INTO AuthTGBans (uuid, timeBan, reason, time, admin) VALUES ('" + uuid.toString()  + "', '" + config.getString("ban.timeBan") + "', '" + config.getString("ban.reason") + "', '" + config.getString("ban.time") + "', '" + config.getString("ban.admin") + "')");
                    }
                    if (config.contains("mute")) {
                        st.executeUpdate("INSERT INTO AuthTGMutes (uuid, timeMute, reason, time, admin) VALUES ('" + uuid.toString() + "', '" + config.getString("mute.timeMute") + "', '" + config.getString("mute.reason") + "', '" + config.getString("mute.time") + "', '" + config.getString("mute.admin") + "')");
                    }
                    List<String> friends = config.getStringList("friends");
                    for (String friend : friends) {
                        st.executeUpdate("INSERT INTO AuthTGUsers (uuid, friends) VALUES ('" + uuid.toString() + "', '" + friend + "')");
                    }
                    if (config.contains("commands")) {
                        for (String command : config.getStringList("commands")) {
                            st.executeUpdate("INSERT INTO AuthTGCommands (uuid, command) VALUES ('" + uuid.toString() + "', '" + command + "')");
                        }
                    }
                    f.delete();
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}
