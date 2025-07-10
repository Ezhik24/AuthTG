package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authTG.calbackQuery.*;
import org.ezhik.authTG.commandTG.*;
import org.ezhik.authTG.nextStep.NextStepHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BotTelegram extends TelegramLongPollingBot {
    private String username;
    private String token;
    private Map<String, CommandHandler> commandHandler = new HashMap<>();
    private Map<String, UUID> userData = new HashMap<>();
    private Map<Long, NextStepHandler> nextStepHandler = new HashMap<>();
    private Map<String, CallbackQueryHandler> callbackQueryHandler = new HashMap<>();
    public boolean BOT_IS_STARTED = false;

    public BotTelegram(String token, String username) {
        this.username = username;
        this.token = token;
        commandHandler.put("/resetpassword", new ResetPasswordCMDHandler());
        commandHandler.put("/start", new StartCMDHandler());
        commandHandler.put("/link", new StartCMDHandler());
        commandHandler.put("/tfon", new TFonCMDHandler());
        commandHandler.put("/tfoff", new TFoffCMDHandler());
        commandHandler.put("/accounts", new AccountsCMDHandler());
        commandHandler.put("/unlink", new UnLinkCMDHandler());
        commandHandler.put("/kickme", new KickMeCMDHandler());
        commandHandler.put("/friends", new FriendCMDHandler());
        callbackQueryHandler.put("ys", new LoginAcceptedYes());
        callbackQueryHandler.put("no", new LoginAcceptedNo());
        callbackQueryHandler.put("acc", new AccAccounts());
        callbackQueryHandler.put("addfrys", new FriendYes());
        callbackQueryHandler.put("addfrno", new FriendNo());
        callbackQueryHandler.put("chfr", new ActsFriend());
        callbackQueryHandler.put("delfr", new DelFriends());
        callbackQueryHandler.put("sndtg", new SendMessageTG());
        callbackQueryHandler.put("sndmc", new SendMessageMC());
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatid = update.getMessage().getChatId();
            String command = update.getMessage().getText().toString();
            if (nextStepHandler.containsKey(update.getMessage().getChatId())) nextStepHandler.get(chatid).execute(update);
            else if (command.startsWith("/")) {
                if (commandHandler.containsKey(command)) commandHandler.get(command).execute(update);
            } else if (command.startsWith("#")) {
                User user = User.getUser(AuthTG.loader.getCurrentUUID(chatid));
                String message = command.substring(1);
                if (user.activetg) {
                    if (user.player != null) Handler.sendMCmessage(user.playername, message);
                    else Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.config.getString("messages.telegram.chatminecraft").replace("{PLAYER}", user.playername).replace("{MESSAGE}", message)));
                } else this.sendMessage(chatid, AuthTG.config.getString("messages.telegram.chatminecraftnotactive"));
            } else {
                User user = User.getUser(AuthTG.loader.getCurrentUUID(chatid));
                if (user.activetg) {
                    for (Long s : AuthTG.loader.getChatID()) {
                        this.sendMessage(s, AuthTG.config.getString("messages.telegram.chatmessage").replace("{PLAYER}", user.playername).replace("{MESSAGE}", update.getMessage().getText().toString()));
                    }
                } else {
                    this.sendMessage(chatid, AuthTG.config.getString("messages.telegram.chatmsgusernotactive"));
                }
            }
            this.deleteMessage(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            String[] str = update.getCallbackQuery().getData().toString().split("_");
            callbackQueryHandler.get(str[0]).execute(update);
        }
    }

    public void sendMessage(Long Chatid, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(Chatid);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }
    }

    public void deleteMessage(Message message) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error deleting message: " + e);
        }
    }

    public void setNextStepHandler(Long chatid, NextStepHandler nextStepHandler) {
        this.nextStepHandler.put(chatid, nextStepHandler);
    }

    public void remNextStepHandler(Long chatid) {
        this.nextStepHandler.remove(chatid);
    }

    public void setUserData(String username, UUID data) {
        this.userData.put(username, data);
    }
    public void remUserData(String username) {
        this.userData.remove(username);
    }
    public UUID getUserData(String username) {
        if (userData.containsKey(username)) {
            return userData.get(username);
        }
        return null;
    }

}
