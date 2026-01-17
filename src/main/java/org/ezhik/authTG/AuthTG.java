package org.ezhik.authTG;

import org.apache.logging.log4j.LogManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.handlers.*;
import org.ezhik.authTG.migrates.*;
import org.ezhik.authTG.otherAPI.Log4JFilter;
import org.ezhik.authTG.otherAPI.PlaceholderAPI;
import org.ezhik.authTG.tabcompleter.*;
import org.ezhik.authTG.usersconfiguration.*;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
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
    private static AuthTG instance;
    private static String version;

    public static boolean notRegAndLogin, authNecessarily, activeChatinTG;
    public static List<String> mutecommands, commandsPreAuthorization, forbiddenNicknames;
    public static int ipregmax,minLenghtNickname, minLenghtPassword, maxLenghtNickname, maxLenghtPassword, timeoutSession, kickTimeout, maxAccountTGCount;
    public static double locationX, locationY, locationZ;
    public static String world;
    public static ConfigurationSection macro;

    private static MySQLPool mysqlPool;
    private BotSession botSession;

    @Override
    public void onEnable() {
        instance = this;
        version = getDescription().getVersion();
        logger = getLogger();


        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File usersDir = new File(getDataFolder(), "users");
        if (!usersDir.exists()) usersDir.mkdirs();


        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        if (!new File(getDataFolder(), "messages.yml").exists()) saveResource("messages.yml", true);


        saveResource("temp-config.yml", true);
        saveResource("temp-messages.yml", true);
        setupMessages();
        setupConfiguration();


        loadConfigParameters();

        logger.log(Level.INFO, "Plugin started");


        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new Log4JFilter());


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
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryEvent(), this);


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        }


        Metrics metrics = new Metrics(this, 27268);
        metrics.addCustomChart(new SingleLineChart("players", new Callable<Integer>() {
            @Override
            public Integer call() {
                return Bukkit.getOnlinePlayers().size();
            }
        }));
        metrics.addCustomChart(new SingleLineChart("servers", () -> 1));


        Handler handler = new Handler();
        AuthHandler authHandler = new AuthHandler();
        authHandler.runTaskTimer(this, 0, 20);
        handler.runTaskTimer(this, 0, 1);


        initLoader();


        registerCommands();


        registerTabCompleters();


        MuterEvent.setMutedPlayers(loader.getMutedPlayers());


        initTelegramBot();
    }

    @Override
    public void onDisable() {
        logger.log(Level.INFO, "Plugin stopped");

        if (botSession != null) {
            try {
                botSession.stop();
            } catch (Exception ignored) {}
            botSession = null;
        }

        if (mysqlPool != null) {
            try {
                mysqlPool.close();
            } catch (Exception ignored) {}
            mysqlPool = null;
        }
    }

    public static AuthTG getInstance() {
        return instance;
    }

    public static String getVersion() {
        return version;
    }

    private void initLoader() {
        ConfigurationSection mysql = getConfig().getConfigurationSection("mysql");
        boolean useMysql = mysql != null && mysql.getBoolean("use");

        if (useMysql) {
            ConfigurationSection pool = mysql.getConfigurationSection("pool");
            if (pool == null) {
                logger.log(Level.SEVERE, "[AuthTG] mysql.pool section not found in config.yml");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            int maxPool = pool.getInt("maximumPoolSize");
            long connTimeout = pool.getLong("connectionTimeoutMs");
            long idleTimeout = pool.getLong("idleTimeoutMs");
            long maxLifetime = pool.getLong("maxLifetimeMs");
            String jdbcParams = pool.getString("jdbcParams");

            try {
                mysqlPool = new MySQLPool(
                        mysql.getString("host"),
                        mysql.getString("db"),
                        mysql.getString("user"),
                        mysql.getString("pass"),
                        maxPool, connTimeout, idleTimeout, maxLifetime,
                        jdbcParams
                );

                MySQLSchemaMigrator.migrate(mysqlPool.dataSource(), mysql.getString("db"));
                loader = new MySQLLoader(mysqlPool.dataSource());


                if (!getConfig().getConfigurationSection("onceUsed").getBoolean("mysql")) {
                    new MySQLMigrate(mysql.getString("db"), mysql.getString("host"), mysql.getString("user"), mysql.getString("pass"));
                    getConfig().set("onceUsed.mysql", true);
                    saveConfig();
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AuthTG] Cannot init MySQL/Hikari: " + e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
            }

        } else {

            if (getConfig().getConfigurationSection("onceUsed").getBoolean("mysql")) {

                new YAMLMigrate(
                        getConfig().getConfigurationSection("mysql").getString("db"),
                        getConfig().getConfigurationSection("mysql").getString("host"),
                        getConfig().getConfigurationSection("mysql").getString("user"),
                        getConfig().getConfigurationSection("mysql").getString("pass")
                );
                getConfig().set("onceUsed.mysql", false);
                saveConfig();
            }
            loader = new YAMLLoader();
        }
    }

    private void initTelegramBot() {
        DefaultBotOptions options = new DefaultBotOptions();


        options.setGetUpdatesTimeout(50);

        bot = new BotTelegram(
                getConfig().getString("bot.token"),
                getConfig().getString("bot.username"),
                options
        );

        if ("changeme".equals(bot.getBotToken()) && "changeme".equals(bot.getBotUsername())) {
            logger.log(Level.INFO, "Please set your bot token and username in config.yml");
            return;
        }
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);


            if (botSession != null) {
                try {
                    botSession.stop();
                } catch (Exception ignored) {
                }
                botSession = null;
            }

            botSession = botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.severe("Error: " + e.getMessage());
        }
    }

    private void registerCommands() {
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
        getCommand("unlink").setExecutor(new UnLinkCMD());
        getCommand("authtg").setExecutor(new AuthTGCMD());
    }

    private void registerTabCompleters() {
        getCommand("admin").setTabCompleter(new AdminTabCompleter());
        getCommand("friend").setTabCompleter(new FriendTabCompleter());
        getCommand("command").setTabCompleter(new CommandTabCompleter());
        getCommand("ban").setTabCompleter(new BanTabCompleter());
        getCommand("mute").setTabCompleter(new MuteTabCompleter());
        getCommand("setspawn").setTabCompleter(new SetSpawnTabCompleter());
    }

    private void setupConfiguration() {
        File fileTemp = new File(getDataFolder(), "temp-config.yml");
        YamlConfiguration configTemp = YamlConfiguration.loadConfiguration(fileTemp);

        File fileGlobal = new File(getDataFolder(), "config.yml");
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
        File fileTemp = new File(getDataFolder(), "temp-messages.yml");
        YamlConfiguration configTemp = YamlConfiguration.loadConfiguration(fileTemp);

        File fileGlobal = new File(getDataFolder(), "messages.yml");
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
        File file = new File(getInstance().getDataFolder(), "messages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if ("MC".equals(MCorTGorCNS)) {
            return config.getString("messages.minecraft." + path, "").replace("{BR}", "\n");
        } else if ("TG".equals(MCorTGorCNS)) {
            return config.getString("messages.telegram." + path, "").replace("{BR}", "\n");
        } else if ("CE".equals(MCorTGorCNS)) {
            return config.getString("messages.minecraft." + path, "").replace("{BR}", "\n");
        } else {
            logger.log(Level.SEVERE, "Message path not found, please contact the developer");
            return "";
        }
    }

    public static String getPlaceholderMessage(String placeholder, String path) {
        File file = new File(getInstance().getDataFolder(), "messages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if ("none".equals(placeholder)) {
            return config.getString("placeholders." + path, "");
        } else if ("activetg".equals(placeholder) || "twofactor".equals(placeholder) || "status".equals(placeholder)) {
            return config.getString("placeholders." + placeholder + "." + path, "");
        } else {
            logger.log(Level.SEVERE, "Placeholder path not found, please contact the developer");
            return "";
        }
    }

    public static void loadConfigParameters() {
        File file = new File(getInstance().getDataFolder(), "config.yml");
        YamlConfiguration getConfig = YamlConfiguration.loadConfiguration(file);

        maxAccountTGCount = getConfig.getInt("maxAccountTGCount");
        forbiddenNicknames = getConfig.getStringList("forbiddenNicknames");
        notRegAndLogin = getConfig.getBoolean("notRegAndLogin");
        authNecessarily = getConfig.getBoolean("authNecessarily");
        activeChatinTG = getConfig.getBoolean("activeChatinTG");
        mutecommands = getConfig.getStringList("mutecommands");
        commandsPreAuthorization = getConfig.getStringList("commandsPreAuthorization");
        minLenghtNickname = getConfig.getInt("minLenghtNickname");
        maxLenghtNickname = getConfig.getInt("maxLenghtNickname");
        minLenghtPassword = getConfig.getInt("minLenghtPassword");
        maxLenghtPassword = getConfig.getInt("maxLenghtPassword");
        timeoutSession = getConfig.getInt("timeoutSession");
        kickTimeout = getConfig.getInt("kickTimeout");
        locationX = getConfig.getDouble("spawn.x");
        locationY = getConfig.getDouble("spawn.y");
        locationZ = getConfig.getDouble("spawn.z");
        world = getConfig.getString("spawn.world");
        macro = getConfig.getConfigurationSection("macro");
        ipregmax = getConfig.getInt("ipregmax");
    }
}
