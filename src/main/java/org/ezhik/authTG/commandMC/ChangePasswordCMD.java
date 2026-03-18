package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

import java.util.logging.Level;

public class ChangePasswordCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO,AuthTG.getMessage("notplayer", "CE"));
            return false;
        }
        Player player = (Player) commandSender;
        if (!(strings.length == 3)) {
            MessageHelper.send(player, AuthTG.getMessage("cpusage", "MC"));
            return false;
        }
        if (!AuthTG.loader.passwordValid(player.getUniqueId(), strings[0])) {
            MessageHelper.send(player, AuthTG.getMessage("cpoldpassnotvalid", "MC"));
            return false;
        }
        if (!strings[1].equals(strings[2])) {
            MessageHelper.send(player, AuthTG.getMessage("cpnotmatch", "MC"));
            return false;
        }
        AuthTG.loader.setPasswordHash(player.getUniqueId(), strings[1]);
        MessageHelper.send(player, AuthTG.getMessage("cpsuccess", "MC"));
        return true;
    }
}
