package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

public class ChangePasswordCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
        Player player = (Player) commandSender;
        if (!(strings.length == 3)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cpusage")));
            return false;
        }
        if (!AuthTG.loader.passwordValid(player.getUniqueId(), strings[0])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cpoldpassnotvalid")));
            return false;
        }
        if (!strings[1].equals(strings[2])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cpnotmatch")));
            return false;
        }
        AuthTG.loader.setPasswordHash(player.getUniqueId(), strings[1]);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.cpsuccess")));
        return true;
    }
}
