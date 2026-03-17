package org.ezhik.authTG.events;

import org.bukkit.Bukkit;
import org.ezhik.authTG.util.MessageHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

public class PlayerJoinAnotherEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getPlayer(event.getName());
        if (player != null) {
            if (player.isOnline()) {
                event.disallow(Result.KICK_OTHER, MessageHelper.legacySection(AuthTG.getMessage("joinanother", "MC")));
            }
        }
    }
}
