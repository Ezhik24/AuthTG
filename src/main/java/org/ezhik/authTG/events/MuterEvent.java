package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ezhik.authTG.AuthTG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuterEvent implements Listener {
    private static Map<String, String> mutedplayers = new HashMap<>();
    private static Map<String, List<Object>> mute = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (mutedplayers.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(mutedplayers.get(event.getPlayer().getName()));
        }
        if (mute.containsKey(event.getPlayer().getName())) {
            List<Object> list = mute.get(event.getPlayer().getName());
            if (LocalDateTime.now().isAfter((LocalDateTime) list.get(0))) {
                mute.remove(event.getPlayer().getName());
                AuthTG.loader.deleteMute(event.getPlayer().getUniqueId());
                return;
            }
            if (AuthTG.loader.isMuted(event.getPlayer().getUniqueId())) {
                if (list.get(0).toString().equals("0")) {
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(event.getPlayer().getUniqueId())).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(event.getPlayer().getUniqueId())).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(event.getPlayer().getUniqueId())).replace("{BR}", "\n");
                    event.getPlayer().sendMessage(message);
                } else {
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(event.getPlayer().getUniqueId())).replace("{REASON}", AuthTG.loader.getMuteReason(event.getPlayer().getUniqueId())).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(event.getPlayer().getUniqueId())).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(event.getPlayer().getUniqueId())).replace("{BR}", "\n");
                    event.getPlayer().sendMessage(message);
                }
                event.setCancelled(true);
            }
        }
    }
    public static void mute(String name, String reason) {
        mutedplayers.put(name, reason);
    }
    public static void unmute(String name) {
        mutedplayers.remove(name);
    }
    public static boolean isMute(Player player){
        return mutedplayers.containsKey(player.getName());
    }

    public static void muteChat(String name, List<Object> list) {
        mute.put(name, list);
    }
    public static void setMutedPlayers(Map<String, List<Object>> map) {
        mute = map;
    }
    public static boolean isMuteChat(Player player) {
        return mute.containsKey(player.getName());
    }
    public static void unmuteChat(String name) {
        mute.remove(name);
    }
    public static List<Object> getMuteChat(String name) {
        return mute.get(name);
    }
}