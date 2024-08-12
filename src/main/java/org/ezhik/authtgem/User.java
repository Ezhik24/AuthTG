package org.ezhik.authtgem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.ezhik.authtgem.commands.CodeCMD;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class User {
    public Long chatid = null;
    public String username = null;
    public String firstname = null;
    public String lastname = null;
    public boolean active = false;
    public boolean twofactor = false;
    public Player player = null;
    public  UUID uuid = null;
    public String playername = "";
    public List<String> friends = new ArrayList<>();

    private User(UUID uuid) {
        YamlConfiguration userconfig = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + uuid + ".yml");
        try {
            userconfig.load(file);
            this.uuid = uuid;
            this.playername = userconfig.getString("playername");
            if(playername == null) playername = "";
            this.chatid = userconfig.getLong("ChatID");
            this.username = userconfig.getString("username");
            this.firstname = userconfig.getString("firstname");
            this.lastname = userconfig.getString("lastname");
            this.twofactor = userconfig.getBoolean("twofactor");
            this.player = Bukkit.getPlayer(uuid);
            this.active = userconfig.getBoolean("active");
            this.friends = userconfig.getStringList("friends");
        } catch (FileNotFoundException e) {
            System.out.println("Error file not found: " + e);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error loading config file: " + e);
        }
    }

    public static String generateConfirmationCode() {
        Random random = new Random();
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            code.append(characters.charAt(randomIndex));
        }
        return code.toString();
    }
    public static void register(Message message, UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        YamlConfiguration userconfig = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + p.getUniqueId() + ".yml");
        try {
            userconfig.load(file);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        userconfig.set("ChatID", message.getChatId());
        userconfig.set("username", message.getChat().getUserName());
        userconfig.set("firstname", message.getChat().getFirstName());
        userconfig.set("lastname", message.getChat().getLastName());
        userconfig.set("active", false);
        userconfig.set("twofactor", true);


        try {
            userconfig.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }

        File oldfile = new File("plugins/Minetelegram/users/" + message.getChatId().toString() + ".yml");
        oldfile.delete();
        String code = generateConfirmationCode();
        AuthTGEM.bot.sendMessage(message.getChatId(), "[Бот] В игре выполните команду /code " + code + " что бы привязать аккаунт.");
        p.sendMessage(ChatColor.RED + "[MT] Выполните команду /code (из телеграмма). Если это не вы, то проигнорируйте это сообщение.");
        CodeCMD.code.put(p.getUniqueId(), code);

    }

    public static void starcmd(Message message) {
        AuthTGEM.bot.sendMessage(message.getChatId(), "[Бот] Выполните следующие пункты:\n1.Войдите в игру.\n2.Авторизуйтесь.\n3.Напишите свой никнейм.");
    }

    public static boolean isNickname(String nickname){
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getName().equals(nickname)){
                return true;
            }
        }
        return false;
    }



    public void sendMessage(String message) {
        AuthTGEM.bot.sendMessage(this.chatid, "[Бот@" + this.playername + "] " + message);
    }




    public static List<User> getUserList(){
        List<User> users = new ArrayList<User>();
        File folder = new File("plugins/Minetelegram/users");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            System.out.println(file.getName());
            if (file.isFile()) {
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                User user = new User(uuid);
                if (user.active) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    public static List<User> getTFAUsers(){
        List<User> users = User.getUserList();
        for (User user : users){
            if (user != null) {
                if (user.twofactor) {
                    users.add(user);
                }
            }
        }
        return users;
    }
    public void sendLoginAccepted(String message) {
        InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> colkeyb = new ArrayList<>();
        InlineKeyboardButton yesbtn = new InlineKeyboardButton();
        yesbtn.setText("Да");
        yesbtn.setCallbackData("ys"+this.player.getName());
        InlineKeyboardButton nobtn = new InlineKeyboardButton();
        nobtn.setText("Нет");
        nobtn.setCallbackData("no"+this.player.getName());
        colkeyb.add(yesbtn);
        colkeyb.add(nobtn);
        List<List<InlineKeyboardButton>>keyboard = new ArrayList<>();
        keyboard.add(colkeyb);
        keyb.setKeyboard(keyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(this.chatid);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(keyb);
        try {
            AuthTGEM.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }

    }

    public static User getUser(UUID uuid) {
        User user = new User(uuid);
        if (user.active) {
            return user;
        }
        else return null;
    }

    public static User getUser(String playername){
        List<User> users = new ArrayList<>();
        for (User user : User.getUserList()){
            if (user.playername.equals(playername)){
                if(user.active) return user;
            }
        }
        return null;

    }

    public void kick(){
        Handler.kick(this.player.getName(), "Владелец кикнул аккаунт через телеграмм");
    }

    public void resetpassword() {
        YamlConfiguration userconf = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + this.player.getUniqueId() + ".yml");
        String password = generateConfirmationCode();
        try {
            userconf.load(file);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        userconf.set("password", PasswordHasher.hashPassword(password));
        try {
            userconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }
        this.sendMessage(" Ваш пароль был изменен на " + password + ". \nОбязательно смените пароль,после захода на сервер!\nКоманда для смены пароля: /cp");

    }

    public void setTwofactor(boolean state) {
        YamlConfiguration userconf = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + this.player.getUniqueId() + ".yml");
        try {
            userconf.load(file);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        userconf.set("twofactor", state);
        try {
            userconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }

        if(state){
            this.sendMessage("Двухфакторная авторизация успешно включена");
        }
        else{
            this.sendMessage("Двухфакторная авторизация успешно выключена");
        }
    }

    public static void sendBroadcastMessage(String message) {
        for (User user : User.getUniclUsers()) {
            AuthTGEM.bot.sendMessage(user.chatid, "[Бот]" + message);
        }
    }

    public void unlink(){
        String code = generateConfirmationCode();
        this.sendMessage("Выполните в игре команду: /code " + code + " что бы отвязать аккаунт.");
        this.player.sendMessage( ChatColor.RED + "[MT] Выполните в игре команду: /code <код из телеграмма> что бы отвязать аккаунт.");
        CodeCMD.code.put(this.player.getUniqueId(), code);

    }

    public static List<String> getPlayerNames(Long chatid) {
        List<String> names = new ArrayList<>();
        for (User user : User.getUserList()) {
            if (user != null)
                if (user.chatid.equals(chatid)) {
                    names.add(user.playername);
                }
        }
        return names;
    }

    public static User getOnlineUser(Long chatid) {
        if (BotTelegram.curentplayer.containsKey(chatid.toString())) {
            Player player = Bukkit.getPlayer(BotTelegram.curentplayer.get(chatid.toString()));
            if (player != null) {
                return User.getUser(player.getUniqueId());
            }
        }
        List<String> players = getPlayerNames(chatid);
        for (User user : getUserList()){
           if(user.player != null){
               if (players.contains(user.player.getName())){
                   BotTelegram.curentplayer.put(chatid.toString(), user.playername);
                   return user;
               }
           }
        }
        return null;
    }

    public static User getCurrentUser(Long chatid){
        if (BotTelegram.curentplayer.containsKey(chatid.toString())) {
            return User.getUser(BotTelegram.curentplayer.get(chatid.toString()));
        } else {
            for (User user : User.getUserList()) {
                if (user.chatid.equals(chatid)) {
                    BotTelegram.curentplayer.put(chatid.toString(), user.playername);
                    return user;
                }
            }
            return null;
        }
    }

    public static List<User> getUniclUsers() {
        List<User> users = new ArrayList<>();
        List<Long> chatIds = new ArrayList<>();
        for (User user : User.getUserList()) {
            if (user != null) {
                if (!chatIds.contains(user.chatid)) {
                    chatIds.add(user.chatid);
                    users.add(user);
                }
            }
        }
        return users;
    }
    public void addfriend(String friendname) {
        this.friends.add(friendname);
        YamlConfiguration userconf = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + this.player.getUniqueId() + ".yml");
        try {
            userconf.load(file);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        userconf.set("friends", this.friends);
        try {
            userconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }

    }

    public void remFriendFromConf(String friendname) {
        this.friends.remove(friendname);
        YamlConfiguration userconf = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/users/" + this.uuid + ".yml");
        try {
            userconf.load(file);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e);
        } catch (InvalidConfigurationException e) {
            System.out.println("Error parsing config file: " + e);
        }
        userconf.set("friends", this.friends);
        try {
            userconf.save(file);
        } catch (IOException e) {
            System.out.println("Error saving config file: " + e);
        }
    }
    public List<User> getUnicFriends() {
        List<User> users = new ArrayList<>();
        List<Long> chatIds = new ArrayList<>();
        for(String friend : this.friends){
            User user = User.getUser(friend);
            if(user != null) {
                if (!chatIds.contains(user.chatid) && user.chatid != this.chatid) {
                    chatIds.add(user.chatid);
                    users.add(user);
                }
            }
        }
        return users;
    }
    public static String getplayerstatus(String playername){
        User user = User.getUser(playername);
        if(user.player != null){
            return " [Online]";
        }
        else{
            return " [Offline]";
        }
    }
    public String remFriend(String friendname) {
        if (!this.friends.contains(friendname)){
            return "Такого игрока нет в друзьях";
        }else{
            this.remFriendFromConf(friendname);
            User frienduser = User.getUser(friendname);
            if (frienduser != null) {
                frienduser.remFriendFromConf(this.playername);
                if (frienduser.player != null) player.sendMessage(ChatColor.GREEN + "[MT] " + this.player.getName() + " удалил вас из друзей");
                frienduser.sendMessage(this.player.getName() + " удалил вас из друзей");
            }
            return ChatColor.RED + "[MT] Вы удалили " + friendname + " из друзей";
        }
    }
    public void sendMessageB(String message, String friend) {
        InlineKeyboardMarkup playerKB = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> colkeyb = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowkeyb = new ArrayList<>();
        InlineKeyboardButton acts = new InlineKeyboardButton();
        acts.setText("Действия");
        acts.setCallbackData("chfr" + friend);
        colkeyb.add(acts);
        rowkeyb.add(colkeyb);
        playerKB.setKeyboard(rowkeyb);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(this.chatid);
        sendMessage.setReplyMarkup(playerKB);
        try {
            AuthTGEM.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }
}
