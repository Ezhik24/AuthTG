package org.ezhik.authtgem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class MuterEvent implements Listener {
    private static Map<String, String> mutedplayers = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (mutedplayers.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(mutedplayers.get(event.getPlayer().getName()));
        }
    }
    public static void mute(String name, String reason) {
        mutedplayers.put(name, reason);
    }
    public static void unmute(String name) {
        mutedplayers.remove(name);
    }
    public static boolean isMute(Player player){
        return mutedplayers.containsKey(player.getName());
    }
}

