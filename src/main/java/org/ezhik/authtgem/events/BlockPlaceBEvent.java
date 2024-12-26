package org.ezhik.authtgem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceBEvent implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (FreezerEvent.isFreeze(event.getPlayer())) event.setCancelled(true);

    }
}
