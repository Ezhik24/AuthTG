package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.handlers.*;
import org.ezhik.authTG.migrates.*;
import org.ezhik.authTG.tabcompleter.*;
import org.ezhik.authTG.usersconfiguration.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AuthTG extends JavaPlugin {
    public static Loader loader;
    public static BotTelegram bot;
    public static FileConfiguration config;
    public static Logger logger;

    @Override
    public void onEnable() {
        // Load Logger
        logger = getLogger();
        // Load config plugin
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        config = getConfig();
        // Logs
        logger.log(Level.INFO, "Plugin started");
        // Register Events
        Bukkit.getServer().getPluginManager().registerEvents(new FreezerEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnJoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MuterEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockCommandEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockDropItemEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockDamageEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceBEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockDropBEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinAnotherEvent(), this);
        // Load placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        }
        // Load Handlers
        Handler handler = new Handler();
        AuthHandler authHandler = new AuthHandler();
        authHandler.runTaskTimer(this, 0, 20);
        handler.runTaskTimer(this, 0, 1);
        // Load UserConfiguration
        if (getConfig().getConfigurationSection("mysql").getBoolean("use")) {
            ConfigurationSection mysql = getConfig().getConfigurationSection("mysql");
            loader = new MySQLLoader(mysql.getString("db"), mysql.getString("user"), mysql.getString("pass"), mysql.getString("host"));
            new MySQLMigrate(mysql.getString("db"), mysql.getString("host"), mysql.getString("user"), mysql.getString("pass"));
        } else {
            ConfigurationSection mysql = getConfig().getConfigurationSection("mysql");
            if (!getConfig().getString("mysql.host").equals("localhost")) new YAMLMigrate(mysql.getString("db"), mysql.getString("host"), mysql.getString("user"),mysql.getString("pass"));
            loader = new YAMLLoader();
        }
        // Register commands
        getCommand("register").setExecutor(new RegisterCMD());
        getCommand("login").setExecutor(new LoginCMD());
        getCommand("code").setExecutor(new CodeCMD());
        getCommand("mcbc").setExecutor(new MCbcCMD());
        getCommand("tgbc").setExecutor(new TGbcCMD());
        getCommand("changepassword").setExecutor(new ChangePasswordCMD());
        getCommand("setpassword").setExecutor(new SetPasswordCMD());
        getCommand("friend").setExecutor(new FriendCMD());
        getCommand("setspawn").setExecutor(new SetSpawnCMD());
        getCommand("admin").setExecutor(new AdminCMD());
        getCommand("command").setExecutor(new CommandCMD());
        getCommand("kick").setExecutor(new KickCMD());
        getCommand("mute").setExecutor(new MuteCMD());
        getCommand("ban").setExecutor(new BanCMD());
        getCommand("unban").setExecutor(new UnBanCMD());
        getCommand("unmute").setExecutor(new UnMuteCMD());
        // Register TabCompleter
        getCommand("admin").setTabCompleter(new AdminTabCompleter());
        getCommand("friend").setTabCompleter(new FriendTabCompleter());
        getCommand("command").setTabCompleter(new CommandTabCompleter());
        // Load MutedPlayers
        MuterEvent.setMutedPlayers(loader.getMutedPlayers());
        // Load Bot
        bot = new BotTelegram(getConfig().getString("bot.token"), getConfig().getString("bot.username"));
        if (!bot.BOT_IS_STARTED) {
            if (bot.getBotToken().equals("changeme") && bot.getBotUsername().equals("changeme")) {
                logger.log(Level.INFO,"Please set your bot token and username in config.yml");
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
        // Logs
        logger.log(Level.INFO, "Plugin stopped");
    }
}
