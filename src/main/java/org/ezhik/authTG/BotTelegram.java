package org.ezhik.authTG;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.ezhik.authTG.calbackQuery.*;
import org.ezhik.authTG.commandTG.*;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.nextStep.NextStepHandler;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class BotTelegram extends TelegramLongPollingBot {

    private final String username;
    private final String token;

    private final Map<String, CommandHandler> commandHandler = new ConcurrentHashMap<>();
    private final Map<String, UUID> userData = new ConcurrentHashMap<>();
    private final Map<Long, NextStepHandler> nextStepHandler = new ConcurrentHashMap<>();
    private final Map<String, CallbackQueryHandler> callbackQueryHandler = new ConcurrentHashMap<>();
    private final ExecutorService telegramIoExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "AuthTG-TelegramIO");
        thread.setDaemon(true);
        return thread;
    });

    public BotTelegram(String token, String username, DefaultBotOptions options) {
        super(options);
        this.username = username;
        this.token = token;

        commandHandler.put("/resetpassword", new ResetPasswordCMDHandler());
        //commandHandler.put("/start", new StartCMDHandler());
        commandHandler.put("/link", new StartCMDHandler());
        commandHandler.put("/tfon", new TFonCMDHandler());
        commandHandler.put("/tfoff", new TFoffCMDHandler());
        commandHandler.put("/accounts", new AccountsCMDHandler());
        commandHandler.put("/unlink", new UnLinkCMDHandler());
        commandHandler.put("/kickme", new KickMeCMDHandler());
        commandHandler.put("/friends", new FriendCMDHandler());
        commandHandler.put("/kick", new KickCMDHandler());
        commandHandler.put("/ban", new BanCMDHandler());
        commandHandler.put("/mute", new MuteCMDHandler());
        commandHandler.put("/command", new CommandCMDHandler());
        commandHandler.put("/unban", new UnbanCMDHandler());
        commandHandler.put("/unmute", new UnmuteCMDHandler());

        callbackQueryHandler.put("ys", new LoginAcceptedYes());
        callbackQueryHandler.put("no", new LoginAcceptedNo());
        callbackQueryHandler.put("acc", new AccAccounts());
        callbackQueryHandler.put("addfrys", new FriendYes());
        callbackQueryHandler.put("addfrno", new FriendNo());
        callbackQueryHandler.put("chfr", new ActsFriend());
        callbackQueryHandler.put("delfr", new DelFriends());
        callbackQueryHandler.put("sndtg", new SendMessageTG());
        callbackQueryHandler.put("sndmc", new SendMessageMC());
        callbackQueryHandler.put("cmdfirst", new CMDFirstStep());
        callbackQueryHandler.put("cmdsecond", new CMDSecondStep());

        ConfigurationSection section = AuthTG.macro;
        if (section != null) {
            for (String key : section.getKeys(false)) {
                commandHandler.put("/" + key,
                        new MacroCMDHandler(section.getString(key + ".mccmd"), section.getString(key + ".nsmsg")));
            }
        }
    }

    private boolean isTelegramEnabled() {
        return AuthTG.isTelegramEnabled();
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
        if (!isTelegramEnabled()) return;

        try {
            if (update.hasMessage() && update.getMessage() != null && update.getMessage().hasText()) {
                handleMessage(update);
            }

            if (update.hasCallbackQuery() && update.getCallbackQuery() != null) {
                handleCallback(update);
            }
        } catch (Exception e) {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] onUpdateReceived error: " + e.getMessage(), e);
        }
    }

    private void handleMessage(Update update) {
        Long chatid = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (chatid == null || text == null) return;

        if (nextStepHandler.containsKey(chatid) && !text.startsWith("/")) {
            NextStepHandler h = nextStepHandler.get(chatid);
            if (h != null) h.execute(update);
            deleteMessage(update.getMessage());
            return;
        }

        if (text.startsWith("/")) {
            if (nextStepHandler.containsKey(chatid)) nextStepHandler.remove(chatid);

            String[] str = text.split(" ");
            CommandHandler h = commandHandler.get(str[0]);
            if (h != null) h.execute(update);

            deleteMessage(update.getMessage());
            return;
        }

        if (text.startsWith("#")) {
            UUID current = AuthTG.loader.getCurrentUUID(chatid);
            User user = (current != null) ? User.getUser(current) : null;

            String message = text.substring(1);

            if (user != null && user.activetg) {
                if (user.player != null) {
                    Handler.sendMCmessage(user.playername, message);
                } else {
                    String mcMessage = ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("chatminecraft", "MC")
                                    .replace("{PLAYER}", user.playername)
                                    .replace("{MESSAGE}", message));

                    Bukkit.getScheduler().runTask(AuthTG.getInstance(), () ->
                            Bukkit.broadcastMessage(mcMessage)
                    );
                }
            } else {
                this.sendMessage(chatid, AuthTG.getMessage("chatminecraftnotactive", "TG"));
            }

            deleteMessage(update.getMessage());
            return;
        }

        if (AuthTG.activeChatinTG) {
            UUID current = AuthTG.loader.getCurrentUUID(chatid);
            User user = (current != null) ? User.getUser(current) : null;

            if (user != null && user.activetg) {
                Set<Long> chats = AuthTG.loader.getChatID();
                if (chats != null) {
                    for (Long s : chats) {
                        if (s != null && s > 0) {
                            this.sendMessage(s,
                                    AuthTG.getMessage("chatmessage", "TG")
                                            .replace("{PLAYER}", user.playername)
                                            .replace("{MESSAGE}", text));
                        }
                    }
                }
            } else {
                this.sendMessage(chatid, AuthTG.getMessage("chatmsgusernotactive", "TG"));
            }
        }

        deleteMessage(update.getMessage());
    }

    private void handleCallback(Update update) {
        String data = update.getCallbackQuery().getData();
        if (data == null) return;

        answerCallback(update.getCallbackQuery().getId(), null);

        String[] str = data.split("_");
        if (str.length == 0) return;

        CallbackQueryHandler h = callbackQueryHandler.get(str[0]);
        if (h != null) {
            h.execute(update);
        } else {
            AuthTG.logger.log(Level.WARNING, "[AuthTG] Unknown callback: " + str[0]);
        }
    }

    public void sendMessage(Long chatId, String message) {
        if (!isTelegramEnabled()) return;
        if (chatId == null || chatId <= 0) return;
        if (message == null) message = "";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        executeAsync(sendMessage);
    }

    public void executeAsync(SendMessage sendMessage) {
        if (!isTelegramEnabled()) return;
        if (sendMessage == null) return;
        telegramIoExecutor.execute(() -> executeSendMessageNow(sendMessage));
    }

    private void executeSendMessageNow(SendMessage sendMessage) {
        if (!isTelegramEnabled()) return;

        try {
            execute(sendMessage);

        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == 400
                    && e.getApiResponse() != null
                    && e.getApiResponse().contains("chat not found")) {

                Long parsedChatId = parseChatId(sendMessage.getChatId());

                if (parsedChatId != null) {
                    try {
                        List<UUID> uuids = AuthTG.loader.getPlayerNames(parsedChatId);
                        if (uuids != null) {
                            for (UUID u : uuids) {
                                AuthTG.loader.setActiveTG(u, false);
                                AuthTG.loader.setChatID(u, 0L);
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    AuthTG.logger.log(Level.WARNING,
                            "[AuthTG] Telegram chatId " + parsedChatId + " not found. Disabled TG for this chatId.");
                }
                return;
            }

            AuthTG.logger.log(Level.WARNING,
                    "[AuthTG] Telegram sendMessage error: " + e.getErrorCode() + " " + e.getApiResponse());

        } catch (TelegramApiException e) {
            AuthTG.logger.log(Level.WARNING,
                    "[AuthTG] Telegram sendMessage error: " + e.getMessage(), e);
        }
    }

    public void deleteMessage(Message message) {
        if (!isTelegramEnabled()) return;
        if (message == null) return;
        telegramIoExecutor.execute(() -> deleteMessageNow(message));
    }

    private void deleteMessageNow(Message message) {
        if (!isTelegramEnabled()) return;

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());

        try {
            execute(deleteMessage);
        } catch (TelegramApiRequestException e) {
            AuthTG.logger.log(Level.FINE,
                    "[AuthTG] deleteMessage: " + e.getErrorCode() + " " + e.getApiResponse());
        } catch (TelegramApiException e) {
            AuthTG.logger.log(Level.FINE,
                    "[AuthTG] deleteMessage error: " + e.getMessage());
        }
    }

    public void answerCallback(String callbackQueryId, String text) {
        if (!isTelegramEnabled()) return;
        if (callbackQueryId == null || callbackQueryId.isBlank()) return;

        telegramIoExecutor.execute(() -> {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQueryId);
            if (text != null && !text.isBlank()) {
                answer.setText(text);
                answer.setShowAlert(false);
            }

            try {
                execute(answer);
            } catch (TelegramApiException e) {
                AuthTG.logger.log(Level.FINE,
                        "[AuthTG] answerCallback error: " + e.getMessage());
            }
        });
    }

    private Long parseChatId(String chatId) {
        if (chatId == null || chatId.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(chatId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setNextStepHandler(Long chatid, NextStepHandler nextStepHandler) {
        if (!isTelegramEnabled()) return;
        if (chatid == null || nextStepHandler == null) return;
        this.nextStepHandler.put(chatid, nextStepHandler);
    }

    public void remNextStepHandler(Long chatid) {
        if (!isTelegramEnabled()) return;
        if (chatid == null) return;
        this.nextStepHandler.remove(chatid);
    }

    public void setUserData(String username, UUID data) {
        if (!isTelegramEnabled()) return;
        if (username == null || data == null) return;
        this.userData.put(username, data);
    }

    public void remUserData(String username) {
        if (!isTelegramEnabled()) return;
        if (username == null) return;
        this.userData.remove(username);
    }

    public UUID getUserData(String username) {
        if (!isTelegramEnabled()) return null;
        if (username == null) return null;
        return userData.get(username);
    }

    public void shutdown() {
        nextStepHandler.clear();
        userData.clear();
        callbackQueryHandler.clear();
        telegramIoExecutor.shutdownNow();
    }
}
