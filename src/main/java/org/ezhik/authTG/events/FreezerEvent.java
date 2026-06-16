package org.ezhik.authTG.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FreezerEvent implements Listener {

    private static final Map<String, Location> freezeplayer = new ConcurrentHashMap<>();
    public static final Map<String, Location> beforeFreeze = new ConcurrentHashMap<>();

    public static void freezeplayer(Player player, Location location) {
        if (player == null || location == null) {
            return;
        }

        freezeplayer.put(player.getName(), location.clone());
    }

    public static void unfreezeplayer(String name) {
        freezeplayer.remove(name);
        User user = User.getUser(name);
        if (user != null && user.activetg) {
            user.sendMessage(AuthTG.getMessage("joinacc", "TG").replace("{IP}", user.player.getAddress().getAddress().toString()).replace("/", ""));
        }
    }

    public static boolean isFreeze(Player player) {
        return player != null && freezeplayer.containsKey(player.getName());
    }

    public static boolean isFreeze(String name) {
        return name != null && freezeplayer.containsKey(name);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location frozenLocation = freezeplayer.get(event.getPlayer().getName());
        if (frozenLocation == null || event.getTo() == null) {
            return;
        }

        Location to = event.getTo();
        if (samePosition(to, frozenLocation)) {
            return;
        }

        Location target = frozenLocation.clone();
        target.setYaw(to.getYaw());
        target.setPitch(to.getPitch());
        event.setTo(target);
    }

    private boolean samePosition(Location first, Location second) {
        if (first == null || second == null) {
            return false;
        }

        if (first.getWorld() == null || second.getWorld() == null || !first.getWorld().equals(second.getWorld())) {
            return false;
        }

        return Double.compare(first.getX(), second.getX()) == 0
                && Double.compare(first.getY(), second.getY()) == 0
                && Double.compare(first.getZ(), second.getZ()) == 0;
    }
}