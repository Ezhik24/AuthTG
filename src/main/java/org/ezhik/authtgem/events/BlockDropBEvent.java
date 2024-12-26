package org.ezhik.authtgem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class BlockDropBEvent {
    @EventHandler
    public void onBlockItem(BlockBreakEvent event) {
        if (FreezerEvent.isFreeze(event.getPlayer())) event.setCancelled(true);

    }
}
