package org.ezhik.authtgem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Handler extends BukkitRunnable {
    private static Map<String, String> kickplayers = new HashMap<>();
    @Override
    public void run() {
        if (kickplayers.size() != 0) {
            for(String name : kickplayers.keySet()) {
                Player player = Bukkit.getPlayer(name);
                if(player == null) {
                    kickplayers.remove(name);
                } else {
                    player.kickPlayer(kickplayers.get(name));
                    kickplayers.remove(name);
                }
            }
        }

    }

    public static void kick(String name,String reason) {
        kickplayers.put(name, reason);
    }

}
