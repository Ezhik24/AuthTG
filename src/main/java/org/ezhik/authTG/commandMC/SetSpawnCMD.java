package org.ezhik.authTG.commandMC;

import jdk.javadoc.doclet.Taglet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.io.File;
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
            AuthTG.locationX = player.getLocation().getX();
            AuthTG.locationY = player.getLocation().getY();
            AuthTG.locationZ = player.getLocation().getZ();
            AuthTG.world = player.getLocation().getWorld().getName();
            File file = new File("plugins/AuthTG/config.yml");
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.set("spawn.x", AuthTG.locationX);
            yamlConfiguration.set("spawn.y", AuthTG.locationY);
            yamlConfiguration.set("spawn.z", AuthTG.locationZ);
            yamlConfiguration.set("spawn.world", AuthTG.world);
            try {
                yamlConfiguration.save(file);
            } catch (Exception e) {
                AuthTG.logger.log(Level.SEVERE, "Cannot save config.yml");
            }
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("setspawnsuccess", "MC")));
            return true;
        }
        if (strings[0].equals("none")) {
            AuthTG.locationX = 0;
            AuthTG.locationY = 0;
            AuthTG.locationZ = 0;
            AuthTG.world = "none";
            File file = new File("plugins/AuthTG/config.yml");
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.set("spawn.x", AuthTG.locationX);
            yamlConfiguration.set("spawn.y", AuthTG.locationY);
            yamlConfiguration.set("spawn.z", AuthTG.locationZ);
            yamlConfiguration.set("spawn.world", AuthTG.world);
            try {
                yamlConfiguration.save(file);
            } catch (Exception e) {
                AuthTG.logger.log(Level.SEVERE, "Cannot save config.yml");
            }
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("setspawnnone", "MC")));
            return true;
        }
        return true;
    }
}
