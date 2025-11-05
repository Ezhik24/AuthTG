package org.ezhik.authTG.session;

import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IPManager implements SessionManager{
    @Override
    public boolean isAuthorized(Player player) {
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

    @Override
    public void deleteAuthorized(UUID uuid) {
        AuthTG.loader.deleteSession(uuid);
    }

    @Override
    public void addAuthorized(UUID uuid, String ip, LocalDateTime time) {
        AuthTG.loader.setSession(uuid, ip, time);
    }
}
