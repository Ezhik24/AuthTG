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
        if (AuthTG.spawn != null) {
            p.teleport(AuthTG.spawn);
            FreezerEvent.freezeplayer(p, AuthTG.spawn);
        } else {
            FreezerEvent.freezeplayer(p, p.getLocation());
        }
        User user = User.getUser(p.getUniqueId());
        LocalDateTime date = LocalDateTime.now();
        if (AuthTG.loader.getBanTime(p.getUniqueId()) != null) {
            if (AuthTG.loader.getBanTime(p.getUniqueId()).equals("0")) {
                event.setJoinMessage(null);
                Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("ban", "MC")).replace("{REASON}", AuthTG.loader.getBanReason(p.getUniqueId())).replace("{TIMEBAN}", "навсегда").replace("{TIME}",AuthTG.loader.getBanTimeAdmin(p.getUniqueId())).replace("{ADMIN}", AuthTG.loader.getBanAdmin(p.getUniqueId())));
                return;
            }
            LocalDateTime date1 = LocalDateTime.parse(AuthTG.loader.getBanTime(p.getUniqueId()), DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
            if (date.isAfter(date1)) AuthTG.loader.deleteBan(p.getUniqueId());
            else  {
                event.setJoinMessage(null);
                Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("ban", "MC")).replace("{REASON}", AuthTG.loader.getBanReason(p.getUniqueId())).replace("{TIMEBAN}", AuthTG.loader.getBanTime(p.getUniqueId())).replace("{TIME}",AuthTG.loader.getBanTimeAdmin(p.getUniqueId())).replace("{ADMIN}", AuthTG.loader.getBanAdmin(p.getUniqueId())));
            }
        }
        if (p.getName().length() < AuthTG.config.getInt("minLenghtNickname") || p.getName().length() > AuthTG.config.getInt("maxLenghtNickname")) {
            Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("nicknamelenght", "MC").replace("{MIN}", String.valueOf(AuthTG.config.getInt("minLenghtNickname"))).replace("{MAX}", String.valueOf(AuthTG.config.getInt("maxLenghtNickname")))));
        }
        AuthHandler.setTimeout(p.getUniqueId(), AuthTG.config.getInt("kickTimeout"));
        if (AuthTG.config.getBoolean("notRegAndLogin") && !AuthTG.config.getBoolean("authNecessarily")) {
            FreezerEvent.unfreezeplayer(p.getName());
        }
        else if (AuthTG.config.getBoolean("notRegAndLogin") && AuthTG.config.getBoolean("authNecessarily")) {
            if (user != null && user.activetg) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("joininaccounttext", "MC")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("joininaccounts1", "MC")), AuthTG.getMessage("joininaccounts2", "MC"), 20, 10000000, 0);
                user.sendLoginAccepted(AuthTG.getMessage("loginaccept", "TG").replace("{PLAYER}", user.playername));
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("authtgactivetext", "MC")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("authtgactives1", "MC")), AuthTG.getMessage("authtgactives2", "MC"), 20, 10000000, 0);
            }
        } else {
            if (user != null) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("loginmessage", "MC")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("logintitles1", "MC")), AuthTG.getMessage("logintitles2", "MC"), 20, 10000000, 0);
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("registermessage", "MC")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("registertitles1", "MC")), AuthTG.getMessage("registertitles2", "MC"), 20, 10000000, 0);
            }
        }
    }
}
