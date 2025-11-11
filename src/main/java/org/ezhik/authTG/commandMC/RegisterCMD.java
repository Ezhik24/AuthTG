package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;

import java.util.logging.Level;

public class RegisterCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO,AuthTG.getMessage("notplayer", "CE"));
            return false;
        }
        if (AuthTG.notRegAndLogin) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("registeroff", "MC")));
            return false;
        }
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("registerusage", "MC")));
            return false;
        }
        Player player = (Player) commandSender;
        if (AuthTG.loader.isActive(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("alreadyreg", "MC")));
            return false;
        }
        if (strings[0].length() < AuthTG.minLenghtPassword || strings[0].length() > AuthTG.maxLenghtPassword) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("registerlenght", "MC").replace("{MIN}", String.valueOf(AuthTG.minLenghtPassword)).replace("{MAX}", String.valueOf(AuthTG.maxLenghtPassword))));
            return false;
        }
        if (!strings[0].equals(strings[1])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("registernomatch", "MC")));
            return false;
        }
        AuthTG.loader.setPlayerName(player.getUniqueId(), player.getName());
        AuthTG.loader.setPasswordHash(player.getUniqueId(),strings[0]);
        AuthTG.loader.setActive(player.getUniqueId(), true);
        if (AuthTG.authNecessarily) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgactivetext", "MC")));
            MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgactivetext", "MC")));
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("authtgactives1", "MC")), AuthTG.getMessage("authtgactives2", "MC"), 0,1000000000,0);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("registersuccess", "MC")));
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
            AuthHandler.removeTimeout(player.getUniqueId());
        }
        return true;
    }
}
