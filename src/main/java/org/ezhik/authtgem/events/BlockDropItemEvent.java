package org.ezhik.authtgem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Map;

public class BlockDropItemEvent implements Listener {
    @EventHandler
    public void onBlockItem(PlayerDropItemEvent event) {
        if (FreezerEvent.isFreeze(event.getPlayer())) event.setCancelled(true);

    }
}
