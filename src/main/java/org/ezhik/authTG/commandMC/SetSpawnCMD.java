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
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cУ вас недостаточно прав!"));
            return false;
        }
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            AuthTG.config.set("spawnLocation",  player.getLocation());
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lВы установили спавн!"));
            return true;
        }
        if (strings[0].equals("none")) {
            AuthTG.config.set("spawnLocation",  null);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lВы отключили спавн!"));
            return true;
        }
        return true;
    }
}
