package org.ezhik.authtgem.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class BlockCommandEvent implements Listener {
    @EventHandler
    public void onCommmand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(MuterEvent.isMute(player)) {
           if(!(event.getMessage().startsWith("/login") || event.getMessage().startsWith("/register") || event.getMessage().startsWith("/reg") || event.getMessage().startsWith("/l"))) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lЭта команда доступна только для зарегистрированных пользователей!"));
           }
        }
    }
}
