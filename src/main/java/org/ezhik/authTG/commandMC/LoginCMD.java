package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;

import java.time.LocalDateTime;
import java.util.logging.Level;

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO,AuthTG.getMessage("notplayer", "CE"));
            return false;
        }
        if (AuthTG.notRegAndLogin) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("loginoff", "MC")));
            return false;
        }
        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("loginnousage", "MC")));
            return false;
        }
        Player player = (Player) commandSender;
        if (!AuthTG.loader.passwordValid(player.getUniqueId(), strings[0])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("loginpassnovalid", "MC")));
            return false;
        }
        User user = User.getUser(player.getUniqueId());
        if (AuthTG.authNecessarily) {
            if (user.activetg) {
                user.sendLoginAccepted(AuthTG.getMessage("loginaccept", "TG").replace("{PLAYER}", user.playername).replace("{IP}", player.getAddress().getAddress().toString().replace("/", "")));
                MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("joininaccounttext", "MC")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("joininaccounttext", "MC")));
                player.sendTitle(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("joininaccounts1", "MC")), AuthTG.getMessage("joininaccounts2", "MC"), 0,1000000000,0);
            } else {
                MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgactivetext", "MC")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgactivetext", "MC")));
                player.sendTitle(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgactives1", "MC")), AuthTG.getMessage("authtgactives2", "MC"), 0,1000000000,0);
            }
        } else {
            if (user.activetg && user.twofactor) {
                user.sendLoginAccepted(AuthTG.getMessage("loginaccept", "TG").replace("{PLAYER}", user.playername).replace("{IP}", player.getAddress().getAddress().toString().replace("/", "")));
                MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("joininaccounttext", "MC")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("joininaccounttext", "MC")));
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("joininaccounts1", "MC")), AuthTG.getMessage("joininaccounts2", "MC"), 0, 1000000000, 0);
            } else {
                if (user.activetg) {
                    if (user.friends != null) {
                        for (String friend : user.friends) {
                            User friendUser = User.getUser(friend);
                            if (friendUser.activetg) {
                                friendUser.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendjoin", "TG").replace("{PLAYER}", user.playername)));
                            } else {
                                AuthTG.loader.removeFriend(friendUser.uuid, user.playername);
                                AuthTG.loader.removeFriend(user.uuid, friendUser.playername);
                            }
                        }
                    }
                }
                LocalDateTime time = LocalDateTime.now().plusMinutes(AuthTG.timeoutSession);
                AuthTG.sessionManager.addAuthorized(player.getUniqueId(), player.getAddress().getAddress().toString(),time);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("loginsuccess", "MC")));
                FreezerEvent.unfreezeplayer(player.getName());
                if (FreezerEvent.beforeFreeze.containsKey(player.getName())) {
                    Handler.teleport(player.getName(), FreezerEvent.beforeFreeze.get(player.getName()));
                    FreezerEvent.beforeFreeze.remove(player.getName());
                }
                MuterEvent.unmute(player.getName());
                player.resetTitle();
                if (AuthTG.kickTimeout != 0) {
                    AuthHandler.removeTimeout(player.getUniqueId());
                }
            }
        }
        return true;
    }
}
