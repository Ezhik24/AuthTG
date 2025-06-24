package org.ezhik.authTG.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockDropBEvent implements Listener {
    @EventHandler
    public void onBlockItem(BlockBreakEvent event) {
        if (FreezerEvent.isFreeze(event.getPlayer())) event.setCancelled(true);

    }
}
