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
        if (strings.length == 1) {
            Player p = (Player) commandSender;
            YamlConfiguration userconfig = new YamlConfiguration();
            File file = new File("plugins/Minetelegram/users/" + p.getUniqueId() + ".yml");
            try {
                userconfig.load(file);
            } catch (IOException e) {
                System.out.println("Error loading config file: " + e);
            } catch (InvalidConfigurationException e) {
                System.out.println("Error parsing config file: " + e);
            }
            if (userconfig.getString("password").equals(PasswordHasher.hashPassword(strings[0]))) {
                if (!userconfig.contains("twofactor") || !userconfig.getBoolean("twofactor")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_successful_login")));
                    FreezerEvent.unfreezeplayer(p.getName());
                    MuterEvent.unmute(p.getName());
                    p.resetTitle();
                } else {
                    User user = User.getUser(p.getUniqueId());
                    user.sendLoginAccepted(AuthTGEM.messageTG.get("login_who_entered"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_who_entered")));
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_title_tg_s1color")) + AuthTGEM.messageMC.get("login_title_tg_s1"),AuthTGEM.messageMC.get("login_title_tg_s2") , 20, 1000000000, 0);
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_wrong_password")));
            }
            userconfig.set("playername", p.getName());
            try {
                userconfig.save(file);
            } catch (IOException e) {
                System.out.println("Error saving config file: " + e);
            }
        } else {
            Player player = (Player) commandSender;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("login_wrong_command")));

        }
        return true;
    }
}