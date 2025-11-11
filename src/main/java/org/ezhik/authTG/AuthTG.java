package org.ezhik.authTG;

import org.apache.logging.log4j.LogManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.handlers.*;
import org.ezhik.authTG.migrates.*;
import org.ezhik.authTG.otherAPI.*;
import org.ezhik.authTG.session.IPManager;
import org.ezhik.authTG.session.SessionManager;
import org.ezhik.authTG.tabcompleter.*;
import org.ezhik.authTG.usersconfiguration.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AuthTG extends JavaPlugin {
    public static Loader loader;
    public static BotTelegram bot;
    public static Logger logger;
    private static Plugin instance;
    private static String version;
    public  static SessionManager sessionManager;
    public static boolean notRegAndLogin, authNecessarily, activeChatinTG;
    public static List<String> mutecommands, commandsPreAuthorization;
    public static int minLenghtNickname, minLenghtPassword, maxLenghtNickname, maxLenghtPassword, timeoutSession,kickTimeout;
    public static double locationX, locationY, locationZ;
    public static String world;
    public static ConfigurationSection macro;

    @Override
    public void onEnable() {
        // Init
        instance = this;
        version = getDescription().getVersion();
        // Load Logger
        logger = getLogger();
        // Load config plugin
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        if (!new File(getDataFolder(), "messages.yml").exists()) saveResource("messages.yml", true);
        // Generate temp-config.yml
        saveResource("temp-config.yml", true);
        saveResource("temp-messages.yml", true);
        setupMessages();
        setupConfiguration();
        // Load config parameters
        loadConfigParameters();
        //Load SessionManager
        if (Bukkit.getServer().getPluginManager().getPlugin("AuthTGCookie") != null) {
            //TODO
        } else {
            sessionManager = new IPManager();
        }
        // Logs
        logger.log(Level.INFO, "Plugin started");
        // Load LoggerCore
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new Log4JFilter());
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
        Bukkit.getServer().getPluginManager().registerEvents(new onLeaveEvent(), this);
        // Load placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        }
        // Load Metrics
        Metrics metrics = new Metrics(this, 27268);
        metrics.addCustomChart(new SingleLineChart("players", new Callable<Integer>() {
            @Override
            public Integer call() {
                return Bukkit.getOnlinePlayers().size();
            }
        }));
        metrics.addCustomChart(new SingleLineChart("servers", () -> 1));
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
        getCommand("logout").setExecutor(new LogoutCMD());
        // Register TabCompleter
        getCommand("admin").setTabCompleter(new AdminTabCompleter());
        getCommand("friend").setTabCompleter(new FriendTabCompleter());
        getCommand("command").setTabCompleter(new CommandTabCompleter());
        // Load MutedPlayers
        MuterEvent.setMutedPlayers(loader.getMutedPlayers());
        // Load Bot
        bot = new BotTelegram(getConfig().getString("bot.token"), getConfig().getString("bot.username"));
        if (bot.getBotToken().equals("changeme") && bot.getBotUsername().equals("changeme")) {
            logger.log(Level.INFO,"Please set your bot token and username in config.yml");
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
        // Logs
        logger.log(Level.INFO, "Plugin stopped");
        // Save spawn location
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static String getVersion() {
        return version;
    }

    private void setupConfiguration() {
        File fileTemp = new File("plugins/AuthTG/temp-config.yml");
        YamlConfiguration configTemp = YamlConfiguration.loadConfiguration(fileTemp);
        File fileGlobal = new File("plugins/AuthTG/config.yml");
        YamlConfiguration configGlobal = YamlConfiguration.loadConfiguration(fileGlobal);
        Set<String> set = configTemp.getKeys(true);
        Set<String> setGlobal = configGlobal.getKeys(true);
        set.removeAll(setGlobal);
        for (String key : set) {
            configGlobal.set(key, configTemp.get(key));
        }
        try {
            configGlobal.save(fileGlobal);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't save config.yml");
        }
        fileTemp.delete();
    }
    private void setupMessages() {
        File fileTemp = new File("plugins/AuthTG/temp-messages.yml");
        YamlConfiguration configTemp = YamlConfiguration.loadConfiguration(fileTemp);
        File fileGlobal = new File("plugins/AuthTG/messages.yml");
        YamlConfiguration configGlobal = YamlConfiguration.loadConfiguration(fileGlobal);
        Set<String> set = configTemp.getKeys(true);
        Set<String> setGlobal = configGlobal.getKeys(true);
        set.removeAll(setGlobal);
        for (String key : set) {
            configGlobal.set(key, configTemp.get(key));
        }
        try {
            configGlobal.save(fileGlobal);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't save messages.yml");
        }
        fileTemp.delete();
    }
    public static String getMessage(String path, String MCorTGorCNS) {
        File file = new File("plugins/AuthTG/messages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (MCorTGorCNS.equals("MC")) {
            return config.getString("messages.minecraft." + path).replace("{BR}", "\n");
        } else if (MCorTGorCNS.equals("TG")) {
            return config.getString("messages.telegram." + path).replace("{BR}", "\n");
        } else if (MCorTGorCNS.equals("CE")) {
            return config.getString("messages.minecraft." + path).replace("{BR}", "\n");
        }
        else {
            logger.log(Level.SEVERE, "Message path not found, please contact the developer");
            return null;
        }
    }

    public static String getPlaceholderMessage(String placeholder, String path) {
        File file = new File("plugins/AuthTG/messages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (placeholder.equals("none")) {
            return config.getString("placeholders." + path);
        } else if (placeholder.equals("activetg")) {
            return config.getString("placeholders." + placeholder + "." + path);
        } else if (placeholder.equals("twofactor")) {
            return config.getString("placeholders." + placeholder + "." + path);
        } else if (placeholder.equals("status")) {
            return config.getString("placeholders." + placeholder + "." + path);
        } else {
            logger.log(Level.SEVERE, "Placeholder path not found, please contact the developer");
            return null;
        }
    }

    private void loadConfigParameters() {
        notRegAndLogin = getConfig().getBoolean("notRegAndLogin");
        authNecessarily = getConfig().getBoolean("authNecessarily");
        activeChatinTG = getConfig().getBoolean("activeChatinTG");
        mutecommands = getConfig().getStringList("mutecommands");
        commandsPreAuthorization = getConfig().getStringList("commandsPreAuthorization");
        minLenghtNickname = getConfig().getInt("minLenghtNickname");
        maxLenghtNickname = getConfig().getInt("maxLenghtNickname");
        minLenghtPassword = getConfig().getInt("minLenghtPassword");
        maxLenghtPassword = getConfig().getInt("maxLenghtPassword");
        timeoutSession = getConfig().getInt("timeoutSession");
        kickTimeout = getConfig().getInt("kickTimeout");
        locationX = getConfig().getDouble("spawn.x");
        locationY = getConfig().getDouble("spawn.y");
        locationZ = getConfig().getDouble("spawn.z");
        world = getConfig().getString("spawn.world");
        macro = getConfig().getConfigurationSection("macro");
    }
}
