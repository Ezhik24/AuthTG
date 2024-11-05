package org.ezhik.authtgem.message;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MessageTranslationMC extends LinkedHashMap<String, String> {

    public  MessageTranslationMC() {
        File configfile = new File("plugins/Minetelegram/messages/messageMC_RU.yml");
        YamlConfiguration messageconfig = new YamlConfiguration();
        try {
            messageconfig.load(configfile);
        } catch (FileNotFoundException e) {
            this.put("prefix", "&f&l[&b&lMT&f&l]");
            this.put("addfriends_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду так: /addfriend <ник>");
            this.put("addfriends_tg_noasign", "{PREFIX} &c&lПривяжите аккаунт к телеграмму");
            this.put("friend_tg_noasign", "{PREFIX} &c&lДанный игрок не привязывал аккаунт к телеграмму");
            this.put("friends_already_added", "{PREFIX} &c&lВы уже добавляли этого игрока в друзья");
            this.put("removefriend_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду так: /remfriend <ник>");
            this.put("removefriend_tg_noasign","{PREFIX} &c&lПривяжите аккаунт к телеграмму");
            this.put("changepassword_success", "{PREFIX} &a&lВы успешно изменили пароль");
            this.put("changepassword_oldpasswd_wrong", "{PREFIX} &c&lНеверный старый пароль");
            this.put("changepassword_newpasswd_wrong", "{PREFIX} &c&lПароли не совпадают");
            this.put("changepassword_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду  так: /changepassword <старый пароль> <новый пароль>");
            this.put("code_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду так: /code <код>");
            this.put("code_account_deactivated", "{PREFIX} &c&lАккаунт был успешно деактивирован");
            this.put("code_account_activated", "{PREFIX} &a&lАккаунт был успешно активирован");
            this.put("code_invalid", "{PREFIX} &c&lНеверный код. Попробуйте еще раз.");
            this.put("listfriends_list", "{PREFIX} &c&lСписок друзей:");
            this.put("listfriends_friend", "&a&l");
            this.put("login_successful_login", "{PREFIX} &a&lВы успешно вошли в игру");
            this.put("login_who_entered", "{PREFIX} &a&lЭто вы вошли в игру?");
            this.put("login_wrong_password", "{PREFIX} &c&lНеверный пароль");
            this.put("login_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду так: /login <пароль>");
            this.put("login_title_tg_s1color", "&c&l");
            this.put("login_title_tg_s1","Потвердите вход");
            this.put("login_title_tg_s2","через Телеграмм");
            this.put("register_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду так: /register <пароль> <повтор пароля>");
            this.put("register_successful_register", "{PREFIX} &a&lВы успешно зарегистрировали аккаунт");
            this.put("register_wrong_passwords", "{PREFIX} &c&lПароли не совпадают");
            this.put("register_already_register", "{PREFIX} &c&lВы уже зарегистрированы.Для сброса пароля обратитесь к Администратору");
            this.put("setpassword_wrong_command", "{PREFIX} &c&lКоманда введена неверно. Введите команду так: /setpassword <ник> <старый пароль> <новый пароль>");
            this.put("setpassword_nopermission", "{PREFIX} &c&lУ вас нет прав для использования этой команды");
            this.put("setpassword_wrong_password","{PREFIX} &c&lПароли не совпадают");
            this.put("setpassword_succesfly_chanpass","&f&l[&b&lMT&f&l] &a&lВы успешно изменили пароль игроку {PLAYER}");
            File newconfigfile = new File("plugins/Minetelegram/messages/messageMC_RU.yml");
            YamlConfiguration newmessageconfig = new YamlConfiguration();
            for (String key : this.keySet()) {
                newmessageconfig.set(key, this.get(key));
                this.replace(key, this.get(key).replace("{BR}", "\n").replace("{PREFIX}",  ""));
            };
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
            this.put(key, messageconfig.getString(key).replace("{BR}", "\n").replace("{PREFIX}",""));
        }
    }
    public String getSetpasswordPlayerName(String[] strings) {
        Player player = Bukkit.getPlayer(strings[0]);
        return this.get("setpassword_succesfly_chanpass").replace("{PLAYER}",player.getName());
    }
}
