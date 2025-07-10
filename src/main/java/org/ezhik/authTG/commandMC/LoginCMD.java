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

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
        if (AuthTG.config.getBoolean("notRegAndLogin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.loginoff")));
            return false;
        }
        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.loginnousage")));
            return false;
        }
        Player player = (Player) commandSender;
        if (!AuthTG.loader.passwordValid(player.getUniqueId(), strings[0])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.loginpassnovalid")));
            return false;
        }
        User user = User.getUser(player.getUniqueId());
        if (AuthTG.config.getBoolean("authNecessarily")) {
            if (user.activetg) {
                user.sendLoginAccepted(AuthTG.config.getString("messages.telegram.loginaccept").replace("{PLAYER}", user.playername));
                MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.joininaccounttext")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.joininaccounttext")));
                player.sendTitle(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.joininaccounts1")), AuthTG.config.getString("messages.minecraft.joininaccounts2"), 0,1000000000,0);
            } else {
                MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.authtgactivetext")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.authtgactivetext")));
                player.sendTitle(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.authtgactives1")), AuthTG.config.getString("messages.minecraft.authtgactives2"), 0,1000000000,0);
            }
        } else {
            if (user.activetg && user.twofactor) {
                user.sendLoginAccepted(AuthTG.config.getString("messages.telegram.loginaccept").replace("{PLAYER}", user.playername));
                MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.joininaccounttext")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.joininaccounttext")));
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.joininaccounts1")), AuthTG.config.getString("messages.minecraft.joininaccounts2"), 0, 1000000000, 0);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.loginsuccess")));
                FreezerEvent.unfreezeplayer(player.getName());
                MuterEvent.unmute(player.getName());
                player.resetTitle();
            }
        }
        return true;
    }
}
