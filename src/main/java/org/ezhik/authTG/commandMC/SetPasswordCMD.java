package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SetPasswordCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (strings.length != 3) {
            MessageHelper.send(commandSender, AuthTG.getMessage("setpasswordusage", "MC"));
            return false;
        }
        if (!commandSender.hasPermission("authtg.setpassword")) {
            MessageHelper.send(commandSender, AuthTG.getMessage("setpasswordnoperm", "MC"));
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);
        if (!strings[1].equals(strings[2])) {
            MessageHelper.send(commandSender, AuthTG.getMessage("setpassnotmatch", "MC"));
            return false;
        }
        AuthTG.loader.setPasswordHash(player.getUniqueId(), strings[1]);
        MessageHelper.send(commandSender, AuthTG.getMessage("setpasssuccess", "MC").replace("{PLAYER}", player.getName()));
        return true;
    }
}
