package org.ezhik.authTG.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ezhik.authTG.AuthTG;

public class onLeaveEvent implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        if (AuthTG.loader.isBanned(e.getPlayer().getUniqueId())){
            e.setQuitMessage(null);
        }
    }
}
