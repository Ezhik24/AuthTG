package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ezhik.authTG.AuthTG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.List.*;

public class BlockCommandEvent implements Listener {
    @EventHandler
    public void onCommmand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(MuterEvent.isMute(player)) {
            List<String> commands = new ArrayList<>(List.of("/login", "/register", "/reg", "/l", "/code"));
            commands.addAll(AuthTG.commandsPreAuthorization);
            String command = event.getMessage().split(" ")[0];
            if(!commands.contains(command)) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("joinblock", "MC")));
            }
        }
        if (MuterEvent.isMuteChat(player)) {
            String[] args = event.getMessage().split(" ");
            if (args[0].contains(AuthTG.mutecommands.toString())) {
                List<Object> list = MuterEvent.getMuteChat(event.getPlayer().getName());
                if (LocalDateTime.now().isAfter((LocalDateTime) list.get(0))) {
                    MuterEvent.unmuteChat(event.getPlayer().getName());
                    AuthTG.loader.deleteMute(event.getPlayer().getUniqueId());
                    return;
                }
                if (list.get(0).toString().equals("0")) {
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(player.getUniqueId())).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(event.getPlayer().getUniqueId())).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(event.getPlayer().getUniqueId()));
                    event.getPlayer().sendMessage(message);
                } else {
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(player.getUniqueId())).replace("{REASON}", AuthTG.loader.getMuteReason(player.getUniqueId())).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(event.getPlayer().getUniqueId())).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(event.getPlayer().getUniqueId()));
                    event.getPlayer().sendMessage(message);
                }
                event.setCancelled(true);
            }
        }
    }
}
