package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.PasswordHasher;

import java.io.File;
import java.io.IOException;

public class ChangepasswordCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        YamlConfiguration userconfig = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + player.getUniqueId() + ".yml");
        if (strings.length == 3) {
            try {
                userconfig.load(file);
            } catch (IOException e) {
                System.out.println("Error loading config file: " + e);
            } catch (InvalidConfigurationException e) {
                System.out.println("Error parsing config file: " + e);
            }
            if (strings[1].equals(strings[2])) {
                if (userconfig.getString("password").equals(PasswordHasher.hashPassword(strings[0]))) {
                    userconfig.set("password", PasswordHasher.hashPassword(strings[1]));
                    try {
                        userconfig.save(file);
                    } catch (IOException e) {
                        System.out.println("Error saving config file: " + e);
                    }
                    player.sendMessage(ChatColor.GREEN + "[MT] Пароль успешно изменен");
                } else player.sendMessage(ChatColor.RED + "[MT] Неверный старый пароль");
            } else player.sendMessage(ChatColor.RED + "[MT] Пароли не совпадают");
        }else {
            player.sendMessage(ChatColor.RED + "[MT] Неверная команда. Введите команду так: /changepassword <старый пароль> <новый пароль> <повторите пароль>");
        }

        return true;
    }

}
