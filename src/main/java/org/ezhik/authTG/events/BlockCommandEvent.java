package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ezhik.authTG.AuthTG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BlockCommandEvent implements Listener {
    @EventHandler
    public void onCommmand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(MuterEvent.isMute(player)) {
           if(!(event.getMessage().startsWith("/login") || event.getMessage().startsWith("/register") || event.getMessage().startsWith("/reg") || event.getMessage().startsWith("/l") || event.getMessage().startsWith("/code"))) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.joinblock")));
           }
        }
        if (MuterEvent.isMuteChat(player)) {
            String[] args = event.getMessage().split(" ");
            if (args[0].contains(AuthTG.config.getList("mutecommands").toString())) {
                List<Object> list = MuterEvent.getMuteChat(event.getPlayer().getName());
                if (LocalDateTime.now().isAfter((LocalDateTime) list.get(0))) {
                    MuterEvent.unmuteChat(event.getPlayer().getName());
                    AuthTG.loader.deleteMute(event.getPlayer().getUniqueId());
                } else {
                    if (list.get(0).toString().equals("0")) {
                        String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", list.get(1).toString()).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(event.getPlayer().getUniqueId())).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(event.getPlayer().getUniqueId())).replace("{BR}", "\n");
                        event.getPlayer().sendMessage(message);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                        LocalDateTime localDateTime = (LocalDateTime) list.get(0);
                        String formatteddate = localDateTime.format(formatter);
                        String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", formatteddate).replace("{REASON}", list.get(1).toString()).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(event.getPlayer().getUniqueId())).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(event.getPlayer().getUniqueId())).replace("{BR}", "\n");
                        event.getPlayer().sendMessage(message);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
