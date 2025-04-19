package org.ezhik.authtgem.message;

import org.bukkit.Bukkit;
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
            this.put("sendmcmsg_friend", "[Бот@{PLAYER}] Отправьте текст сообщения");
            this.put("sendmsg_friend", "[Бот@{PLAYER}] Отправьте текст сообщения");
            this.put("del_friends", "[Бот] ");
            this.put("kickme_player_notfound","[Бот] Пользователь не зарегистрирован или он не в игре");
            this.put("resetpass_player_notfound","[Бот] Пользователь не зарегистрирован или он не в игре");
            this.put("tfon_player_notfound", "[Бот] Пользователь не зарегистрирован или он не в игре");
            this.put("tfoff_player_notfound", "[Бот] Пользователь не зарегистрирован или он не в игре");
            this.put("unlink_player_notfound", "[Бот] Зайди в игру и попробуй еще раз");
            this.put("kickme_kick_succes", " Вы успешно кикнули свой аккаунт через телеграми!");
            this.put("tg_noasign_chat", "[Бот] Привяжите учетную запись к Телеграмму");
            this.put("tgasign_incorrect_password","[Бот] Неверный пароль, повторите попытку");
            this.put("account_already_tgasign", "[Бот] Вы уже привязывали эту учетную запись");
            this.put("account_already_tgasign_round", "[Бот] Эта учетная запись уже привязана к другой учетной записи Телегримма");
            this.put("tgasign_check_password", "[Бот] Введите пароль от аккаунта");
            this.put("bid_succes_rejected", "Заявка успешно отклонена");
            this.put("sendmsg_message","Сообщение от пользователя {PLAYER} : ");
            this.put("addfriend_succesadd","Вам добавлен в друзья {PLAYER}");
            this.put("acc_choose","[Бот] Выбран игрок {PLAYER}");
            this.put("acc_logining", "Ваш аккаунт {PLAYER} вошёл в игру! {BR} Если это не вы,то кикните аккаунт и смените пароль!");
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

    public String getFriendPNTell(Player player) {
        User user = User.getUser(player.getName());
        return this.get("tellfriends_message_succes").replace("{PLAYER}", user.playername);
    }

    public String getPlayerNameSM(String  playername) {
        return this.get("sendMessage_prefix").replace("{PLAYER}", playername);
    }

    public String getPlayerNameSMB(String playername) {
        return this.get("sendMessageB_prefix").replace("{PLAYER}",playername);
    }

    public String getPNFriendOnJoin(Player player) {
        User user = User.getUser(player.getName());
        return  this.get("friends_join_game").replace("{PLAYER}", user.playername);
    }

    public String getPNtgAct(String friendname) {
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
        return this.get("code_activate_acc").replace("{CODE}",code);
    }

    public String getPassword(String password) {
        return this.get("changepassword_intg").replace("{PASSWORD}", password);
    }

    public String getSendMCMsgFriendPN(Long chatid) {
        User user = User.getCurrentUser(chatid);
        return this.get("sendmcmsg_friend").replace("{PLAYER}", user.playername);
    }

    public String getSendMsgFriendPN(String playername) {
        return this.get("sendmsg_friend").replace("{PLAYER}", playername);
    }

    public String getPNSendMSGmessage(User user) {
        return this.get("sendmsg_message").replace("{PLAYER}", user.playername);
    }

    public String getAddFriendPN(String friendname) {
        User user = User.getUser(Bukkit.getPlayer(friendname).getUniqueId());
        return this.get("addfriend_succesadd").replace("{PLAYER}", user.playername);
    }

    public String getAccChoosePN(String playername) {
        return this.get("acc_choose").replace("{PLAYER}",playername);
    }

    public String getAccLogining(String playername) {
        return this.get("acc_logining").replace("{PLAYER}", playername);
    }
}
