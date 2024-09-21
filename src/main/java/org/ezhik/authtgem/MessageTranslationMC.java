package org.ezhik.authtgem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MessageTranslationMC {
    public static String tg_asign = "&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к Телеграмму";
    public static String code_accept_message = "&f&l[&b&lMT&f&l] &c&lВыполните команду /code (из телеграмма). Если это не вы, то проигнорируйте это сообщение.";
    public static String no_asign_tg_addfriend = "&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к телеграмму";
    public static String friend_already_added = "&f&l[&b&lMT&f&l] &c&lВы уже добавляли этого игрока в друзья";
    public static String no_asign_tg_friend = "&f&l[&b&lMT&f&l] &c&lДанный игрок не привязывал аккаунт к телеграмму";
    public static String command_entered_incorrently_addfriend = "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /addfriend <ник>";

    public static void load(){
        File file = new File("plugins/Minetelegram/messageMC_RU.yml");
        YamlConfiguration message_mc = new YamlConfiguration();
        try {
            message_mc.load(file);
        } catch (FileNotFoundException e) {
            message_mc.set("Code_Accept_Message", code_accept_message);
            message_mc.set("TG_Asign", tg_asign);
            message_mc.set("No_Asign_TG_AddFriend", no_asign_tg_addfriend);
            message_mc.set("Friend_Already_Added", friend_already_added);
            message_mc.set("No_Asign_TG_Friend", no_asign_tg_friend);
            message_mc.set("Command_Entered_Incorrently_AddFriend", command_entered_incorrently_addfriend);
            try {
                message_mc.save(file);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (InvalidConfigurationException e) {
            System.out.println(e);
        }
        code_accept_message = message_mc.getString("Code_Accept_Message");
        tg_asign = message_mc.getString("TG_Asign");
        no_asign_tg_addfriend = message_mc.getString("No_Asign_TG_AddFriend");
        friend_already_added = message_mc.getString("Friend_Already_Added");
        no_asign_tg_friend = message_mc.getString("No_Asign_TG_Friend");
        command_entered_incorrently_addfriend = message_mc.getString("Command_Entered_Incorrently_AddFriend");
    }
}
