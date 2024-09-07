package org.ezhik.authtgem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MessageTranslationMC {
    public static String tg_asign = "&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к Телеграмму";
    public static String code_accept_message = "&f&l[&b&lMT&f&l] &c&lВыполните команду /code (из телеграмма). Если это не вы, то проигнорируйте это сообщение.";

    public static void load(){
        File file = new File("plugins/Minetelegram/messagemc.yml");
        YamlConfiguration message_mc = new YamlConfiguration();
        try {
            message_mc.load(file);
        } catch (FileNotFoundException e) {
            message_mc.set("Code_Accept_Message", code_accept_message);
            message_mc.set("TG_Asign", tg_asign);
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
    }
}
