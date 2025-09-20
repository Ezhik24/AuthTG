package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class OnJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (AuthTG.config.getLocation("spawnLocation") != null) p.teleport(AuthTG.config.getLocation("spawnLocation"));
        FreezerEvent.freezeplayer(p.getName());
        User user = User.getUser(p.getUniqueId());
        LocalDateTime date = LocalDateTime.now();
        if (AuthTG.loader.getBanTime(p.getUniqueId()) != null) {
            if (AuthTG.loader.getBanTime(p.getUniqueId()).equals("0")) {
                Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{REASON}", AuthTG.loader.getBanReason(p.getUniqueId())).replace("{TIMEBAN}", "навсегда").replace("{TIME}",AuthTG.loader.getBanTimeAdmin(p.getUniqueId())).replace("{ADMIN}", AuthTG.loader.getBanAdmin(p.getUniqueId())).replace("{BR}", "\n"));
                return;
            }
            LocalDateTime date1 = LocalDateTime.parse(AuthTG.loader.getBanTime(p.getUniqueId()), DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
            if (date.isAfter(date1)) AuthTG.loader.deleteBan(p.getUniqueId());
            else Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{REASON}", AuthTG.loader.getBanReason(p.getUniqueId())).replace("{TIMEBAN}", AuthTG.loader.getBanTime(p.getUniqueId())).replace("{TIME}",AuthTG.loader.getBanTimeAdmin(p.getUniqueId())).replace("{ADMIN}", AuthTG.loader.getBanAdmin(p.getUniqueId())).replace("{BR}", "\n"));
        }
        if (p.getName().length() < AuthTG.config.getInt("minLenghtNickname") || p.getName().length() > AuthTG.config.getInt("maxLenghtNickname")) {
            Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.nicknamelenght").replace("{MIN}", String.valueOf(AuthTG.config.getInt("minLenghtNickname"))).replace("{MAX}", String.valueOf(AuthTG.config.getInt("maxLenghtNickname")))));
        }
        AuthHandler.setTimeout(p.getUniqueId(), AuthTG.config.getInt("kickTimeout"));
        if (AuthTG.config.getBoolean("notRegAndLogin")) {
            if (user != null && user.activetg) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.joininaccounttext")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.joininaccounts1")), AuthTG.config.getString("messages.minecraft.joininaccounts1"), 20, 10000000, 0);
                user.sendLoginAccepted(AuthTG.config.getString("messages.telegram.loginaccept").replace("{PLAYER}", user.playername));
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.authtgactivetext")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.authtgactives1")), AuthTG.config.getString("messages.minecraft.authtgactives1"), 20, 10000000, 0);
            }
        } else {
            if (user != null) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.loginmessage")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.logintitles1")), AuthTG.config.getString("messages.minecraft.logintitles2"), 20, 10000000, 0);
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.registermessage")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.registertitles1")), AuthTG.config.getString("messages.minecraft.registertitles2"), 20, 10000000, 0);
            }
        }
        if (user != null && user.activetg) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.joinacc"));
            List<String> list = AuthTG.loader.getListFriends(user.uuid);
            if (list != null) {
                for (String s : list) {
                    User user1 = User.getUser(s);
                    if (user1.activetg) {
                        user1.sendMessage(AuthTG.config.getString("messages.telegram.friendjoin").replace("{PLAYER}", user.playername));
                    } else {
                        AuthTG.loader.removeFriend(user.uuid, s);
                        AuthTG.loader.removeFriend(user1.uuid, user.playername);
                    }
                }
            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.accnotactivetg")));
        }
    }
}
