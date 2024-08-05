package org.ezhik.authtgem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ezhik.authtgem.User;

public class OnPlayerQuitEvent implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        if (user != null){
            if (player.hasPermission("mt.admin")) {
                user.setAdmin();
            }
            if (player.hasPermission("mt.moderator")) {
                user.setModerator();
            }
        }

    }

}
