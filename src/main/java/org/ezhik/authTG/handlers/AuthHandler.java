package org.ezhik.authTG.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.ezhik.authTG.AuthTG;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthHandler extends BukkitRunnable {
    private static Map<UUID, Integer> timeoutMap = new HashMap<>();
    @Override
    public void run() {
        if (!timeoutMap.isEmpty()) {
            for (Map.Entry<UUID, Integer> entry : timeoutMap.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    if (entry.getValue() > 0) {
                        entry.setValue(entry.getValue() - 1);
                    } else {
                        player.kickPlayer(AuthTG.getMessage("kicktimeout", "MC"));
                        timeoutMap.remove(entry.getKey());
                    }
                } else {
                    timeoutMap.remove(entry.getKey());
                }
            }
        }
    }

    public static void setTimeout(UUID uuid, int time) {
        timeoutMap.put(uuid, time);
    }

    public static void removeTimeout(UUID uuid) {
        if (timeoutMap.containsKey(uuid)) timeoutMap.remove(uuid);
    }
}
