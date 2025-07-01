package org.ezhik.authTG.configuration;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GlobalConfig {
    public boolean authNecessarily = false;
    public boolean notRegAndLogin = false;
    public int minLenghtNickname = 3;
    public int maxLenghtNickname = 15;
    public int minLenghtPassword = 3;
    public int maxLenghtPassword = 32;

    public GlobalConfig() {
        File file = new File("plugins/AuthTG/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            config.set("authNecessarily", authNecessarily);
            config.set("notRegAndLogin", notRegAndLogin);
            config.set("minLenghtNickname", minLenghtNickname);
            config.set("maxLenghtNickname", maxLenghtNickname);
            config.set("minLenghtPassword", minLenghtPassword);
            config.set("maxLenghtPassword", maxLenghtPassword);
            try {
                config.save(file);
            } catch (IOException e) {
                System.out.println("Error saving config.yml");
            }
        } else {
            authNecessarily = config.getBoolean("authNecessarily");
            notRegAndLogin = config.getBoolean("notRegAndLogin");
            minLenghtNickname = config.getInt("minLenghtNickname");
            maxLenghtNickname = config.getInt("maxLenghtNickname");
            minLenghtPassword = config.getInt("minLenghtPassword");
            maxLenghtPassword = config.getInt("maxLenghtPassword");
        }
    }

    public void setSpawnLocation(Location loc) {
        File file = new File("plugins/AuthTG/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("spawnLocation", loc);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config.yml");
        }
    }

    public Location getSpawnLocation() {
        File file = new File("plugins/AuthTG/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.get("spawnLocation") != null) {
            return (Location) config.get("spawnLocation");
        } else {
            return null;
        }
    }
}
