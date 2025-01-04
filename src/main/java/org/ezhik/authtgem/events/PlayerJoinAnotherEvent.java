package org.ezhik.authtgem.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.ezhik.authtgem.Handler;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class PlayerJoinAnotherEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getPlayer(event.getName());
        if (player != null) {
            if (player.isOnline()) {
                event.disallow(Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', "&c&lКто-то уже играет с этого никнейма..."));
            }
        }
    }
}
