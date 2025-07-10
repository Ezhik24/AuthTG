package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

public class SetSpawnCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
        if (!commandSender.hasPermission("authtg.setspawn")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setspawnnoperm")));
            return false;
        }
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            AuthTG.config.set("spawnLocation",  player.getLocation());
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setspawnsuccess")));
            return true;
        }
        if (strings[0].equals("none")) {
            AuthTG.config.set("spawnLocation",  null);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setspawnnone")));
            return true;
        }
        return true;
    }
}
