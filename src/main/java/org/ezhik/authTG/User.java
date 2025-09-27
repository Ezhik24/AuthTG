package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ezhik.authTG.commandMC.CodeCMD;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class User {
    public Long chatid;
    public String username;
    public String firstname;
    public String lastname;
    public boolean active;
    public boolean activetg;
    public boolean twofactor;
    public Player player;
    public  UUID uuid;
    public String playername;
    public List<String > friends;
    public boolean isadmin;
    public Set<String> commands;

    private User(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.twofactor = AuthTG.loader.getTwofactor(uuid);
        this.playername = AuthTG.loader.getPlayerName(uuid);
        this.active = AuthTG.loader.isActive(uuid);
        this.activetg = AuthTG.loader.getActiveTG(uuid);
        this.firstname = AuthTG.loader.getFirstName(uuid);
        this.lastname = AuthTG.loader.getLastName(uuid);
        this.username = AuthTG.loader.getUserName(uuid);
        this.friends = AuthTG.loader.getListFriends(uuid);
        this.chatid = AuthTG.loader.getChatID(uuid);
        this.isadmin = AuthTG.loader.isAdmin(uuid);
        this.commands = AuthTG.loader.getCommands(uuid);
    }
    // Generate a random confirmation code
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

    public static User getUser(UUID uuid) {
        User user = new User(uuid);
        if (user.active) {
            return user;
        }
        else return null;
    }

    public static User getUser(String playername) {
        UUID uuid = AuthTG.loader.getUUIDbyPlayerName(playername);
        if (uuid != null) {
            User user = getUser(uuid);
            return user;
        } else return null;
    }

    public static User getCurrentUser(Long chatid) {
        UUID uuid = AuthTG.loader.getCurrentUUID(chatid);
        if (uuid == null) {
            return null;
        } else {
            User user = getUser(uuid);
            return user;
        }
    }

    public static void register(Message message, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        AuthTG.loader.setChatID(uuid, message.getChatId());
        AuthTG.loader.setUsername(uuid, message.getChat().getUserName());
        AuthTG.loader.setFirstName(uuid, message.getChat().getFirstName());
        AuthTG.loader.setLastName(uuid, message.getChat().getLastName());
        AuthTG.loader.setTwofactor(uuid, true);
        AuthTG.loader.setActiveTG(uuid, false);
        AuthTG.loader.setCurrentUUID(uuid, message.getChatId());
        AuthTG.loader.setPlayerNames(message.getChatId(), uuid);
        String code = generateConfirmationCode();
        AuthTG.bot.sendMessage(message.getChatId(), AuthTG.config.getString("messages.telegram.codemsgactivated").replace("{CODE}", code));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.codemsgactivated")));
        CodeCMD.code.put(uuid, code);
    }

    public void sendMessage(String message) {
        AuthTG.bot.sendMessage(this.chatid, AuthTG.config.getString("messages.telegram.prefix").replace("{PLAYER}", this.playername) + message);
    }

    public void sendLoginAccepted(String message) {
        InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> colkeyb = new ArrayList<>();
        InlineKeyboardButton yesbtn = new InlineKeyboardButton();
        yesbtn.setText(AuthTG.config.getString("messages.telegram.yesbutton"));
        yesbtn.setCallbackData("ys_" + this.uuid);
        InlineKeyboardButton nobtn = new InlineKeyboardButton();
        nobtn.setText(AuthTG.config.getString("messages.telegram.nobutton"));
        nobtn.setCallbackData("no_" + this.uuid);
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
            AuthTG.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }

    }

    public static void sendBroadcastMessage(String message) {
        for (Long chatid : AuthTG.loader.getChatID()) {
            AuthTG.bot.sendMessage(chatid, AuthTG.config.getString("messages.telegram.broadcast") + message);
        }
    }

    public void sendMessageFriend(String message, UUID friend) {
        InlineKeyboardMarkup playerkeyb = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> colkeyb = new ArrayList<>();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton acts = new InlineKeyboardButton();
        acts.setText(AuthTG.config.getString("messages.telegram.msgfriendbutton"));
        acts.setCallbackData("chfr_" + friend);
        colkeyb.add(acts);
        keyboard.add(colkeyb);
        playerkeyb.setKeyboard(keyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(AuthTG.config.getString("messages.telegram.msgfriend").replace("{PLAYER}", this.playername) + message);
        sendMessage.setChatId(this.chatid);
        sendMessage.setReplyMarkup(playerkeyb);
        try {
            AuthTG.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }
    }
}
