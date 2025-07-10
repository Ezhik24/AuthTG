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

public class RegisterCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
        if (AuthTG.config.getBoolean("notRegAndLogin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.registeroff")));
            return false;
        }
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.registerusage")));
            return false;
        }
        Player player = (Player) commandSender;
        if (AuthTG.loader.isActive(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.alreadyreg")));
            return false;
        }
        if (strings[0].length() < AuthTG.config.getInt("minLenghtPassword") || strings[0].length() > AuthTG.config.getInt("maxLenghtPassword")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.registerlenght").replace("{MIN}", String.valueOf(AuthTG.config.getInt("minLenghtPassword"))).replace("{MAX}", String.valueOf(AuthTG.config.getInt("maxLenghtPassword")))));
            return false;
        }
        if (!strings[0].equals(strings[1])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.registernomatch")));
            return false;
        }
        AuthTG.loader.setPlayerName(player.getUniqueId(), player.getName());
        AuthTG.loader.setPasswordHash(player.getUniqueId(),strings[0]);
        AuthTG.loader.setActive(player.getUniqueId(), true);
        if (AuthTG.config.getBoolean("authNecessarily")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.authtgactivetext")));
            MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.authtgactivetext")));
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.minecraft.authtgactives1")), AuthTG.config.getString("messages.minecraft.authtgactives2"), 0,1000000000,0);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.registersuccess")));
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
        }
        return true;
    }
}
