package org.ezhik.authTG;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class IPManager{
    public static boolean isAuthorized(Player player) {
        List<Object> session = AuthTG.loader.getSession(player.getUniqueId());
        if (session == null || session.size() < 2 || player.getAddress() == null) {
            return false;
        }
        String sessionIP = String.valueOf(session.get(0));
        String playerIP = player.getAddress().getAddress().toString();
        if (!sessionIP.equals(playerIP)) {
            return false;
        }
        try {
            LocalDateTime date = LocalDateTime.now();
            LocalDateTime expireTime = LocalDateTime.parse(String.valueOf(session.get(1)));
            return date.isBefore(expireTime);
        } catch (DateTimeParseException e) {
            AuthTG.logger.log(Level.WARNING, "Invalid session time for player " + player.getUniqueId());
            AuthTG.loader.deleteSession(player.getUniqueId());
        }
        return false;
    }

    public static void deleteAuthorized(UUID uuid) {
        AuthTG.loader.deleteSession(uuid);
    }

    public static void addAuthorized(UUID uuid, String ip, LocalDateTime time) {
        AuthTG.loader.setSession(uuid, ip, time);
    }
}
