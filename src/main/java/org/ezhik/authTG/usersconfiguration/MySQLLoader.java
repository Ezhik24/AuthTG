package org.ezhik.authTG.usersconfiguration;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.PasswordHasher;
import org.ezhik.authTG.events.MuterEvent;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;

public class MySQLLoader implements Loader {

    private final DataSource ds;

    public MySQLLoader(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void setPlayerName(UUID uuid, String playername) {
        String sql = "INSERT INTO AuthTGUsers(uuid, playername, currentUUID) VALUES (?, ?, false)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, playername);
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setPasswordHash(UUID uuid, String password) {
        String sql = "UPDATE AuthTGUsers SET password=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, PasswordHasher.hashPassword(password));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setActive(UUID uuid, boolean active) {
        String sql = "UPDATE AuthTGUsers SET active=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isActive(UUID uuid) {
        String sql = "SELECT active FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("active");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean passwordValid(UUID uuid, String password) {
        String sql = "SELECT password FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return PasswordHasher.hashPassword(password).equals(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        String sql = "SELECT playername FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("playername");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return "";
    }

    @Override
    public boolean getTwofactor(UUID uuid) {
        String sql = "SELECT twofactor FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("twofactor");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean getActiveTG(UUID uuid) {
        String sql = "SELECT activeTG FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("activeTG");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<String> getListFriends(UUID uuid) {
        String sql = "SELECT friend FROM AuthTGFriends WHERE uuid=?";
        List<String> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String f = rs.getString("friend");
                    if (!list.contains(f)) list.add(f);
                }
            }
            return list;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getUserName(UUID uuid) {
        String sql = "SELECT username FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("username");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getFirstName(UUID uuid) {
        String sql = "SELECT firstname FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("firstname");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getLastName(UUID uuid) {
        String sql = "SELECT lastname FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("lastname");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Long getChatID(UUID uuid) {
        String sql = "SELECT chatid FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("chatid");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return 0L;
    }

    @Override
    public UUID getCurrentUUID(Long chatid) {
        String sql = "SELECT uuid FROM AuthTGUsers WHERE chatid=? AND currentUUID=true LIMIT 1";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setCurrentUUID(UUID uuid, Long chatid) {
        String clear = "UPDATE AuthTGUsers SET currentUUID=false WHERE chatid=?";
        String set = "UPDATE AuthTGUsers SET currentUUID=true WHERE uuid=?";
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(clear)) {
                ps.setLong(1, chatid);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(set)) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setChatID(UUID uuid, Long chatid) {
        String sql = "UPDATE AuthTGUsers SET chatid=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatid);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setUsername(UUID uuid, String username) {
        String sql = "UPDATE AuthTGUsers SET username=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setLastName(UUID uuid, String lastname) {
        String sql = "UPDATE AuthTGUsers SET lastname=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, lastname);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setFirstName(UUID uuid, String firstname) {
        String sql = "UPDATE AuthTGUsers SET firstname=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firstname);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setTwofactor(UUID uuid, boolean state) {
        String sql = "UPDATE AuthTGUsers SET twofactor=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, state);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setActiveTG(UUID uuid, boolean state) {
        String sql = "UPDATE AuthTGUsers SET activeTG=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, state);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public List<UUID> getPlayerNames(Long chatid) {
        String sql = "SELECT uuid FROM AuthTGUsers WHERE chatid=? AND activeTG=true";
        List<UUID> list = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID u = UUID.fromString(rs.getString("uuid"));
                    if (!list.contains(u)) list.add(u);
                }
            }
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
        String sql = "SELECT chatid FROM AuthTGUsers";
        Set<Long> set = new HashSet<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) set.add(rs.getLong("chatid"));
            return set;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addFriend(UUID uuid, String friend) {
        String sql = "INSERT INTO AuthTGFriends(uuid, friend) VALUES (?, ?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, friend);
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public UUID getUUIDbyPlayerName(String playername) {
        String sql = "SELECT uuid FROM AuthTGUsers WHERE playername=? LIMIT 1";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void removeFriend(UUID uuid, String friend) {
        String sql = "DELETE FROM AuthTGFriends WHERE uuid=? AND friend=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, friend);
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void setAdmin(UUID uuid) {
        String sql = "UPDATE AuthTGUsers SET admin=true WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void removeAdmin(UUID uuid) {
        String sql = "UPDATE AuthTGUsers SET admin=false WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public Set<String> getAdminList() {
        String sql = "SELECT playername FROM AuthTGUsers WHERE admin=true";
        Set<String> set = new HashSet<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) set.add(rs.getString("playername"));
            return set;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Set<String> getCommands(UUID uuid) {
        String sql = "SELECT command FROM AuthTGCommands WHERE uuid=?";
        Set<String> list = new HashSet<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getString("command"));
            }
            return list;
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void addCommand(UUID uuid, String command) {
        String sql = "INSERT INTO AuthTGCommands(uuid, command) VALUES (?, ?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, command);
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void removeCommand(UUID uuid, String command) {
        String sql = "DELETE FROM AuthTGCommands WHERE uuid=? AND command=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, command);
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isAdmin(UUID uuid) {
        String sql = "SELECT admin FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("admin");
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void setBanTime(UUID uuid, String dateBan, String reason, String time, String admin) {
        String sql = "INSERT INTO AuthTGBans(uuid, timeBan, reason, time, admin) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, dateBan);
            ps.setString(3, reason);
            ps.setString(4, time);
            ps.setString(5, admin);
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override public String getBanTime(UUID uuid) { return getOneString("SELECT timeBan FROM AuthTGBans WHERE uuid=?", uuid); }
    @Override public String getBanReason(UUID uuid) { return getOneString("SELECT reason FROM AuthTGBans WHERE uuid=?", uuid); }
    @Override public String getBanAdmin(UUID uuid) { return getOneString("SELECT admin FROM AuthTGBans WHERE uuid=?", uuid); }
    @Override public String getBanTimeAdmin(UUID uuid) { return getOneString("SELECT time FROM AuthTGBans WHERE uuid=?", uuid); }

    @Override
    public void deleteBan(UUID uuid) {
        String sql = "DELETE FROM AuthTGBans WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isBanned(UUID uuid) {
        String sql = "SELECT 1 FROM AuthTGBans WHERE uuid=? LIMIT 1";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void setMuteTime(UUID uuid, String dateMute, String reason, String time, String admin) {
        String insert = "INSERT INTO AuthTGMutes(uuid, timeMute, reason, time, admin) VALUES (?, ?, ?, ?, ?)";
        String pname = "SELECT playername FROM AuthTGUsers WHERE uuid=? LIMIT 1";

        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(insert)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, dateMute);
                ps.setString(3, reason);
                ps.setString(4, time);
                ps.setString(5, admin);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(pname)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        List<Object> list = new ArrayList<>();
                        LocalDateTime parsedDate = LocalDateTime.parse(dateMute, DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
                        list.add(0, parsedDate);
                        list.add(1, reason);
                        MuterEvent.muteChat(rs.getString("playername"), list);
                    }
                }
            }

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override public String getMuteTime(UUID uuid) { return getOneString("SELECT timeMute FROM AuthTGMutes WHERE uuid=?", uuid); }
    @Override public String getMuteReason(UUID uuid) { return getOneString("SELECT reason FROM AuthTGMutes WHERE uuid=?", uuid); }
    @Override public String getMuteAdmin(UUID uuid) { return getOneString("SELECT admin FROM AuthTGMutes WHERE uuid=?", uuid); }
    @Override public String getMuteTimeAdmin(UUID uuid) { return getOneString("SELECT time FROM AuthTGMutes WHERE uuid=?", uuid); }

    @Override
    public void deleteMute(UUID uuid) {
        String sql = "DELETE FROM AuthTGMutes WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public boolean isMuted(UUID uuid) {
        String sql = "SELECT timeMute FROM AuthTGMutes WHERE uuid=? LIMIT 1";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("timeMute") != null;
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Map<String, List<Object>> getMutedPlayers() {
        Map<String, List<Object>> map = new HashMap<>();

        // меньше запросов: подтягиваем playername JOIN'ом
        String sql = "SELECT m.timeMute, m.reason, u.playername " +
                "FROM AuthTGMutes m " +
                "LEFT JOIN AuthTGUsers u ON u.uuid = m.uuid";

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String playername = rs.getString("playername");
                if (playername == null) continue;

                List<Object> list = new ArrayList<>();
                LocalDateTime parsedDate = LocalDateTime.parse(rs.getString("timeMute"), DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
                list.add(0, parsedDate);
                list.add(1, rs.getString("reason"));

                map.put(playername, list);
            }

        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }

        return map;
    }

    @Override
    public void setSession(UUID uuid, String ip, LocalDateTime time) {
        String sql = "UPDATE AuthTGUsers SET ip=?, time=? WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ip);
            ps.setString(2, String.valueOf(time));
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public void deleteSession(UUID uuid) {
        String sql = "UPDATE AuthTGUsers SET ip='0', time='0' WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
    }

    @Override
    public List<Object> getSession(UUID uuid) {
        String sql = "SELECT ip, time FROM AuthTGUsers WHERE uuid=?";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<Object> list = new ArrayList<>();
                if (rs.next()) {
                    list.add(rs.getString("ip"));
                    list.add(rs.getString("time"));
                }
                return list;
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return List.of();
    }

    private String getOneString(String sql, UUID uuid) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            AuthTG.logger.log(Level.SEVERE, "SQLException: " + e.getMessage());
        }
        return null;
    }
}
