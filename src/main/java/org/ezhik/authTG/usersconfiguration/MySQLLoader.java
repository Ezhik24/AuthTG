package org.ezhik.authTG.usersconfiguration;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.PasswordHasher;
import org.ezhik.authTG.events.MuterEvent;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;

public class MySQLLoader implements Loader {
    private String database;
    private String username;
    private String password;
    private String host;
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    public MySQLLoader(String database, String username, String password, String host) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.host = host;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database,
                    username,
                    password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGUsers ("
                    + "priKey INT NOT NULL AUTO_INCREMENT,"
                    + "uuid varchar(36) NOT NULL,"
                    + "playername varchar(120) NOT NULL,"
                    + "password varchar(64),"
                    + "active BOOLEAN NOT NULL DEFAULT false,"
                    + "twofactor BOOLEAN NOT NULL DEFAULT false,"
                    + "activeTG BOOLEAN NOT NULL DEFAULT false,"
                    + "chatid BIGINT,"
                    + "username varchar(32),"
                    + "firstname varchar(120),"
                    + "lastname varchar(120),"
                    + "currentUUID BOOLEAN NOT NULL DEFAULT false,"
                    + "admin BOOLEAN NOT NULL DEFAULT false,"
                    + "PRIMARY KEY (priKey))"
            );
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGFriends ("
                    + "priKey INT NOT NULL AUTO_INCREMENT,"
                    + "uuid varchar(36) NOT NULL,"
                    + "friend varchar(120) NOT NULL,"
                    + "PRIMARY KEY (priKey))"
            );
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGCommands ("
                    + "priKey INT NOT NULL AUTO_INCREMENT,"
                    + "uuid varchar(36) NOT NULL,"
                    + "command varchar(30) NOT NULL,"
                    + "PRIMARY KEY (priKey))"
            );
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGBans ("
                    + "priKey INT NOT NULL AUTO_INCREMENT,"
                    + "uuid varchar(36) NOT NULL,"
                    + "timeBan varchar(64) NOT NULL,"
                    + "reason varchar(120) NOT NULL,"
                    + "time varchar(36) NOT NULL,"
                    + "admin varchar(90) NOT NULL,"
                    + "PRIMARY KEY (priKey))"
            );
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGMutes ("
                    + "priKey INT NOT NULL AUTO_INCREMENT,"
                    + "uuid varchar(36) NOT NULL,"
                    + "timeMute varchar(64) NOT NULL,"
                    + "reason varchar(120) NOT NULL,"
                    + "time varchar(36) NOT NULL,"
                    + "admin varchar(90) NOT NULL,"
                    + "PRIMARY KEY (priKey))"
            );
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS AuthTGSessions ("
                    + "priKey INT NOT NULL AUTO_INCREMENT,"
                    + "uuid varchar(36) NOT NULL,"
                    + "ip varchar(120) NOT NULL,"
                    + "time varchar(50) NOT NULL,"
                    + "PRIMARY KEY (priKey))"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }
    @Override
    public void setPlayerName(UUID uuid, String playername) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("INSERT INTO AuthTGUsers(uuid, playername, currentUUID) VALUES ('" + uuid.toString() + "', '" + playername + "', false) ");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setPasswordHash(UUID uuid, String password) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            String hash = PasswordHasher.hashPassword(password);
            st.executeUpdate("UPDATE AuthTGUsers SET password = '" + hash + "' WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setActive(UUID uuid, boolean active) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET active = " + active + " WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isActive(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT active FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getBoolean("active");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean passwordValid(UUID uuid, String password) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT password FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return PasswordHasher.hashPassword(password).equals(rs.getString("password"));
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT playername FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getString("playername");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return "";
    }

