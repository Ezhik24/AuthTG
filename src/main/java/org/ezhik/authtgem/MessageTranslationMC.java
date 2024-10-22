package org.ezhik.authtgem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class MessageTranslationMC extends HashMap<String, String> {

    public  MessageTranslationMC() {
        File configfile = new File("plugins/Minetelegram/messages/messageMC_RU.yml");
        YamlConfiguration messageconfig = new YamlConfiguration();
        try {
            messageconfig.load(configfile);
        } catch (FileNotFoundException e) {
            this.put("addfriends_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /addfriend <ник>");
            this.put("addfriends_tg_noasign", "&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к телеграмму");
            this.put("friend_tg_noasign", "&f&l[&b&lMT&f&l] &c&lДанный игрок не привязывал аккаунт к телеграмму");
            this.put("friends_already_added", "&f&l[&b&lMT&f&l] &c&lВы уже добавляли этого игрока в друзья");
            this.put("changepassword_success", "&f&l[&b&lMT&f&l] &a&lВы успешно изменили пароль");
            this.put("changepassword_oldpasswd_wrong", "&f&l[&b&lMT&f&l] &c&lНеверный старый пароль");
            this.put("changepassword_newpasswd_wrong", "&f&l[&b&lMT&f&l] &c&lПароли не совпадают");
            this.put("changepassword_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /changepassword <старый пароль> <новый пароль>");
            this.put("code_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /code <код>");
            File newconfigfile = new File("plugins/Minetelegram/messages/messageMC_RU.yml");
            YamlConfiguration newmessageconfig = new YamlConfiguration();
            for (String key : this.keySet()) {
                newmessageconfig.set(key, this.get(key));
                this.replace(key, this.get(key).replace("{BR}", "\n"));
            }
            try {
                newmessageconfig.save(newconfigfile);
            } catch (IOException ex) {
                System.out.println("Error saving config file: " + ex);
            }
        }
        catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        for (String key : messageconfig.getKeys(false)) {
            this.put(key, messageconfig.getString(key).replace("{BR}", "\n"));
        }
    }
}
