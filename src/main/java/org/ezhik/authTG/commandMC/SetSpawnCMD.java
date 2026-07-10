package org.ezhik.authTG.commandMC;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

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
            MessageHelper.send(commandSender, AuthTG.getMessage("setspawnnoperm", "MC"));
            return false;
        }
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            Location location = player.getLocation();
            AuthTG.locationX = location.getX();
            AuthTG.locationY = location.getY();
            AuthTG.locationZ = location.getZ();
            AuthTG.locationYaw = location.getYaw();
            AuthTG.locationPitch = location.getPitch();
            AuthTG.world = location.getWorld().getName();
            File file = new File("plugins/AuthTG/config.yml");
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.set("spawn.x", AuthTG.locationX);
            yamlConfiguration.set("spawn.y", AuthTG.locationY);
            yamlConfiguration.set("spawn.z", AuthTG.locationZ);
            yamlConfiguration.set("spawn.yaw", AuthTG.locationYaw);
            yamlConfiguration.set("spawn.pitch", AuthTG.locationPitch);
            yamlConfiguration.set("spawn.world", AuthTG.world);
            try {
                yamlConfiguration.save(file);
            } catch (Exception e) {
                AuthTG.logger.log(Level.SEVERE, "Cannot save config.yml");
            }
            MessageHelper.send(commandSender, AuthTG.getMessage("setspawnsuccess", "MC"));
            return true;
        }
        if (strings[0].equals("none")) {
            AuthTG.locationX = 0;
            AuthTG.locationY = 0;
            AuthTG.locationZ = 0;
            AuthTG.locationYaw = 0;
            AuthTG.locationPitch = 0;
            AuthTG.world = "none";
            File file = new File("plugins/AuthTG/config.yml");
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.set("spawn.x", AuthTG.locationX);
            yamlConfiguration.set("spawn.y", AuthTG.locationY);
            yamlConfiguration.set("spawn.z", AuthTG.locationZ);
            yamlConfiguration.set("spawn.yaw", AuthTG.locationYaw);
            yamlConfiguration.set("spawn.pitch", AuthTG.locationPitch);
            yamlConfiguration.set("spawn.world", AuthTG.world);
            try {
                yamlConfiguration.save(file);
            } catch (Exception e) {
                AuthTG.logger.log(Level.SEVERE, "Cannot save config.yml");
            }
            MessageHelper.send(commandSender, AuthTG.getMessage("setspawnnone", "MC"));
            return true;
        }
        return true;
    }
}
