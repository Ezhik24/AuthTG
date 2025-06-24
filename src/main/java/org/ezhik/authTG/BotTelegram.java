package org.ezhik.authTG;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ezhik.authTG.calbackQuery.AccAccounts;
import org.ezhik.authTG.calbackQuery.CallbackQueryHandler;
import org.ezhik.authTG.calbackQuery.LoginAcceptedNo;
import org.ezhik.authTG.calbackQuery.LoginAcceptedYes;
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
    private String username = "changeme";
    private String token = "changeme";
    private Map<String, CommandHandler> commandHandler = new HashMap<>();
    private Map<Long, NextStepHandler> nextStepHandler = new HashMap<>();
    private Map<String, CallbackQueryHandler> callbackQueryHandler = new HashMap<>();

    public BotTelegram() {
        File file = new File("plugins/AuthTG/botconf.yml");
        YamlConfiguration botconf = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            botconf.set("username", username);
            botconf.set("token", token);
            try {
                botconf.save(file);
            } catch (IOException e) {
                System.out.println("Error saving file: " +e);
            }
        } else {
            username = botconf.getString("username");
            token = botconf.getString("token");
        }
        commandHandler.put("/start", new StartCMDHandler());
        commandHandler.put("/tfon" , new TFonCMDHandler());
        commandHandler.put("/tfoff", new TFoffCMDHandler());
        commandHandler.put("/accounts", new AccountsCMDHandler());
        commandHandler.put("/unlink", new UnLinkCMDHandler());
        callbackQueryHandler.put("ys" , new LoginAcceptedYes());
        callbackQueryHandler.put("no", new LoginAcceptedNo());
        callbackQueryHandler.put("acc", new AccAccounts());
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
            if (command.startsWith("/")) {
                if (commandHandler.containsKey(command)) commandHandler.get(command).execute(update);
            } else {
                if (nextStepHandler.containsKey(update.getMessage().getChatId())) nextStepHandler.get(chatid).execute(update);
            }
            this.deleteMessage(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            String[] str = update.getCallbackQuery().getData().toString().split("_");
            callbackQueryHandler.get(str[0]).execute(update, UUID.fromString(str[1]));
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

}
