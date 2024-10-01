package org.ezhik.authtgem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authtgem.commands.*;
import org.ezhik.authtgem.events.BlockCommandEvent;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;
import org.ezhik.authtgem.events.OnJoinEvent;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.TelegramBotsApi;

public final class AuthTGEM extends JavaPlugin {
    public static BotTelegram bot;
    public static MessageTranslationTG messageTG;

    @Override
    public void onEnable() {
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] Пожалуйста,подпишитесь на мой телеграмм канал https://t.me/ezhichek11"));
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] Please,subcribe for my telegram channel https://t.me/ezhichek11"));
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lПлагин включен!"));
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lPlugin enabled!"));
        Bukkit.getServer().getPluginManager().registerEvents(new FreezerEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnJoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MuterEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockCommandEvent(), this);
        Handler handler = new Handler();
        handler.runTaskTimer(this,0,1);
        getCommand("code").setExecutor(new CodeCMD());
        getCommand("register").setExecutor(new RegisterCMD());
        getCommand("login").setExecutor(new LoginCMD());
        getCommand("changepassword").setExecutor(new ChangepasswordCMD());
        getCommand("setpassword").setExecutor(new SetPasswordCMD());
        getCommand("tgbc").setExecutor(new TgbcCMD());
        getCommand("addfriend").setExecutor(new AddFriendCMD());
        getCommand("removefriend").setExecutor(new RemFriendCMD());
        getCommand("listfriends").setExecutor(new ListFriendsCMD());
        getCommand("tellfriends").setExecutor(new TellFriendsCMD());
        getCommand("setspawn").setExecutor(new SetSpawnCMD());
        messageTG = new MessageTranslationTG();
        bot = new BotTelegram();
        if (bot.getBotToken() == "changeme" && bot.getBotUsername() == "changeme") {
            System.out.println("Please set your bot token and username in config.yml");
            System.out.println("Пожалуйста, укажите ваш токен и имя в config.yml");
        } else {
            TelegramBotsApi botsApi = null;
            try {
                botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(bot);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] Пожалуйста,подпишитесь на мой телеграмм канал https://t.me/ezhichek11"));
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] Please,subcribe for my telegram channel https://t.me/ezhichek11"));
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lПлагин выключен!"));
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lPlugin disabled!"));
    }


}
