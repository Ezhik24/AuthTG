package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.PasswordHasher;
import org.ezhik.authtgem.User;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;

import java.io.File;
import java.io.IOException;

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_wrong_command")));
            return false;
        }
        Player p = (Player) commandSender;
        File file = new File("plugins/Minetelegram/users/" + p.getUniqueId() + ".yml");
        YamlConfiguration userconfig = YamlConfiguration.loadConfiguration(file);
        if (!userconfig.getString("password").equals(PasswordHasher.hashPassword(strings[0]))) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_wrong_password")));
            return false;
        }
        User user = User.getUser(p.getUniqueId());
        if (AuthTGEM.bot.authNecessarily) {
            if (userconfig.getBoolean("active")) {
                user.sendLoginAccepted(AuthTGEM.messageTG.get("login_who_entered"));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_who_entered")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_title_tg_s1color")) + AuthTGEM.messageMC.get("login_title_tg_s1"), AuthTGEM.messageMC.get("login_title_tg_s2"), 20, 1000000000, 0);
            } else {
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("account_auth_nessery1")), AuthTGEM.messageMC.get("account_auth_nessery2"), 0,10000000,0);
            }
        }else {
            if (user != null) {
                user.sendLoginAccepted(AuthTGEM.messageTG.get("login_who_entered"));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_who_entered")));
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_title_tg_s1color")) + AuthTGEM.messageMC.get("login_title_tg_s1"), AuthTGEM.messageMC.get("login_title_tg_s2"), 20, 1000000000, 0);
            } else {
                userconfig.set("twofactor", false);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_successful_login")));
                FreezerEvent.unfreezeplayer(p.getName());
                MuterEvent.unmute(p.getName());
                p.resetTitle();
            }
        }
        userconfig.set("playername", p.getName());
        try {
            userconfig.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }
        return true;
    }
}