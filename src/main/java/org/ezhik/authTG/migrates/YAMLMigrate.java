package org.ezhik.authTG.migrates;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YAMLMigrate {
    Connection conn;
    Statement st = null;
    ResultSet rs = null;
    public YAMLMigrate(String database, String host, String user, String password) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database,
                    user,
                    password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM AuthTGUsers");
            while (rs.next()) {
                File file = new File("plugins/AuthTG/users/" + UUID.fromString(rs.getString("uuid")) + ".yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (!rs.getString("password").equals("null")) config.set("password", rs.getString("password"));
                if (!rs.getString("playername").equals("null")) config.set("playername", rs.getString("playername"));
                config.set("active", rs.getBoolean("active"));
                config.set("chatid", rs.getLong("chatid"));
                if (!rs.getString("firstname").equals("null")) config.set("firstname", rs.getString("firstname"));
                if (!rs.getString("lastname").equals("null")) config.set("lastname", rs.getString("lastname"));
                if (!rs.getString("username").equals("null")) config.set("username", rs.getString("username"));
                config.set("twofactor", rs.getBoolean("twofactor"));
                config.set("activetg", rs.getBoolean("activetg"));
                config.set("admin", rs.getBoolean("admin"));
                try {
                    config.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
            st.executeUpdate("DROP TABLE AuthTGUsers");
            rs = st.executeQuery("SELECT * FROM AuthTGFriends");
            while (rs.next()) {
                File file = new File("plugins/AuthTG/friends/" + UUID.fromString(rs.getString("uuid")) + ".yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                List<String> friends = new ArrayList<>();
                friends.add(rs.getString("friend"));
                config.set("friends", friends);
                try {
                    config.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
            st.executeUpdate("DROP TABLE AuthTGFriends");
            rs = st.executeQuery("SELECT * FROM AuthTGBans");
            while (rs.next()) {
                File file = new File("plugins/AuthTG/bans/" + UUID.fromString(rs.getString("uuid")) + ".yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("ban.timeBan", rs.getString("timeBan"));
                config.set("ban.reason", rs.getString("reason"));
                config.set("ban.time", rs.getString("time"));
                config.set("ban.admin", rs.getString("admin"));
                try {
                    config.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
            st.executeUpdate("DROP TABLE AuthTGBans");
            rs = st.executeQuery("SELECT * FROM AuthTGMutes");
            while (rs.next()) {
                File file = new File("plugins/AuthTG/mutes/" + UUID.fromString(rs.getString("uuid")) + ".yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("mute.timeMute", rs.getString("timeMute"));
                config.set("mute.reason", rs.getString("reason"));
                config.set("mute.time", rs.getString("time"));
                config.set("mute.admin", rs.getString("admin"));
                try {
                    config.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
            st.executeUpdate("DROP TABLE AuthTGMutes");
            rs = st.executeQuery("SELECT * FROM AuthTGCommands");
            while (rs.next()) {
                File file = new File("plugins/AuthTG/commands/" + UUID.fromString(rs.getString("uuid")) + ".yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (!config.contains("commands")) {
                    List<String> commands = new ArrayList<>();
                    commands.add(rs.getString("command"));
                    config.set("commands", commands);
                } else {
                    List<String> commands = config.getStringList("commands");
                    commands.add(rs.getString("command"));
                    config.set("commands", commands);
                }
                try {
                    config.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
            st.executeUpdate("DROP TABLE AuthTGCommands");
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}
