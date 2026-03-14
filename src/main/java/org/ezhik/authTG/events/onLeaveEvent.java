package org.ezhik.authTG.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.captcha.Captcha;

public class onLeaveEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Captcha.clear(event.getPlayer().getUniqueId());

        if (AuthTG.loader.isBanned(event.getPlayer().getUniqueId())) {
            event.setQuitMessage(null);
        }
    }
}
