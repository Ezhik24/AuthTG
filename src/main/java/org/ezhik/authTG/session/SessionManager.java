package org.ezhik.authTG.session;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SessionManager {
    boolean isAuthorized(Player player);

    void deleteAuthorized(UUID uuid);

    void addAuthorized(UUID uuid, String ip, LocalDateTime time);
}
