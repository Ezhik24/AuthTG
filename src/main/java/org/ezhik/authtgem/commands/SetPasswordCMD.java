package org.ezhik.authtgem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.PasswordHasher;

import java.io.File;
import java.io.IOException;

public class SetPasswordCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("minetelegram.setpassword")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("setpassword_nopermission")));
            return false;
        }
        Player player = Bukkit.getPlayer(strings[0]);
        File file = new File("plugins/Minetelegram/users/" + player.getUniqueId() + ".yml");
        YamlConfiguration userconfig = YamlConfiguration.loadConfiguration(file);
        if (!(strings.length == 3)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("setpassword_wrong_command")));
            return false;
        }
        if (!strings[1].equals(strings[2])) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("setpassword_wrong_password")));
            return false;
        }
        userconfig.set("password", PasswordHasher.hashPassword(strings[1]));
        try {
            userconfig.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.getSetpasswordPlayerName(strings)));
        return true;
    }
}
