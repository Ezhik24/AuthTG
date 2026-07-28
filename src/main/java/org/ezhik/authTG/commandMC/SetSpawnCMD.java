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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (!sender.hasPermission("authtg.setspawn")) {
            MessageHelper.send(sender, AuthTG.getMessage("setspawnnoperm", "MC"));
            return false;
        }

        File configFile = new File("plugins/AuthTG/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (args.length == 0) {
            setMainSpawn(player, config, configFile);
            MessageHelper.send(sender, AuthTG.getMessage("setspawnsuccess", "MC"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "none":
                clearMainSpawn(config, configFile);
                MessageHelper.send(sender, AuthTG.getMessage("setspawnnone", "MC"));
                return true;

            case "velocity":
                if (!AuthTG.velocity) {
                    MessageHelper.send(sender, AuthTG.getMessage("velocitydisabled", "MC"));
                    return true;
                }
                handleVelocitySubcommand(sender, player, args, config, configFile);
                return true;

            default:
                MessageHelper.send(sender, AuthTG.getMessage("setspawnusage", "MC"));
                return true;
        }
    }

    private void setMainSpawn(Player player, YamlConfiguration config, File configFile) {
        Location loc = player.getLocation();
        AuthTG.locationX = loc.getX();
        AuthTG.locationY = loc.getY();
        AuthTG.locationZ = loc.getZ();
        AuthTG.locationYaw = loc.getYaw();
        AuthTG.locationPitch = loc.getPitch();
        AuthTG.world = loc.getWorld().getName();

        saveSpawnGroup(config, configFile, "spawn",
                AuthTG.locationX, AuthTG.locationY, AuthTG.locationZ,
                AuthTG.locationYaw, AuthTG.locationPitch, AuthTG.world);
    }

    private void clearMainSpawn(YamlConfiguration config, File configFile) {
        AuthTG.locationX = 0;
        AuthTG.locationY = 0;
        AuthTG.locationZ = 0;
        AuthTG.locationYaw = 0;
        AuthTG.locationPitch = 0;
        AuthTG.world = "none";

        saveSpawnGroup(config, configFile, "spawn",
                AuthTG.locationX, AuthTG.locationY, AuthTG.locationZ,
                AuthTG.locationYaw, AuthTG.locationPitch, AuthTG.world);
    }

    private void handleVelocitySubcommand(CommandSender sender, Player player, String[] args,
                                          YamlConfiguration config, File configFile) {
        if (args.length < 2) {
            MessageHelper.send(sender, AuthTG.getMessage("setspawnvelocityerror", "MC"));
            return;
        }

        String target = args[1].toLowerCase();
        boolean isNone = args.length == 3 && "none".equalsIgnoreCase(args[2]);

        switch (target) {
            case "aftereauthorization":
                if (isNone) {
                    clearVelocityAfterAuth(config, configFile);
                    MessageHelper.send(sender, AuthTG.getMessage("setspawnvelocitynone", "MC"));
                } else {
                    setVelocityAfterAuth(player, config, configFile);
                    MessageHelper.send(sender, AuthTG.getMessage("setspawnvelocitysuccess", "MC"));
                }
                break;

            case "authorization":
                if (isNone) {
                    clearVelocityAuth(config, configFile);
                    MessageHelper.send(sender, AuthTG.getMessage("setspawnvelocitynone", "MC"));
                } else {
                    setVelocityAuth(player, config, configFile);
                    MessageHelper.send(sender, AuthTG.getMessage("setspawnvelocitysuccess", "MC"));
                }
                break;

            default:
                MessageHelper.send(sender, AuthTG.getMessage("setspawnvelocityerror", "MC"));
        }
    }

    private void setVelocityAfterAuth(Player player, YamlConfiguration config, File configFile) {
        Location loc = player.getLocation();
        AuthTG.velocityAfterAuthorizationX = loc.getX();
        AuthTG.velocityAfterAuthorizationY = loc.getY();
        AuthTG.velocityAfterAuthorizationZ = loc.getZ();
        AuthTG.velocityAfterAuthorizationYaw = loc.getYaw();
        AuthTG.velocityAfterAuthorizationPitch = loc.getPitch();
        AuthTG.velocityAfterAuthorizationWorld = loc.getWorld().getName();

        saveSpawnGroup(config, configFile, "velocity.after-authorization",
                AuthTG.velocityAfterAuthorizationX, AuthTG.velocityAfterAuthorizationY,
                AuthTG.velocityAfterAuthorizationZ, AuthTG.velocityAfterAuthorizationYaw,
                AuthTG.velocityAfterAuthorizationPitch, AuthTG.velocityAfterAuthorizationWorld);
    }

    private void clearVelocityAfterAuth(YamlConfiguration config, File configFile) {
        AuthTG.velocityAfterAuthorizationX = 0;
        AuthTG.velocityAfterAuthorizationY = 0;
        AuthTG.velocityAfterAuthorizationZ = 0;
        AuthTG.velocityAfterAuthorizationYaw = 0;
        AuthTG.velocityAfterAuthorizationPitch = 0;
        AuthTG.velocityAfterAuthorizationWorld = "none";

        saveSpawnGroup(config, configFile, "velocity.after-authorization",
                AuthTG.velocityAfterAuthorizationX, AuthTG.velocityAfterAuthorizationY,
                AuthTG.velocityAfterAuthorizationZ, AuthTG.velocityAfterAuthorizationYaw,
                AuthTG.velocityAfterAuthorizationPitch, AuthTG.velocityAfterAuthorizationWorld);
    }

    private void setVelocityAuth(Player player, YamlConfiguration config, File configFile) {
        Location loc = player.getLocation();
        AuthTG.velocityAuthorizationX = loc.getX();
        AuthTG.velocityAuthorizationY = loc.getY();
        AuthTG.velocityAuthorizationZ = loc.getZ();
        AuthTG.velocityAuthorizationYaw = loc.getYaw();
        AuthTG.velocityAuthorizationPitch = loc.getPitch();
        AuthTG.velocityAuthorizationWorld = loc.getWorld().getName();

        saveSpawnGroup(config, configFile, "velocity.authorization",
                AuthTG.velocityAuthorizationX, AuthTG.velocityAuthorizationY,
                AuthTG.velocityAuthorizationZ, AuthTG.velocityAuthorizationYaw,
                AuthTG.velocityAuthorizationPitch, AuthTG.velocityAuthorizationWorld);
    }

    private void clearVelocityAuth(YamlConfiguration config, File configFile) {
        AuthTG.velocityAuthorizationX = 0;
        AuthTG.velocityAuthorizationY = 0;
        AuthTG.velocityAuthorizationZ = 0;
        AuthTG.velocityAuthorizationYaw = 0;
        AuthTG.velocityAuthorizationPitch = 0;
        AuthTG.velocityAuthorizationWorld = "none";

        saveSpawnGroup(config, configFile, "velocity.authorization",
                AuthTG.velocityAuthorizationX, AuthTG.velocityAuthorizationY,
                AuthTG.velocityAuthorizationZ, AuthTG.velocityAuthorizationYaw,
                AuthTG.velocityAuthorizationPitch, AuthTG.velocityAuthorizationWorld);
    }

    private void saveSpawnGroup(YamlConfiguration config, File configFile, String prefix,
                                double x, double y, double z, float yaw, float pitch, String world) {
        config.set(prefix + ".x", x);
        config.set(prefix + ".y", y);
        config.set(prefix + ".z", z);
        config.set(prefix + ".yaw", yaw);
        config.set(prefix + ".pitch", pitch);
        config.set(prefix + ".world", world);

        try {
            config.save(configFile);
        } catch (Exception e) {
            AuthTG.logger.log(Level.SEVERE, "Cannot save config.yml", e);
        }
    }
}
