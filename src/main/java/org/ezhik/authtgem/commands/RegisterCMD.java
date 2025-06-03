package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.PasswordHasher;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;

import java.io.File;
import java.io.IOException;

public class RegisterCMD implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (AuthTGEM.bot.notRegAndLogin) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_cancel")));
            return false;
        }
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_wrong_command")));
            return false;
        }
        Player player = (Player) commandSender;
        File file = new File("plugins/Minetelegram/users/" + player.getUniqueId() + ".yml");
        YamlConfiguration userconfig = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_already_register")));
            return false;
        }
        if (strings[0].length() < AuthTGEM.bot.minLenghtPassword || strings[0].length() > AuthTGEM.bot.maxLenghtPassword) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("register_LenghtPass")));
            return false;
        }
        if (!strings[0].equals(strings[1])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_wrong_passwords")));
            return false;
        }
        userconfig.set("password", PasswordHasher.hashPassword(strings[0]));
        userconfig.set("playername", player.getName());
        try {
            userconfig.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }
        if (AuthTGEM.bot.authNecessarily) {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("account_auth_nessery1")), AuthTGEM.messageMC.get("account_auth_nessery2"), 0,10000000,0);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("register_successful_register")));
            player.resetTitle();
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
        }
        return true;
    }
}
