package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.PasswordHasher;
import org.ezhik.authtgem.User;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;

import java.io.File;
import java.io.IOException;

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
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
                p.sendMessage(ChatColor.GREEN + "[MT] Вы успешно вошли в аккаунт");
                FreezerEvent.unfreezeplayer(p.getName());
                MuterEvent.unmute(p.getName());
                p.resetTitle();
            }
            else{
                User user = User.getUser(p.getUniqueId());
                user.sendLoginAccepted("[Бот] Это вы вошли в игру?");
                p.sendMessage(ChatColor.GREEN + "[MT] Потвердите вход через Телеграмм");
                p.sendTitle(ChatColor.RED + "Потвердите вход", "через Телеграмм", 20, 1000000000, 0);
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