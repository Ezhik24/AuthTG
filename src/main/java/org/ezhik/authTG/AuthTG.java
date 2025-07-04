package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.configuration.GlobalConfig;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.migrates.MySQLMigrate;
import org.ezhik.authTG.migrates.YAMLMigrate;
import org.ezhik.authTG.usersconfiguration.Loader;
import org.ezhik.authTG.usersconfiguration.MySQLLoader;
import org.ezhik.authTG.usersconfiguration.YAMLLoader;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public final class AuthTG extends JavaPlugin {
    public static Loader loader;
    public static BotTelegram bot;
    public static GlobalConfig globalConfig;


    @Override
    public void onEnable() {
        globalConfig = new GlobalConfig();
        System.out.println("[AuthTG] Плагин запустился | Plugin started");
        System.out.println("[AuthTG] Пожалуйста,подпишитесь на ТГ канал AuthTG: https://t.me/authtgspigot");
        System.out.println("[AuthTG] Please,subscribe for my channel AuthTG: https://t.me/authtgspigot");
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
        if (globalConfig.useMySQL) {
            loader = new MySQLLoader(globalConfig.mySQLdatabase, globalConfig.mySQLUser, globalConfig.mySQLPassword, globalConfig.mySQLHost);
            new MySQLMigrate(globalConfig.mySQLdatabase,globalConfig.mySQLHost, globalConfig.mySQLUser, globalConfig.mySQLPassword);
        } else {
            new YAMLMigrate(globalConfig.mySQLdatabase,globalConfig.mySQLHost, globalConfig.mySQLUser, globalConfig.mySQLPassword);
            loader = new YAMLLoader();
        }
        getCommand("register").setExecutor(new RegisterCMD());
        getCommand("login").setExecutor(new LoginCMD());
        getCommand("code").setExecutor(new CodeCMD());
        getCommand("mcbc").setExecutor(new MCbcCMD());
        getCommand("tgbc").setExecutor(new TGbcCMD());
        getCommand("changepassword").setExecutor(new ChangePasswordCMD());
        getCommand("setpassword").setExecutor(new SetPasswordCMD());
        getCommand("friend").setExecutor(new FriendCMD());
        getCommand("setspawn").setExecutor(new SetSpawnCMD());
        bot = new BotTelegram();
        if (bot.getBotToken() == "changeme" && bot.getBotUsername() == "changeme") {
            System.out.println("[AuthTG] Please set your bot token and username in botconf.yml");
            System.out.println("[AuthTG] Пожалуйста, укажите ваш токен и имя в botconf.yml");
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
        System.out.println("[AuthTG] Пожалуйста,подпишитесь на ТГ канал AuthTG: https://t.me/authtgspigot");
        System.out.println("[AuthTG] Please,subscribe for my channel AuthTG: https://t.me/authtgspigot");
    }
}
