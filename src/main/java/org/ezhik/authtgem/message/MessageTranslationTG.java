package org.ezhik.authtgem.message;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authtgem.User;

import javax.ws.rs.core.Cookie;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;

public class MessageTranslationTG extends LinkedHashMap<String, String> {

    public  MessageTranslationTG() {
        File configfile = new File("plugins/Minetelegram/messages/messageTG_RU.yml");
        YamlConfiguration messageconfig = new YamlConfiguration();
        try {
            messageconfig.load(configfile);

        } catch (FileNotFoundException e) {
            this.put("sendMessage_prefix", "[Бот@{PLAYER}]");
            this.put("sendMessageB_prefix", "[Бот@{PLAYER}]");
            this.put("start_message", "[Бот] Выполните следующие пункты: {BR} 1.Войдите в игру. {BR} 2.Авторизуйтесь. {BR} 3.Напишите свой никнейм.");
            this.put("tg_noasign_hashtag", "[Бот] Привяжите учётную запись к телеграму.");
            this.put("addfriends_yes", "Да");
            this.put("addfriends_no", "Нет");
            this.put("addfriends_req", "Вы хотите добавить {PLAYER} в друзья?");
            this.put("tellfriends_message_succes", " сообщение от {PLAYER} :");
            this.put("code_account_activated", "Ваш аккаунт успешно активирован!");
            this.put("login_who_entered","[Бот] Это вы вошли в игру?");
            this.put("kick_account_inTG", "Владелец кикнул аккаунт через телеграмм");
            File newconfigfile = new File("plugins/Minetelegram/messages/messageTG_RU.yml");
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
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        for (String key : messageconfig.getKeys(false)) {
            this.put(key, messageconfig.getString(key).replace("{BR}", "\n"));
        }
    }
    public String getAddFriendsReq(CommandSender sender) {
        return this.get("addfriends_req").replace("{PLAYER}", sender.getName());
    }
    public String getFriend(CommandSender commandSender) {
        User user = User.getUser(commandSender.getName());
        return this.get("tellfriends_message_succes").replace("{PLAYER}", user.playername);
    }
    public String getPlayerNameSM(CommandSender commandSender) {
        User user = User.getUser(commandSender.getName());
        return this.get("sendMessage_prefix").replace("{PREFIX}", user.playername);
    }
    public String getPlayerNameSMB(CommandSender commandSender) {
        User user = User.getUser(commandSender.getName());
        return this.get("sendMessageB_prefix").replace("{PREFIX}", user.playername);
    }
}
