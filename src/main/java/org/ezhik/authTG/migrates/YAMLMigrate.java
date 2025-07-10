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
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}
