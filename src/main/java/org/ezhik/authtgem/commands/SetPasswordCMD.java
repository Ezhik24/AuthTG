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

public class SetPasswordCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
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
                player.sendMessage(ChatColor.GREEN + "[MT] Вы успешно изменили пароль игроку" + player.getName());
            }
        } else player.sendMessage(ChatColor.RED + "[MT] Неверена введена команда. Введите команду так: /setpassword <пароль> <повторите пароль>");
        return true;
    }
}
