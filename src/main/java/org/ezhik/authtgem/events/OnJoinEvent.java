package org.ezhik.authtgem.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class OnJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        File file = new File("plugins/Minetelegram/users/" + p.getUniqueId() + ".yml");
        FreezerEvent.freezeplayer(p.getName());
        if (file.exists()) {
            MuterEvent.mute(p.getName(), ChatColor.GREEN + "[MT] Авторизуйтесь! Для авторизации введите команду: /login <пароль>");
            p.sendTitle(ChatColor.RED + "Авторизуйтесь!", "для авторизации введите команду: /login <пароль>", 20, 10000000, 0);
        } else {
            MuterEvent.mute(p.getName(), ChatColor.GREEN + "[MT] Авторизуйтесь! Для авторизации введите команду: /register <пароль> <повтор пароля>");
            p.sendTitle(ChatColor.RED + "Авторизуйтесь!", "для авторизации введите команду: /register <пароль> <повтор пароля>", 20, 10000000, 0);
        }
    }
}
