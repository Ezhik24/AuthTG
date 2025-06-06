package org.ezhik.authtgem.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
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
        if (User.getSpawnLocation() != null) p.teleport(User.getSpawnLocation());
        FreezerEvent.freezeplayer(p.getName());
        File file = new File("plugins/Minetelegram/users/" + p.getUniqueId() + ".yml");
        YamlConfiguration userconfig = YamlConfiguration.loadConfiguration(file);
        User user;
        if (p.getName().length() < AuthTGEM.bot.minLenghtNickname || p.getName().length() > AuthTGEM.bot.maxLenghtNickname) {
            p.kickPlayer(ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("join_LenghtPass")));
        }
        if (AuthTGEM.bot.notRegAndLogin) {
            user = User.getUser(p.getUniqueId());
            if (user != null) { 
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("telegram_entered_conf")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&',"login_title_tg_s1"), "login_title_tg_s2", 20, 10000000,0);
                user.sendLoginAccepted(AuthTGEM.messageTG.get("login_who_entered"));
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("account_notregandlogin_text")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("account_notregandlogin_s1")), AuthTGEM.messageMC.get("account_notregandlogin_s2"),20,10000000,0);
            }
        } else {
            if (AuthTGEM.bot.authNecessarily) user = User.getUser(p.getUniqueId());
            else user = User.getUserJoin(p.getUniqueId());
            if (user != null || userconfig.contains("password")) {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_message")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_title_login_s1")), AuthTGEM.messageMC.get("login_title_login_s2"), 20, 10000000, 0);
            } else {
                MuterEvent.mute(p.getName(), ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_message")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_title_s1")), AuthTGEM.messageMC.get("register_title_s2"), 20, 10000000, 0);
            }
        }
        if (user != null) {
            user.sendMessage(AuthTGEM.messageTG.getAccLogining(p.getName()));
            for (User u : user.getUnicFriends()) {
                u.sendMessageB(AuthTGEM.messageTG.getPNFriendOnJoin(p.getPlayer()), p.getName());
            }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("joinplayer_tgasign")));
        }
    }
}
