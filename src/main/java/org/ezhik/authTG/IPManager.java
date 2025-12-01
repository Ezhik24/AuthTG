package org.ezhik.authTG;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class IPManager{
    public static boolean isAuthorized(Player player) {
        if (AuthTG.loader.getSession(player.getUniqueId()) != null) {
            if (AuthTG.loader.getSession(player.getUniqueId()).get(0).equals(player.getAddress().getAddress().toString())) {
                LocalDateTime time = LocalDateTime.now();
                if (time.isBefore(LocalDateTime.parse(AuthTG.loader.getSession(player.getUniqueId()).get(1).toString()))) {
                    return true;
                } else {
                    AuthTG.loader.deleteSession(player.getUniqueId());
                    return false;
                }
            }
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
