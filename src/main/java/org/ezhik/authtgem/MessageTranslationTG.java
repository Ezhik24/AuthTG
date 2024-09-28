package org.ezhik.authtgem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageTranslationTG extends HashMap<String, String>{

    public  MessageTranslationTG(){
        File configfile = new File("plugins/Minetelegram/messages/messageTG_RU.yml");
        YamlConfiguration messageconfig = new YamlConfiguration();
        try {
            messageconfig.load(configfile);

        } catch (FileNotFoundException e) {
            this.put("assign_acc","[Бот] Привяжите телеграм к аккаунту");

            File newconfigfile = new File("plugins/Minetelegram/messages/messageTG_RU.yml");
            YamlConfiguration newmessageconfig = new YamlConfiguration();
            for(String key : this.keySet()){
                newmessageconfig.set(key, this.get(key));
            }
            try {
                newmessageconfig.save(newconfigfile);
            } catch (IOException ex) {
                System.out.println("Error saving config file: " + ex);
            }
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
    }
}
