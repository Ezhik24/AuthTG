package org.ezhik.authtgem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.PasswordHasher;

import java.io.File;
import java.io.IOException;

public class SetPasswordCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("minetelegram.setpassword")) {
             Player player = Bukkit.getPlayer(strings[0]);
            YamlConfiguration userconfig = new YamlConfiguration();
            File file = new File("plugins/Minetelegram/users/" + player.getUniqueId() + ".yml");
            if (strings.length == 3) {
                if (strings[1].equals(strings[2])) {
                    try {
                        userconfig.load(file);
                    } catch (IOException e) {
                        System.out.println("Error loading config file: " + e);
                    } catch (InvalidConfigurationException e) {
                        System.out.println("Error parsing config file: " + e);
                    }
                    userconfig.set("password", PasswordHasher.hashPassword(strings[1]));
                    try {
                        userconfig.save(file);
                    } catch (IOException e) {
                        System.out.println("Error saving config file: " + e);
                    }

                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.getSetpasswordPlayerName(strings)));
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("setpassword_wrong_password")));
                }
            } else
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("setpassword_wrong_command")));
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("setpassword_nopermission")));
        }
        return true;

    }
}
