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

public class MessageTranslationMC extends LinkedHashMap<String, String> {
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
            this.put("removefriend_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /remfriend <ник>");
            this.put("removefriend_tg_noasign","&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к телеграмму");
            this.put("removefriend_succes_remove", "&f&l[&b&lMT&f&l] &c&lВы удалили {PLAYER} из друзей");
            this.put("removefriend_notfound_friend", "Такого игрока нет в друзьях");
            this.put("removefriend_friend_succes", "&f&l[&b&lMT&f&l] &c&l{PLAYER} удалил вас из друзей");
            this.put("listfriends_list", "&f&l[&b&lMT&f&l] &c&lСписок друзей:");
            this.put("listfriends_friend", "&a&l");
            this.put("listfriends_online_friend", " [Online]");
            this.put("listfriends_offline_friend", " [Offline]");
            this.put("tellfriends_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /tellfriends <ник> <сообщение>");
            this.put("tellfriends_sendmessage_succes","&f&l[&b&lMT&f&l] &a&lСообщение отправлено");
            this.put("tellfriends_friends_tgasign","&f&l[&b&lMT&f&l] &c&lВаш друг отвязал телеграмм аккаунт");
            this.put("changepassword_success", "&f&l[&b&lMT&f&l] &a&lВы успешно изменили пароль");
            this.put("changepassword_oldpasswd_wrong", "&f&l[&b&lMT&f&l] &c&lНеверный старый пароль");
            this.put("changepassword_newpasswd_wrong", "&f&l[&b&lMT&f&l] &c&lПароли не совпадают");
            this.put("changepassword_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду  так: /changepassword <старый пароль> <новый пароль>");
            this.put("code_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /code <код>");
            this.put("code_account_deactivated", "&f&l[&b&lMT&f&l] &c&lАккаунт был успешно деактивирован");
            this.put("code_account_activated", "&f&l[&b&lMT&f&l] &a&lАккаунт был успешно активирован");
            this.put("code_invalid", "&f&l[&b&lMT&f&l] &c&lНеверный код. Попробуйте еще раз.");
            this.put("login_successful_login", "&f&l[&b&lMT&f&l] &a&lВы успешно вошли в игру");
            this.put("login_who_entered", "&f&l[&b&lMT&f&l] &a&lЭто вы вошли в игру?");
            this.put("login_wrong_password", "&f&l[&b&lMT&f&l] &c&lНеверный пароль");
            this.put("login_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /login <пароль>");
            this.put("login_title_tg_s1color", "&c&l");
            this.put("login_title_tg_s1","Потвердите вход");
            this.put("login_title_tg_s2","через Телеграмм");
            this.put("login_title_login_s1", "&a&lАвторизуйтесь!");
            this.put("login_title_login_s2", "для авторизации введите команду: /login <пароль>");
            this.put("login_message", "&f&l[&b&lMT&f&l] &c&lАвторизуйтесь! Для авторизации введите команду: /login <пароль>");
            this.put("register_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /register <пароль> <повтор пароля>");
            this.put("register_successful_register", "&f&l[&b&lMT&f&l] &a&lВы успешно зарегистрировали аккаунт");
            this.put("register_wrong_passwords", "&f&l[&b&lMT&f&l] &c&lПароли не совпадают");
            this.put("register_already_register", "&f&l[&b&lMT&f&l] &c&lВы уже зарегистрированы. Для сброса пароля обратитесь к Администратору");
            this.put("register_title_s1", "&c&lЗарегистрируйтесь!");
            this.put("register_title_s2", "для регистрации введите команду: /register <пароль> <повтор пароля>");
            this.put("register_message", "&f&l[&b&lMT&f&l] &c&lЗарегистрируйтесь! Для авторизации введите команду: /register <пароль> <повтор пароля>");
            this.put("setpassword_wrong_command", "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /setpassword <ник> <новый пароль> <повтор нового пароля>");
            this.put("setpassword_nopermission", "&f&l[&b&lMT&f&l] &c&lУ вас нет прав для использования этой команды");
            this.put("setpassword_wrong_password","&f&l[&b&lMT&f&l] &c&lПароли не совпадают");
            this.put("setpassword_succesfly_chanpass","&f&l[&b&lMT&f&l] &a&lВы успешно изменили пароль игроку {PLAYER}");
            this.put("setspawn_succesfly_none", "&f&l[&b&lMT&f&l] &a&lТочка спавна установлена");
            this.put("setspawn_succesfly_location", "&f&l[&b&lMT&f&l] &a&lТочка спавна установлена");
            this.put("setspawn_nopermission", "&f&l[&b&lMT&f&l] &c&lУ вас нет прав для использования этой команды");
            this.put("code_deactivated_acc", "&f&l[&b&lMT&f&l] Выполните в игре команду: /code <код из телеграмма> что бы отвязать аккаунт.");
            this.put("code_activate_acc", "&f&l[&b&lMT&f&l] &c&lВыполните команду /code (из телеграмма). Если это не вы, то проигнорируйте это сообщение.");
            this.put("tgbc_nopermission", "&f&l[&b&lMT&f&l] &c&lУ вас нет прав для использования этой команды");
            this.put("command_block","&f&l[&b&lMT&f&l] &c&lЭта команда доступна только для зарегистрированных пользователей!");
            this.put("joinplayer_tgasign","&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к Телеграмму");
            this.put("hashtag_sendmsg_minecraft", "&f&l[&b&lMT&f&l] &a&lИгрок {PLAYER} передает сообщение из офлайна: ");
            this.put("bid_rejected","&f&l[&b&lMT&f&l] &c&lЗаявка в друзья отклонена");
            this.put("succes_login_account"," &f&l[&b&lMT&f&l] &a&lУспешный вход в аккаунт!");
            this.put("rejected_login_account"," &f&l[&b&lMT&f&l] &c&lОтклонено Владельцем учетной записи из Телеграмма");
            this.put("sendmcmsg_friend"," &f&l[&b&lMT&f&l] &a&lСообщение от пользователя {PLAYER}: ");
            this.put("user1_added_friend","&f&l[&b&lMT&f&l] &a&lВам добавлен в друзья {PLAYER}");
            this.put("user2_added_friend", "&f&l[&b&lMT&f&l] &a&lВам добавлен в друзья {PLAYER}");
            this.put("account_auth_nessery1","&c&lПривяжи аккаунт");
            this.put("account_auth_nessery2", "/start в боте");
            this.put("joinAnotherLocate", "&c&lКто-то уже играет с этого никнейма...");
            File newconfigfile = new File("plugins/Minetelegram/messages/messageMC_RU.yml");
            YamlConfiguration newmessageconfig = new YamlConfiguration();
            for (String key : this.keySet()) {
                newmessageconfig.set(key, this.get(key));
                this.replace(key, this.get(key).replace("{BR}", "\n"));
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
            this.put(key, messageconfig.getString(key).replace("{BR}", "\n"));
        }
    }

    public String getSetpasswordPlayerName(String[] strings) {
        Player player = Bukkit.getPlayer(strings[0]);
        return this.get("setpassword_succesfly_chanpass").replace("{PLAYER}",player.getName());
    }

    public String getFriendNameRemove(String friendname) {
        return this.get("removefriend_succes_remove").replace("{PLAYER}", friendname);
    }

    public String getFriendRemovePN(CommandSender commandSender) {
        Player player = (Player) commandSender;
        return this.get("removefriend_friend_succes").replace("{PLAYER}", player.getName());
    }

    public String getHashtagPN(Long chatid) {
        User user = User.getCurrentUser(chatid);
        return this.get("hashtag_sendmsg_minecraft").replace("{PLAYER}", user.playername);
    }

    public String getSendMCmsgPN(Long chatid) {
        User user = User.getCurrentUser(chatid);
        return this.get("sendmcmsg_friend").replace("{PLAYER}",user.playername);
    }

    public String getPNaddedUser1Friend(String friendname) {
        User user = User.getUser(Bukkit.getPlayer(friendname).getUniqueId());
        return this.get("user1_added_friend").replace("{PLAYER}",user.playername);
    }

    public String getPNaddedUser2Friend(Long chatid) {
        User user = User.getOnlineUser(chatid);
        return this.get("user2_added_friend").replace("{PLAYER}", user.playername);
    }
}