package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ezhik.authTG.User;

public class OnJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        FreezerEvent.freezeplayer(p.getName());
        User user = User.getUser(p.getUniqueId());
        if (user != null) {
            MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lВойдите в аккаунт,используйте /login <пароль>"));
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lВойдите в аккаунт"), "/login <пароль>", 20, 10000000, 0);
        } else {
            MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lЗарегистрируйтесь, используйте /register <пароль> <повтор пароля>"));
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lЗарегистрируйтесь"), "/reg <пароль> <повтор пароля>", 20, 10000000, 0);
        }
        if (user != null && user.activetg) {
            user.sendMessage("Ваш аккаунт вошел в игру");
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lПривяжите аккаунт к Телеграму"));
        }
    }
}
