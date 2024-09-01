package org.ezhik.authtgem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class BlockCommandEvent implements Listener {
    @EventHandler
    public void onBlockCommands(PlayerCommandPreprocessEvent event) {
        System.out.println(event.getMessage());
            }
}
