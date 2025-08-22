package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.handlers.*;
import org.ezhik.authTG.migrates.MySQLMigrate;
import org.ezhik.authTG.migrates.YAMLMigrate;
import org.ezhik.authTG.usersconfiguration.Loader;
import org.ezhik.authTG.usersconfiguration.MySQLLoader;
import org.ezhik.authTG.usersconfiguration.YAMLLoader;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;

public final class AuthTG extends JavaPlugin {
    public static Loader loader;
    public static BotTelegram bot;
    public static FileConfiguration config;


    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        config = getConfig();
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
        AuthHandler authHandler = new AuthHandler();
        authHandler.runTaskTimer(this, 0, 20);
        handler.runTaskTimer(this, 0, 1);
        if (getConfig().getConfigurationSection("mysql").getBoolean("use")) {
            ConfigurationSection mysql = getConfig().getConfigurationSection("mysql");
            loader = new MySQLLoader(mysql.getString("db"), mysql.getString("user"), mysql.getString("pass"), mysql.getString("host"));
            new MySQLMigrate(mysql.getString("db"), mysql.getString("host"), mysql.getString("user"), mysql.getString("pass"));
        } else {
            ConfigurationSection mysql = getConfig().getConfigurationSection("mysql");
            if (!getConfig().getString("mysql.host").equals("localhost")) new YAMLMigrate(mysql.getString("db"), mysql.getString("host"), mysql.getString("user"),mysql.getString("pass"));
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
        MuterEvent.setMutedPlayers(loader.getMutedPlayers());
        bot = new BotTelegram(getConfig().getString("bot.token"), getConfig().getString("bot.username"));
        if (!bot.BOT_IS_STARTED) {
            if (bot.getBotToken().equals("changeme") && bot.getBotUsername().equals("changeme")) {
                System.out.println("[AuthTG] Please set your bot token and username in config.yml");
                System.out.println("[AuthTG] Пожалуйста, укажите ваш токен и имя в config.yml");
            } else {
                TelegramBotsApi botsApi;
                try {
                    botsApi = new TelegramBotsApi(DefaultBotSession.class);
                    botsApi.registerBot(bot);
                    bot.BOT_IS_STARTED = true;
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
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
