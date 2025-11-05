package org.ezhik.authTG.commandMC;

import jdk.javadoc.doclet.Taglet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.util.logging.Level;

public class SetSpawnCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO,AuthTG.getMessage("notplayer", "CE"));
            return false;
        }
        if (!commandSender.hasPermission("authtg.setspawn")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("setspawnnoperm", "MC")));
            return false;
        }
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            AuthTG.spawn = player.getLocation();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("setspawnsuccess", "MC")));
            return true;
        }
        if (strings[0].equals("none")) {
            AuthTG.spawn = null;
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("setspawnnone", "MC")));
            return true;
        }
        return true;
    }
}
