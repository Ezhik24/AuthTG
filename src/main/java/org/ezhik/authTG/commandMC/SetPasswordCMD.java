package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

public class SetPasswordCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
        if (strings.length != 3) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setpasswordusage")));
            return false;
        }
        if (!commandSender.hasPermission("authtg.setpassword")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setpasswordnoperm")));
            return false;
        }
        Player player = Bukkit.getPlayer(strings[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setpassplnotfound")));
            return false;
        }
        if (!strings[1].equals(strings[2])) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setpassnomatch")));
            return false;
        }
        AuthTG.loader.setPasswordHash(player.getUniqueId(), strings[1]);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.setpasssuccess").replace("{PLAYER}", player.getName())));
        return true;
    }
}
