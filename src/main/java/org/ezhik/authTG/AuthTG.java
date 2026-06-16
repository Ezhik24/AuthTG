package org.ezhik.authTG;

import org.apache.logging.log4j.LogManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.ezhik.authTG.captcha.ClickInventoryEvent;
import org.ezhik.authTG.commandMC.*;
import org.ezhik.authTG.events.*;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.migrates.MySQLMigrate;
import org.ezhik.authTG.migrates.YAMLMigrate;
import org.ezhik.authTG.otherAPI.Log4JFilter;
import org.ezhik.authTG.otherAPI.PlaceholderAPI;
import org.ezhik.authTG.tabcompleter.*;
import org.ezhik.authTG.usersconfiguration.Loader;
import org.ezhik.authTG.usersconfiguration.MySQLLoader;
import org.ezhik.authTG.usersconfiguration.MySQLPool;
import org.ezhik.authTG.usersconfiguration.MySQLSchemaMigrator;
import org.ezhik.authTG.usersconfiguration.YAMLLoader;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AuthTG extends JavaPlugin {

    public static Loader loader;
    public static BotTelegram bot;
    public static BotVK vk;
    public static Logger logger;

    private static AuthTG instance;
    private static String version;
    private static YamlConfiguration messagesConfig;

    public static boolean notRegAndLogin;
    public static boolean authNecessarily;
    public static boolean activeChatinTG;
    public static boolean telegramEnabled;
    public static boolean vkEnabled;
    public static boolean openimmediately;

    public static List<String> mutecommands;
    public static List<String> commandsPreAuthorization;
    public static List<String> forbiddenNicknames;

    public static int ipregmax;
    public static int minLenghtNickname;
    public static int minLenghtPassword;
    public static int maxLenghtNickname;
    public static int maxLenghtPassword;
    public static int timeoutSession;
    public static int kickTimeout;
    public static int maxAccountTGCount;
    public static int maxAccountVKCount;

    public static double locationX;
    public static double locationY;
    public static double locationZ;

    public static String world;
    public static String authNecessarilyPrefer;

    public static ConfigurationSection macro;

    private static MySQLPool mysqlPool;
    private BotSession botSession;

    @Override
    public void onEnable() {
        instance = this;
        version = getDescription().getVersion();
        logger = getLogger();

        ensureDataFolders();
        ensureDefaultResources();

        setupMessages();
        setupConfiguration();

        reloadConfig();
        loadMessagesConfiguration();
        loadConfigParameters();

        logger.log(Level.INFO, "Plugin started");

        org.apache.logging.log4j.core.Logger coreLogger =
                (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new Log4JFilter());

        registerEvents();
        registerPlaceholderApi();
        registerMetrics();
        startSchedulers();

        initLoader();
        registerCommands();
        registerTabCompleters();

        MuterEvent.setMutedPlayers(loader.getMutedPlayers());
        initTelegramBot();
        initVKBot();
    }

    @Override
    public void onDisable() {
        logger.log(Level.INFO, "Plugin stopped");
        stopTelegramBot();

        if (mysqlPool != null) {
            try {
                mysqlPool.close();
            } catch (Exception ignored) {
            }
            mysqlPool = null;
        }

        if (vk != null) {
            try {
                vk.shutdown();
            } catch (Exception ignored) {
            }
            vk = null;
        }
    }

    public static AuthTG getInstance() {
        return instance;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isTelegramEnabled() {
        return telegramEnabled;
    }
    public static boolean isVKEnabled() {
        return vkEnabled;
    }

    public static DataSource getDataSource() {
        return mysqlPool == null ? null : mysqlPool.dataSource();
    }

    public void reloadPluginRuntime() {
        reloadConfig();
        loadMessagesConfiguration();
        loadConfigParameters();
        initTelegramBot();
    }

    private void ensureDataFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File usersDir = new File(getDataFolder(), "users");
        if (!usersDir.exists()) {
            usersDir.mkdirs();
        }
    }

    private void ensureDefaultResources() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        if (!new File(getDataFolder(), "messages.yml").exists()) {
            saveResource("messages.yml", true);
        }

        saveResource("temp-config.yml", true);
        saveResource("temp-messages.yml", true);
    }

    private void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new ClickInventoryEvent(), this);
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
    }

    private void registerPlaceholderApi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        }
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 27268);
        metrics.addCustomChart(new SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));
        metrics.addCustomChart(new SingleLineChart("servers", () -> 1));
    }

    private void startSchedulers() {
        Handler handler = new Handler();
        AuthHandler authHandler = new AuthHandler();
        authHandler.runTaskTimer(this, 0, 20);
        handler.runTaskTimer(this, 0, 1);
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
                        maxPool,
                        connTimeout,
                        idleTimeout,
                        maxLifetime,
                        jdbcParams
                );

                MySQLSchemaMigrator.migrate(mysqlPool.dataSource(), mysql.getString("db"));
                loader = new MySQLLoader(mysqlPool.dataSource());

                ConfigurationSection onceUsed = getConfig().getConfigurationSection("onceUsed");
                if (onceUsed != null && !onceUsed.getBoolean("mysql")) {
                    new MySQLMigrate(
                            mysql.getString("db"),
                            mysql.getString("host"),
                            mysql.getString("user"),
                            mysql.getString("pass")
                    );
                    getConfig().set("onceUsed.mysql", true);
                    saveConfig();
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "[AuthTG] Cannot init MySQL/Hikari: " + e.getMessage(), e);
                Bukkit.getPluginManager().disablePlugin(this);
            }

        } else {
            ConfigurationSection onceUsed = getConfig().getConfigurationSection("onceUsed");
            if (onceUsed != null && onceUsed.getBoolean("mysql")) {
                ConfigurationSection mysqlCfg = getConfig().getConfigurationSection("mysql");
                new YAMLMigrate(
                        mysqlCfg.getString("db"),
                        mysqlCfg.getString("host"),
                        mysqlCfg.getString("user"),
                        mysqlCfg.getString("pass")
                );
                getConfig().set("onceUsed.mysql", false);
                saveConfig();
            }

            loader = new YAMLLoader();
        }
    }

    private void stopTelegramBot() {
        if (botSession != null) {
            try {
                botSession.stop();
            } catch (Exception ignored) {
            }
            botSession = null;
        }

        if (bot != null) {
            try {
                bot.shutdown();
            } catch (Exception ignored) {
            }
            bot = null;
        }
    }

    private void initTelegramBot() {
        stopTelegramBot();

        DefaultBotOptions options = new DefaultBotOptions();
        options.setGetUpdatesTimeout(50);
        configureProxy(options);

        bot = new BotTelegram(
                getConfig().getString("bot.token"),
                getConfig().getString("bot.username"),
                options
        );

        if (!telegramEnabled) {
            logger.log(Level.INFO, "[AuthTG] Telegram integration is disabled in config.yml (tg: false)");
            return;
        }

        if ("changeme".equals(bot.getBotToken()) && "changeme".equals(bot.getBotUsername())) {
            logger.log(Level.INFO, "Please set your bot token and username in config.yml");
            return;
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botSession = botsApi.registerBot(bot);
            logger.log(Level.INFO, "[AuthTG] Telegram polling started");
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
        }
    }

    private void initVKBot() {
        vk = new BotVK(getConfig().getString("vk.token"), getConfig().getInt("vk.groupId"));
        vk.initializationBot();
    }

    private void configureProxy(DefaultBotOptions options) {
        ConfigurationSection proxy = getConfig().getConfigurationSection("bot.proxy");
        if (proxy == null || !proxy.getBoolean("enabled", false)) {
            return;
        }

        String host = proxy.getString("host", "").trim();
        int port = proxy.getInt("port", 0);
        String typeRaw = proxy.getString("type", "SOCKS5");
        String username = proxy.getString("username", "").trim();
        String password = proxy.getString("password", "");

        if (host.isEmpty() || port <= 0) {
            logger.log(Level.WARNING, "[AuthTG] bot.proxy.enabled=true, but host/port are invalid. Proxy ignored.");
            return;
        }

        DefaultBotOptions.ProxyType proxyType = parseProxyType(typeRaw);
        if (proxyType == null) {
            logger.log(Level.WARNING, "[AuthTG] Unknown proxy type: " + typeRaw + ". Use HTTP, SOCKS4 or SOCKS5.");
            return;
        }

        options.setProxyHost(host);
        options.setProxyPort(port);
        options.setProxyType(proxyType);

        if (!username.isEmpty()) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    if (host.equalsIgnoreCase(getRequestingHost()) && port == getRequestingPort()) {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                    return null;
                }
            });
        }

        logger.log(Level.INFO, "[AuthTG] Proxy enabled: " + proxyType + " " + host + ":" + port);
    }

    private DefaultBotOptions.ProxyType parseProxyType(String typeRaw) {
        if (typeRaw == null) {
            return DefaultBotOptions.ProxyType.SOCKS5;
        }

        String type = typeRaw.trim().toUpperCase(Locale.ROOT);
        switch (type) {
            case "HTTP":
                return DefaultBotOptions.ProxyType.HTTP;
            case "SOCKS4":
                return DefaultBotOptions.ProxyType.SOCKS4;
            case "SOCKS5":
                return DefaultBotOptions.ProxyType.SOCKS5;
            default:
                return null;
        }
    }

    private void registerCommands() {
        registerCommand("register", new RegisterCMD());
        registerCommand("login", new LoginCMD());
        registerCommand("code", new CodeCMD());
        registerCommand("2fa", new TwoFactorCodeCMD());
        registerCommand("prefer", new PreferCMD());
        registerCommand("captcha", new CaptchaCMD());

        registerCommand("mcbc", new MCbcCMD());
        registerCommand("tgbc", new TGbcCMD());
        registerCommand("changepassword", new ChangePasswordCMD());
        registerCommand("setpassword", new SetPasswordCMD());
        registerCommand("friend", new FriendCMD());
        registerCommand("setspawn", new SetSpawnCMD());
        registerCommand("admin", new AdminCMD());
        registerCommand("command", new CommandCMD());
        registerCommand("kick", new KickCMD());
        registerCommand("mute", new MuteCMD());
        registerCommand("ban", new BanCMD());
        registerCommand("unban", new UnBanCMD());
        registerCommand("unmute", new UnMuteCMD());
        registerCommand("logout", new LogoutCMD());
        registerCommand("unlink", new UnLinkCMD());
        registerCommand("authtg", new AuthTGCMD());
        registerCommand("mail", new MailCMD());
        registerCommand("vk", new VKCMD());
    }

    private void registerTabCompleters() {
        registerTabCompleter("admin", new AdminTabCompleter());
        registerTabCompleter("friend", new FriendTabCompleter());
        registerTabCompleter("command", new CommandTabCompleter());
        registerTabCompleter("ban", new BanTabCompleter());
        registerTabCompleter("mute", new MuteTabCompleter());
        registerTabCompleter("setspawn", new SetSpawnTabCompleter());
        registerTabCompleter("mail", new MailTabCompleter());
        registerTabCompleter("prefer", new PreferTabCompleter());
    }

    private void registerCommand(String name, CommandExecutor executor) {
        if (getCommand(name) == null) {
            logger.log(Level.SEVERE, "[AuthTG] Command not found in plugin.yml: " + name);
            return;
        }
        getCommand(name).setExecutor(executor);
    }

    private void registerTabCompleter(String name, TabCompleter completer) {
        if (getCommand(name) == null) {
            logger.log(Level.SEVERE, "[AuthTG] Command not found in plugin.yml: " + name);
            return;
        }
        getCommand(name).setTabCompleter(completer);
    }

    private void setupConfiguration() {
        File fileTemp = new File(getDataFolder(), "temp-config.yml");
        YamlConfiguration configTemp = YamlConfiguration.loadConfiguration(fileTemp);

        File fileGlobal = new File(getDataFolder(), "config.yml");
        YamlConfiguration configGlobal = YamlConfiguration.loadConfiguration(fileGlobal);

        Set<String> tempKeys = configTemp.getKeys(true);
        Set<String> globalKeys = configGlobal.getKeys(true);
        tempKeys.removeAll(globalKeys);

        for (String key : tempKeys) {
            configGlobal.set(key, configTemp.get(key));
        }

        try {
            configGlobal.save(fileGlobal);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't save config.yml", e);
        }

        fileTemp.delete();
    }

    private void setupMessages() {
        File fileTemp = new File(getDataFolder(), "temp-messages.yml");
        YamlConfiguration configTemp = YamlConfiguration.loadConfiguration(fileTemp);

        File fileGlobal = new File(getDataFolder(), "messages.yml");
        YamlConfiguration configGlobal = YamlConfiguration.loadConfiguration(fileGlobal);

        Set<String> tempKeys = configTemp.getKeys(true);
        Set<String> globalKeys = configGlobal.getKeys(true);
        tempKeys.removeAll(globalKeys);

        for (String key : tempKeys) {
            configGlobal.set(key, configTemp.get(key));
        }

        try {
            configGlobal.save(fileGlobal);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't save messages.yml", e);
        }

        fileTemp.delete();
    }

    private void loadMessagesConfiguration() {
        File file = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static String getMessage(String path, String channel) {
        if (messagesConfig == null) {
            instance.loadMessagesConfiguration();
        }

        if ("MC".equals(channel) || "CE".equals(channel)) {
            return messagesConfig.getString("messages.minecraft." + path, "").replace("{BR}", "\n");
        }

        if ("TG".equals(channel)) {
            return messagesConfig.getString("messages.telegram." + path, "").replace("{BR}", "\n");
        }
        if ("VK".equals(channel)) {
            return messagesConfig.getString("messages.vk." + path, "").replace("{BR}", "\n");
        }

        logger.log(Level.SEVERE, "Message path not found, please contact the developer");
        return "";
    }

    public static String getPlaceholderMessage(String placeholder, String path) {
        if (messagesConfig == null) {
            instance.loadMessagesConfiguration();
        }

        if ("none".equals(placeholder)) {
            return messagesConfig.getString("placeholders." + path, "");
        }

        if ("activetg".equals(placeholder) || "twofactor".equals(placeholder) || "status".equals(placeholder)) {
            return messagesConfig.getString("placeholders." + placeholder + "." + path, "");
        }

        logger.log(Level.SEVERE, "Placeholder path not found, please contact the developer");
        return "";
    }

    public static void loadConfigParameters() {
        ConfigurationSection config = getInstance().getConfig();

        telegramEnabled = config.getBoolean("tg", true);
        vkEnabled = config.getBoolean("vk.enabled", true);
        maxAccountTGCount = config.getInt("maxAccountTGCount");
        maxAccountVKCount = config.getInt("maxAccountVKCount");
        forbiddenNicknames = config.getStringList("forbiddenNicknames");
        notRegAndLogin = config.getBoolean("notRegAndLogin");
        authNecessarily = config.getBoolean("authNecessarily");
        authNecessarilyPrefer = config.getString("authNecessarilyPrefer");
        activeChatinTG = config.getBoolean("activeChatinTG");
        mutecommands = config.getStringList("mutecommands");
        commandsPreAuthorization = config.getStringList("commandsPreAuthorization");
        openimmediately = config.getBoolean("captcha.openimmediately");
        minLenghtNickname = config.getInt("minLenghtNickname");
        maxLenghtNickname = config.getInt("maxLenghtNickname");
        minLenghtPassword = config.getInt("minLenghtPassword");
        maxLenghtPassword = config.getInt("maxLenghtPassword");
        timeoutSession = config.getInt("timeoutSession");
        kickTimeout = config.getInt("kickTimeout");
        locationX = config.getDouble("spawn.x");
        locationY = config.getDouble("spawn.y");
        locationZ = config.getDouble("spawn.z");
        world = config.getString("spawn.world");
        macro = config.getConfigurationSection("macro");
        ipregmax = config.getInt("ipregmax");
    }
}