package org.ezhik.authtgem.message;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.User;

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
            this.put("sendMessage_prefix", "[Бот@" + "{PLAYER}" + "] ");
            this.put("sendMessageB_prefix", "[Бот@" + "{PLAYER}" + "] ");
            this.put("sendMessageBroadCast", "[Бот] ");
            this.put("start_message", "[Бот] Выполните следующие пункты: {BR} 1.Войдите в игру. {BR} 2.Авторизуйтесь. {BR} 3.Напишите свой никнейм.");
            this.put("tg_noasign_hashtag", "[Бот] Привяжите учётную запись к телеграму.");
            this.put("addfriends_yes", "Да");
            this.put("addfriends_no", "Нет");
            this.put("login_intg_yes", "Да");
            this.put("login_intg_not", "Нет");
            this.put("addfriends_req", "Вы хотите добавить {PLAYER} в друзья?");
            this.put("tellfriends_message_succes", " сообщение от {PLAYER} :");
            this.put("removefriend_friend_remove", "{PLAYER} удалил вас из друзей");
            this.put("friends_act", "Действия");
            this.put("friends_act_friend", "Действия с {PLAYER}");
            this.put("friends_act_minecraftmsg", "Отправить сообщение в Minecraft");
            this.put("friends_act_remfriend", "Удалить из друзей");
            this.put("friends_act_tgmsg", "Отправить личное сообщение");
            this.put("friends_list", "Ваши друзья");
            this.put("friends_list_notfriend", "У вас нет друзей");
            this.put("account_choose", "Выберите игрока");
            this.put("code_account_activated", "Ваш аккаунт успешно активирован!");
            this.put("login_who_entered","[Бот] Это вы вошли в игру?");
            this.put("kick_account_inTG", "Владелец кикнул аккаунт через телеграмм");
            this.put("friends_join_game", "{PLAYER} вошёл в игру");
            this.put("code_deactivated_acc", "Выполните в игре команду: /code {CODE} что бы отвязать аккаунт.");
            this.put("code_activate_acc", "[Бот] В игре выполните команду /code {CODE} что бы привязать аккаунт.");
            this.put("changepassword_intg", " Ваш пароль был изменен на {PASSWORD} . {BR} Обязательно смените пароль,после захода на сервер! {BR} Команда для смены пароля: /cp");
            this.put("auth_in_2step_on", "Двухфакторная авторизация успешно включена");
            this.put("auth_in_2step_off", "Двухфакторная авторизация успешно выключена");
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

    public String getFriendPN(Player player) {
        User user = User.getUser(player.getName());
        return this.get("tellfriends_message_succes").replace("{PLAYER}", user.playername);
    }

    public String getPlayerNameSM(Long chatid) {
        User user = User.getCurrentUser(chatid);
        return this.get("sendMessage_prefix").replace("{PLAYER}", user.playername);
    }

    public String getPlayerNameSMB(Long chatid) {
        User user = User.getCurrentUser(chatid);
        return this.get("sendMessageB_prefix").replace("{PLAYER}",user.playername);
    }

    public String getPlayerNameFriend(Player player) {
        User user = User.getUser(player.getName());
        return  this.get("friends_join_game").replace("{PLAYER}", user.playername);
    }

    public String getFriendNameTG(String friendname) {
        return this.get("friends_act_friend").replace("{PLAYER}", friendname);
    }

    public String getRemoveFriend(CommandSender commandSender) {
        Player player = (Player) commandSender;
        return this.get("removefriend_friend_remove").replace("{PLAYER}", player.getName());
    }

    public String getCodeDeActivated(String code) {
        return this.get("code_deactivated_acc").replace("{CODE}",code);
    }

    public String getCodeActivated(String code) {
        return this.get("code_activated_acc").replace("{CODE}",code);
    }

    public String getPassword(String password) {
        return this.get("changepassword_intg").replace("{PASSWORD}", password);
    }
}
