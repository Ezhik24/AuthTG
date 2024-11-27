package org.ezhik.authtgem.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.User;

import java.io.File;

public class OnJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        File file = new File("plugins/Minetelegram/users/" + p.getUniqueId() + ".yml");
        if (User.getSpawnLocation() != null) p.teleport(User.getSpawnLocation());
        FreezerEvent.freezeplayer(p.getName());
        if (file.exists()) {
            MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_message")));
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_title_login_s1")), AuthTGEM.messageMC.get("login_title_login_s2"), 20, 10000000, 0);
        } else {
            MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_message")));
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_title_s1")), AuthTGEM.messageMC.get("register_title_s2"), 20, 10000000, 0);
        }
        User user = User.getUser(p.getUniqueId());
        if (user != null) {
            for (User u : user.getUnicFriends()) {
                u.sendMessageB(AuthTGEM.messageTG.getPNFriendOnJoin(p.getPlayer()), p.getName());
            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("joinplayer_tgasign")));
        }
    }
}
