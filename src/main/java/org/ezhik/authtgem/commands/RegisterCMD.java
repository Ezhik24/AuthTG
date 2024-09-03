package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.PasswordHasher;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;

import java.io.File;
import java.io.IOException;

public class RegisterCMD implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /register <пароль> <повтор пароля>"));
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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &a&lВы успешно зарегистрировали аккаунт"));
                player.resetTitle();


                FreezerEvent.unfreezeplayer(player.getName());
                MuterEvent.unmute(player.getName());
            } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lПароли не совпадают"));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lВы уже зарегистрированы.Для сброса пароля обратитесь к Администратору"));
        }
        return true;
    }
}
