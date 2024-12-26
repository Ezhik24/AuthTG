package org.ezhik.authtgem.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.ezhik.authtgem.User;

import java.util.HashMap;
import java.util.Map;

public class FreezerEvent implements Listener {

    private static Map<String, Location> freezeplayer = new HashMap<String, Location>();
    public static void freezeplayer(String name) {
        Player player = Bukkit.getPlayer(name);
        freezeplayer.put(name, player.getLocation());
    }
    public static void unfreezeplayer(String name) {
        freezeplayer.remove(name);

    }
    public static boolean isFreeze(Player player) {
        return freezeplayer.containsKey(player.getName());
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (freezeplayer.containsKey(event.getPlayer().getName())) {
            event.getPlayer().teleport(freezeplayer.get(event.getPlayer().getName()));
        }
    }


}
