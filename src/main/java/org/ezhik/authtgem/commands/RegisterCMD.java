package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.BotTelegram;
import org.ezhik.authtgem.PasswordHasher;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class RegisterCMD implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_wrong_command")));
            return false;
        }
        Player player = (Player) commandSender;
        YamlConfiguration userconfig = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + player.getUniqueId() + ".yml");
        if (!file.exists()) {
            if (strings[0].equals(strings[1])) {
                try {
                    userconfig.load(file);
                } catch (IOException e) {
                    System.out.println("Error loading config file: " + e);
                } catch (InvalidConfigurationException e) {
                    System.out.println("Error parsing config file: " + e);
                }
                userconfig.set("password", PasswordHasher.hashPassword(strings[0]));
                userconfig.set("playername", player.getName());
                try {
                    userconfig.save(file);
                } catch (IOException e) {
                    System.out.println("Error saving config file: " + e);
                }
                if (AuthTGEM.bot.authNecessarily) {
                    player.sendTitle("Привяжи аккаунт", "/start в боте", 0,10000000,0);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_successful_register")));
                    player.resetTitle();
                    FreezerEvent.unfreezeplayer(player.getName());
                    MuterEvent.unmute(player.getName());
                }
            } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_wrong_passwords")));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_already_register")));
        }
        return true;
    }
}
