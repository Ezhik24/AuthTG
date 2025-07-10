package org.ezhik.authTG.usersconfiguration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authTG.PasswordHasher;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class YAMLLoader implements Loader{
    public Map<Long,UUID> currentuser = new HashMap<>();
    public Map<Long,List<UUID>> playernames = new HashMap<>();
    public Map<String,UUID> uuidbyplayername = new HashMap<>();
    public YAMLLoader() {
        File[] folder = new File("plugins/AuthTG/users/").listFiles();
        if (folder != null) {
            for (File file : folder) {
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(file);
                    if (config.contains("playername") && config.getBoolean("active")) {
                        uuidbyplayername.put(config.getString("playername"),UUID.fromString(file.getName().replace(".yml", "")));
                    }
                    if (config.contains("chatid") && config.getBoolean("activetg")) {
                        if (playernames.containsKey(config.getLong("chatid"))) {
                            playernames.get(config.getLong("chatid")).add(UUID.fromString(file.getName().replace(".yml", "")));
                        } else {
                            List<UUID> list = new ArrayList<>();
                            list.add(UUID.fromString(file.getName().replace(".yml", "")));
                            playernames.put(config.getLong("chatid"), list);
                        }
                        this.currentuser.put(config.getLong("chatid"), UUID.fromString(file.getName().replace(".yml", "")));
                    }
                } catch (IOException e) {
                    System.out.println("Error load file: " + e);
                } catch (InvalidConfigurationException e) {
                    System.out.println("Error load file: " + e);
                }
            }
        }
    }

    @Override
    public void setPlayerName(UUID uuid, String playername) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration playerconf = new YamlConfiguration();
        try {
            playerconf.load(file);
        } catch (FileNotFoundException e) {
            playerconf.set("active", false);
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        playerconf.set("playername", playername);
        this.uuidbyplayername.put(playername, uuid);
        try {
            playerconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public void setPasswordHash(UUID uuid, String password) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration playerconf = new YamlConfiguration();
        try {
            playerconf.load(file);
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        playerconf.set("password", PasswordHasher.hashPassword(password));
        try {
            playerconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " +e);
        }

    }

    @Override
    public void setActive(UUID uuid, boolean active) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration playerconf = new YamlConfiguration();
        try {
            playerconf.load(file);
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        playerconf.set("active", active);
        try {
            playerconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public boolean isActive(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getBoolean("active");
    }

    @Override
    public boolean passwordValid(UUID uuid, String password) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getString("password").equals(PasswordHasher.hashPassword(password));
    }

    @Override
    public String getPlayerName(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getString("playername");
    }

    @Override
    public boolean getTwofactor(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }  catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getBoolean("twofactor");
    }

    @Override
    public boolean getActiveTG(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }  catch (FileNotFoundException e) {
            return false;
        }catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getBoolean("activetg");
    }

    @Override
    public List<String> getListFriends(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }  catch (FileNotFoundException e) {
            return null;
        }catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return (List<String>) config.getList("friends");
    }

    @Override
    public String getUserName(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }  catch (FileNotFoundException e) {
            return null;
        }catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getString("username");
    }

    @Override
    public String getFirstName(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }  catch (FileNotFoundException e) {
            return null;
        }catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getString("firstname");
    }

    @Override
    public String getLastName(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }  catch (FileNotFoundException e) {
            return null;
        }catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        return config.getString("lastname");
    }

    @Override
    public Long getChatID(UUID uuid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException e) {
            return -1L;
        } catch (IOException e) {
            System.out.println("Error load file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error load file: " + e);
        }
        if (config.contains("chatid")) {
            return config.getLong("chatid");
        } else {
            return -1L;
        }
    }

    @Override
    public UUID getCurrentUUID(Long chatid) {
        if (currentuser.containsKey(chatid)) {
            return currentuser.get(chatid);
        }
        return null;
    }

    @Override
    public void setCurrentUUID(UUID uuid, Long chatid) {
        this.currentuser.put(chatid, uuid);
    }

    @Override
    public void setChatID(UUID uuid, Long chatid) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        config.set("chatid", chatid);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public void setUsername(UUID uuid, String username) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        config.set("username", username);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public void setLastName(UUID uuid, String lastname) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        config.set("lastname", lastname);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public void setFirstName(UUID uuid, String firstname) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        config.set("firstname", firstname);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public void setTwofactor(UUID uuid, boolean state) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        config.set("twofactor", state);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public void setActiveTG(UUID uuid, boolean state) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        if (!state) {
            for (Long chatid : currentuser.keySet()) {
                if (currentuser.get(chatid).equals(uuid)) {
                    currentuser.remove(chatid);
                }
            }
            for (Long chatid : playernames.keySet()) {
                if (playernames.get(chatid).contains(uuid)) {
                    playernames.get(chatid).remove(uuid);
                }
            }
        }
        config.set("activetg", state);
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public List<UUID> getPlayerNames(Long chatid) {
        if (playernames.containsKey(chatid)) {
            return playernames.get(chatid);
        }
        return null;
    }

    @Override
    public void setPlayerNames(Long chatid, UUID uuid) {
        if (playernames.containsKey(chatid)) {
            playernames.get(chatid).add(uuid);
        }
    }

    @Override
    public Set<Long> getChatID() {
        return this.currentuser.keySet();
    }

    @Override
    public void addFriend(UUID uuid, String friend) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        if (config.contains("friends")) {
            List<String > list = (List<String>) config.getList("friends");
            list.add(friend);
            config.set("friends", list);
        } else {
            List<String> list = new ArrayList<>();
            list.add(friend);
            config.set("friends", list);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    @Override
    public UUID getUUIDbyPlayerName(String playername) {
        if (uuidbyplayername.containsKey(playername)) {
            return uuidbyplayername.get(playername);
        }
        return null;
    }

    @Override
    public void removeFriend(UUID uuid, String friend) {
        File file = new File("plugins/AuthTG/users/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            System.out.println("Error loading file " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading file " + e);
        }
        List<String> list = (List<String>) config.getList("friends");
        list.remove(friend);
        if (list.isEmpty()) {
            config.set("friends", null);
        } else {
            config.set("friends", list);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }
}