package org.ezhik.authtgem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

public class MessageTranslationTG {
    public static String code_accept_message = "[Бот] В игре выполните команду /code {CODE} что бы привязать аккаунт.";
    public static String start_message = "[Бот] Выполните следующие пункты:\n1.Войдите в игру.\n2.Авторизуйтесь.\n3.Напишите свой никнейм.";

    public static void load() {
        File file = new File("plugins/Minetelegram/messagetg.yml");
        YamlConfiguration message_tg = new YamlConfiguration();
        try {
            message_tg.load(file);
        } catch (FileNotFoundException e) {
            message_tg.set("Code_Accept_Message", code_accept_message);
            message_tg.set("Start_Message", start_message);
            try {
                message_tg.save(file);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (InvalidConfigurationException e) {
            System.out.println(e);
        }
        code_accept_message = message_tg.getString("Code_Accept_Message");
        start_message = message_tg.getString("Start_Message");
    }
}
