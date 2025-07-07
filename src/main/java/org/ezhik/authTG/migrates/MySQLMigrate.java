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
                    st.executeUpdate("INSERT INTO AuthTGUsers (uuid, password, playername, active, activetg, chatid, twofactor, username, firstname, lastname) VALUES ('" + uuid + "', '" + passworduser + "', '" + playername + "', " + active + ", " + activetg + ", " + chatid + ", " + twofactor + ", '" + username + "', '" + firstname + "', '" + lastname + "')");
                    List<String> friends = config.getStringList("friends");
                    for (String friend : friends) {
                        st.executeUpdate("INSERT INTO AuthTGUsers (uuid, friends) VALUES ('" + uuid.toString() + "', '" + friend + "')");
                    }
                    f.delete();
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}
