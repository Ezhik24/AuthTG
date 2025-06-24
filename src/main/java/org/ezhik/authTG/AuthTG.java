package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.usersconfiguration.Loader;
import org.ezhik.authTG.usersconfiguration.YAMLLoader;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public final class AuthTG extends JavaPlugin {
    public static Loader loader;
    public static BotTelegram bot;

    @Override
    public void onEnable() {
        loader = new YAMLLoader();
        System.out.println("[AuthTG] Плагин запустился | Plugin started");
        System.out.println("[AuthTG] Пожалуйста,подпишитесь на мой ТГ канал: https://t.me/ezhichek11");
        System.out.println("[AuthTG] Please,subscribe for my tg channel: https://t.me/ezhichek11");
        Bukkit.getServer().getPluginManager().registerEvents(new FreezerEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnJoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MuterEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockCommandEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockDropItemEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockDamageEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceBEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockDropBEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinAnotherEvent(), this);
        Handler handler = new Handler();
        handler.runTaskTimer(this, 0, 1);
        getCommand("register").setExecutor(new RegisterCMD());
        getCommand("login").setExecutor(new LoginCMD());
        getCommand("code").setExecutor(new CodeCMD());
        bot = new BotTelegram();
        if (bot.getBotToken() == "changeme" && bot.getBotUsername() == "changeme") {
            System.out.println("Please set your bot token and username in botconf.yml");
            System.out.println("Пожалуйста, укажите ваш токен и имя в botconf.yml");
        } else {
            TelegramBotsApi botsApi;
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
        System.out.println("[AuthTG] Плагин выключен | Plugin disable");
        System.out.println("[AuthTG] Пожалуйста,подпишитесь на мой ТГ канал: https://t.me/ezhichek11");
        System.out.println("[AuthTG] Please,subscribe for my tg channel: https://t.me/ezhichek11");
    }
}
