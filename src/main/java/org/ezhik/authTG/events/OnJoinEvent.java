package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.Handler;
import org.ezhik.authTG.User;

public class OnJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (AuthTG.config.getLocation("spawnLocation") != null) p.teleport(AuthTG.config.getLocation("spawnLocation"));
        FreezerEvent.freezeplayer(p.getName());
        User user = User.getUser(p.getUniqueId());
        if (p.getName().length() < AuthTG.config.getInt("minLenghtNickname") || p.getName().length() > AuthTG.config.getInt("maxLenghtNickname")) {
            Handler.kick(p.getName(), ChatColor.translateAlternateColorCodes('&',"&c&lНикнейм должен быть от " + AuthTG.config.getInt("minLenghtNickname") + " до " + AuthTG.config.getInt("maxLenghtNickname") + " символов"));
        }
        if (AuthTG.config.getBoolean("notRegAndLogin")) {
            if (user != null && user.activetg) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lПотвердите вход через Телеграм"));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lПотвердите вход"), "через Телеграм", 20, 10000000, 0);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lПотвердите вход через Телеграм"));
                user.sendLoginAccepted("[Бот@" + user.playername + "] Это вы вошли в игру?");
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lПривяжите аккаунт к Телеграму"));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lПривяжите аккаунт"), "к Телеграму", 20, 10000000, 0);
            }
        } else {
            if (user != null) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lВойдите в аккаунт,используйте /login <пароль>"));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lВойдите в аккаунт"), "/login <пароль>", 20, 10000000, 0);
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lЗарегистрируйтесь, используйте /register <пароль> <повтор пароля>"));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lЗарегистрируйтесь"), "/reg <пароль> <повтор пароля>", 20, 10000000, 0);
            }
        }
        if (user != null && user.activetg) {
            user.sendMessage("Ваш аккаунт вошел в игру");
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lПривяжите аккаунт к Телеграму"));
        }
    }
}