    @Override
    public boolean getTwofactor(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT twofactor FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getBoolean("twofactor");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean getActiveTG(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT activeTG FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getBoolean("activeTG");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<String> getListFriends(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT friend FROM AuthTGFriends WHERE uuid = '" + uuid.toString() + "'");
            List<String> list = new ArrayList<>();
            while (rs.next()) {
                if (!list.contains(rs.getString("friend"))) {
                    list.add(rs.getString("friend"));
                }
            }
            conn.close();
            return list;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getUserName(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT username FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getString("username");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getFirstName(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT firstname FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getString("firstname");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getLastName(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT lastname FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getString("lastname");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Long getChatID(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT chatid FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getLong("chatid");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return 0L;
    }

    @Override
    public UUID getCurrentUUID(Long chatid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT currentUUID FROM AuthTGUsers WHERE chatid = " + chatid);
            if (rs.next()) {
                if (rs.getBoolean("currentUUID")) {
                    ResultSet rs1 = st.executeQuery("SELECT uuid FROM AuthTGUsers WHERE chatid = " + chatid);
                    if (rs1.next()) {
                        return UUID.fromString(rs1.getString("uuid"));
                    }
                }
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setCurrentUUID(UUID uuid, Long chatid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT currentUUID FROM AuthTGUsers WHERE chatid = " + chatid);
            st.executeUpdate("UPDATE AuthTGUsers SET currentUUID = false WHERE chatid = " + chatid);
            st.executeUpdate("UPDATE AuthTGUsers SET currentUUID = true WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setChatID(UUID uuid, Long chatid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET chatid = " + chatid + " WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setUsername(UUID uuid, String username) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET username = '" + username + "' WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setLastName(UUID uuid, String lastname) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET lastname = '" + lastname + "' WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setFirstName(UUID uuid, String firstname) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET firstname = '" + firstname + "' WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setTwofactor(UUID uuid, boolean state) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET twofactor = " + state + " WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setActiveTG(UUID uuid, boolean state) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("UPDATE AuthTGUsers SET activeTG = " + state + " WHERE uuid = '" + uuid.toString() + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public List<UUID> getPlayerNames(Long chatid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT uuid,activeTG FROM AuthTGUsers WHERE chatid = " + chatid);
            List<UUID> list = new ArrayList<>();
            while (rs.next()) {
                if (rs.getBoolean("activeTG")) {
                    if (!list.contains(UUID.fromString(rs.getString("uuid")))) {
                        list.add(UUID.fromString(rs.getString("uuid")));
                    }
                }
            }
            conn.close();
            return list;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setPlayerNames(Long chatid, UUID uuid) {}

    @Override
    public Set<Long> getChatID() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            Set<Long> set = new HashSet<>();
            rs = st.executeQuery("SELECT chatid FROM AuthTGUsers");
            while (rs.next()) {
                if (!set.contains(rs.getLong("chatid"))) {
                    set.add(rs.getLong("chatid"));
                }
            }
            conn.close();
            return set;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addFriend(UUID uuid, String friend) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("INSERT INTO AuthTGFriends(uuid, friend) VALUES ('" + uuid.toString() + "', '" + friend + "') ");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public UUID getUUIDbyPlayerName(String playername) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery("SELECT uuid FROM AuthTGUsers WHERE playername = '" + playername + "'");
            if (rs.next()) {
                return UUID.fromString(rs.getString("uuid"));
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void removeFriend(UUID uuid, String friend) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate("DELETE FROM AuthTGFriends WHERE uuid = '" + uuid.toString() + "' AND friend = '" + friend + "'");
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "UPDATE AuthTGUsers SET admin = true WHERE uuid = '" + uuid.toString() + "'"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void removeAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "UPDATE AuthTGUsers SET admin = false WHERE uuid = '" + uuid.toString() + "'"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public Set<String> getAdminList() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT playername FROM AuthTGUsers WHERE admin = true"
            );
            Set<String> set = new HashSet<>();
            while (rs.next()) {
                set.add(rs.getString("playername"));
            }
            conn.close();
            return set;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Set<String> getCommands(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT command FROM AuthTGCommands WHERE uuid = '" + uuid.toString() + "'"
            );
            Set<String> list = new HashSet<>();
            while (rs.next()) {
                list.add(rs.getString("command"));
            }
            conn.close();
            return list;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addCommand(UUID uuid, String command) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "INSERT INTO AuthTGCommands(uuid, command) VALUES ('" + uuid.toString() + "', '" + command + "')"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void removeCommand(UUID uuid, String command) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "DELETE FROM AuthTGCommands WHERE uuid = '" + uuid.toString() + "' AND command = '" + command + "'"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT admin FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getBoolean("admin");
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void setBanTime(UUID uuid, String dateBan, String reason, String time, String admin) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "INSERT INTO AuthTGBans(uuid, timeBan, reason, time, admin) VALUES ('" + uuid.toString() + "', '" + dateBan + "', '" + reason + "', '" + time + "', '" + admin + "')"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public String getBanTime(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT timeBan FROM AuthTGBans WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("timeBan");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getBanReason(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT reason FROM AuthTGBans WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getBanAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT admin FROM AuthTGBans WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("admin");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getBanTimeAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT time FROM AuthTGBans WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("time");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteBan(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "DELETE FROM AuthTGBans WHERE uuid = '" + uuid.toString() + "'"
            );
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isBanned(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT uuid FROM AuthTGBans WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void setMuteTime(UUID uuid, String dateMute, String reason, String time, String admin) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "INSERT INTO AuthTGMutes(uuid, timeMute, reason, time, admin) VALUES ('" + uuid.toString() + "', '" + dateMute + "', '" + reason + "', '" + time + "', '" + admin + "')"
            );
            rs = st.executeQuery(
                    "SELECT playername FROM AuthTGUsers WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                List<Object> list = new ArrayList<>();
                LocalDateTime parsedDate = LocalDateTime.parse(dateMute, DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
                list.add(0, parsedDate);
                list.add(1, reason);
                MuterEvent.muteChat(rs.getString("playername"), list);
            }
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public String getMuteTime(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT timeMute FROM AuthTGMutes WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("timeMute");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getMuteReason(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT reason FROM AuthTGMutes WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getMuteAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT admin FROM AuthTGMutes WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("admin");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getMuteTimeAdmin(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT time FROM AuthTGMutes WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                return rs.getString("time");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteMute(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "DELETE FROM AuthTGMutes WHERE uuid = '" + uuid.toString() + "'"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isMuted(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT timeMute FROM AuthTGMutes WHERE uuid = '" + uuid.toString() + "'"
            );
            if (rs.next()) {
                if (rs.getString("timeMute") != null) {
                    return true;
                }
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Map<String, List<Object>> getMutedPlayers() {
        Map<String, List<Object>> map = new HashMap<>();
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            rs = st.executeQuery(
                    "SELECT * FROM AuthTGMutes"
            );
            while (rs.next()) {
                List<Object> list = new ArrayList<>();
                LocalDateTime parsedDate = LocalDateTime.parse(rs.getString("timeMute"), DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
                list.add(0, parsedDate);
                list.add(1, rs.getString("reason"));
                String playername = getPlayerName(UUID.fromString(rs.getString("uuid")));
                map.put(playername, list);
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());;
        }
        return map;
    }

    @Override
    public void setSession(UUID uuid, String ip, LocalDateTime time) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "INSERT INTO AuthTGSessions(uuid, ip, time) VALUES ('" + uuid.toString() + "', '" + ip + "', '" + time.toString() + "')"
            );
            conn.close();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void deleteSession(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeUpdate(
                    "DELETE FROM AuthTGSessions WHERE uuid = '" + uuid.toString() + "'"
            );
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public List<Object> getSession(UUID uuid) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + "/" + this.database,
                    this.username,
                    this.password
            );
            st = conn.createStatement();
            st.executeQuery(
                    "SELECT * FROM AuthTGSessions WHERE uuid = '" + uuid.toString() + "'"
            );
            List<Object> list = new ArrayList<>();
            if (rs.next()) {
                list.add(rs.getString("ip"));
                list.add(rs.getString("time"));
            }
            conn.close();
            return list;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return List.of();
    }
}