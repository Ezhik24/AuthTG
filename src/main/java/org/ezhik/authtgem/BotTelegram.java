package org.ezhik.authtgem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BotTelegram extends TelegramLongPollingBot {
    private String username = "changeme";
    private String token = "changeme";
    private static Map<String, String> nextStep = new HashMap<>();
    private static Map<String, UUID> playerUUID = new HashMap<>();
    private Map<String, String> sendMessageData = new HashMap<>();
    public static Map<String, String> curentplayer = new HashMap<>();


    public BotTelegram() {
        YamlConfiguration config = new YamlConfiguration();
        File file = new File("plugins/Minetelegram/config.yml");
        if (!file.exists()) {
            config.set("username", username);
            config.set("token", token);
            try {
                config.save(file);
            } catch (Exception e) {
                System.out.println("Error creating config file: " + e);
            }
        } else {
            try {
                config.load(file);
            } catch (IOException e) {
                System.out.println("Error loading config file: " + e);
            } catch (InvalidConfigurationException e) {
                System.out.println("Error loading config file: " + e);
            }
            username = config.getString("username");
            token = config.getString("token");
        }


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
            if (update.getMessage().getText().toString().startsWith("/")) {
                if (update.getMessage().getText().toString().equals("/start") || update.getMessage().getText().toString().equals("/link")) {
                    User.starcmd(update.getMessage());
                    nextStep.put(update.getMessage().getChatId().toString(), "askplayername");
                }
                if (update.getMessage().getText().toString().equals("/kickme")) {
                  User user = User.getOnlineUser(update.getMessage().getChatId());
                  if (user != null) {
                      user.kick();
                      user.sendMessage(" Вы успешно кикнули свой аккаунт через телеграми!");
                  } else this.sendMessage(update.getMessage().getChatId(), "[Бот] Пользователь не зарегистрирован или он не в игре");
                }

                if (update.getMessage().getText().toString().equals("/unlink")) {
                   User user = User.getOnlineUser(update.getMessage().getChatId());
                   if (user == null) this.sendMessage(update.getMessage().getChatId(), "[Бот] Зайди в игру и попробуй еще раз");
                   else user.unlink();
                }
                if (update.getMessage().getText().toString().equals("/resetpassword")) {
                    User user = User.getOnlineUser(update.getMessage().getChatId());
                    if (user != null) user.resetpassword();
                    else this.sendMessage(update.getMessage().getChatId(), "[Бот] Пользователь не зарегистрирован или он не в игре");
                }
                if (update.getMessage().getText().toString().equals("/tfoff")) {
                    User user = User.getOnlineUser(update.getMessage().getChatId());
                    if (user != null) user.setTwofactor(false);
                    else this.sendMessage(update.getMessage().getChatId(), "[Бот] Пользователь не зарегистрирован или он не в игре");
                }
                if (update.getMessage().getText().toString().equals("/tfon")) {
                    User user = User.getOnlineUser(update.getMessage().getChatId());
                    if (user != null) user.setTwofactor(true);
                    else this.sendMessage(update.getMessage().getChatId(), "[Бот] Пользователь не зарегистрирован или он не в игре");
                }

                if (update.getMessage().getText().toString().equals("/accounts")) {
                    this.chosePlayer(update.getMessage().getChatId());
                }
                if (update.getMessage().getText().toString().equals("/friends")) {
                    this.showFriendsList(update.getMessage());
                }

            } else {
                if (nextStep.containsKey(update.getMessage().getChatId().toString())) {
                    if (nextStep.get(update.getMessage().getChatId().toString()).equals("askpassword")) {
                        String password = update.getMessage().getText().toString().replace(" ", "").replace("\n", "");
                        String hash = PasswordHasher.hashPassword(password);
                        YamlConfiguration userconfig = new YamlConfiguration();
                        File file = new File("plugins/Minetelegram/users/" + playerUUID.get(update.getMessage().getChatId().toString()) + ".yml");
                        try {
                            userconfig.load(file);
                        } catch (IOException e) {
                            System.out.println("Error loading config file: " + e);
                        } catch (InvalidConfigurationException e) {
                            System.out.println("Error parsing config file: " + e);
                        }
                        if (hash.equals(userconfig.getString("password"))) {
                            User.register(update.getMessage(), playerUUID.get(update.getMessage().getChatId().toString()));
                            nextStep.put(update.getMessage().getChatId().toString(), "none");
                        } else {
                            this.sendMessage(update.getMessage().getChatId(), "[Бот] Неверный пароль, повторите попытку");
                        }
                        this.deleteMessage(update.getMessage());
                    }
                    if (nextStep.get(update.getMessage().getChatId().toString()).equals("askplayername")) {
                        if (User.isNickname(update.getMessage().getText().toString())) {
                            Player player = Bukkit.getPlayer(update.getMessage().getText().toString());
                            UUID uuid = player.getUniqueId();
                            User user = User.getUser(uuid);
                            if (user != null) {
                                if (user.chatid.equals(update.getMessage().getChatId())) {
                                    this.sendMessage(update.getMessage().getChatId(), "[Бот] Вы уже привязывали эту учетную запись");
                                } else {
                                    this.sendMessage(update.getMessage().getChatId(), "[Бот] Эта учетная запись уже привязана к другой учетной записи Телегримма");
                                }
                            } else {
                                playerUUID.put(update.getMessage().getChatId().toString(), uuid);
                                this.sendMessage(update.getMessage().getChatId(), "[Бот] Введите пароль от аккаунта");
                                nextStep.put(update.getMessage().getChatId().toString(), "askpassword");
                            }
                        }
                    }
                    if (nextStep.get(update.getMessage().getChatId().toString()).equals("sendmsg")) {
                        nextStep.put(update.getMessage().getChatId().toString(), "none");
                        this.deleteMessage(update.getMessage());
                        User senderuser = User.getCurrentUser(update.getMessage().getChatId());
                        User frienduser = User.getUser(sendMessageData.get(update.getMessage().getChatId().toString()));
                        frienduser.sendMessage("Сообщение от пользователя " + senderuser.playername + ": " + update.getMessage().getText().toString());
                    }
                    if (nextStep.get(update.getMessage().getChatId().toString()).equals("sendmcmsg")) {
                        nextStep.put(update.getMessage().getChatId().toString(), "none");
                        this.deleteMessage(update.getMessage());
                        User senderuser = User.getCurrentUser(update.getMessage().getChatId());
                        User frienduser = User.getUser(sendMessageData.get(update.getMessage().getChatId().toString()));
                        frienduser.player.sendMessage(ChatColor.GREEN + " [MT] Сообщение от пользователя " + senderuser.playername + ": " + update.getMessage().getText().toString());

                    }
                    if(nextStep.get(update.getMessage().getChatId().toString()).equals("none")) nextStep.remove(update.getMessage().getChatId().toString());
                }

            }
        }
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().toString().startsWith("ys")) {
                String playername = update.getCallbackQuery().getData().toString().replace("ys", "");
                FreezerEvent.unfreezeplayer(playername);
                MuterEvent.unmute(playername);
                this.deleteMessage(update.getCallbackQuery().getMessage());
                Player player = Bukkit.getPlayer(playername);
                player.resetTitle();
                player.sendMessage(ChatColor.GREEN + "[MT] Успешный вход в аккаунт");
            }
            if (update.getCallbackQuery().getData().toString().startsWith("no")) {
                String playername = update.getCallbackQuery().getData().toString().replace("no", "");
                Handler.kick(playername, "Отклонено Владельцем учетной записи из Телеграмма");
                this.deleteMessage(update.getCallbackQuery().getMessage());
            }

            if (update.getCallbackQuery().getData().toString().startsWith("acc")) {
                String playername = update.getCallbackQuery().getData().toString().replace("acc", "");
                curentplayer.put(update.getCallbackQuery().getMessage().getChatId().toString(), playername);
                this.sendMessage(update.getCallbackQuery().getMessage().getChatId(), "[Бот] Выбран игрок " + playername);
            }
            if(update.getCallbackQuery().getData().toString().startsWith("addfr")){
                if(update.getCallbackQuery().getData().toString().startsWith("addfrys")){
                    String friendname = update.getCallbackQuery().getData().toString().replace("addfrys", "");
                    User user1 = User.getOnlineUser(update.getCallbackQuery().getMessage().getChatId());
                    User user2 = User.getUser(Bukkit.getPlayer(friendname).getUniqueId());
                    if (!user1.friends.contains(user2.playername)) user1.addfriend(user2.playername);
                    if (!user2.friends.contains(user1.playername)) user2.addfriend(user1.playername);
                    user2.player.sendMessage(ChatColor.GREEN + "[MT] Вам добавлен в друзья " + user1.playername);
                    user1.player.sendMessage(ChatColor.GREEN + "[MT] Вам добавлен в друзья " + user2.playername);
                    user1.sendMessage("Вам добавлен в друзья " + user2.playername);
                    this.deleteMessage(update.getCallbackQuery().getMessage());
                }
                if (update.getCallbackQuery().getData().toString().startsWith("addfrno")) {
                    String friendname = update.getCallbackQuery().getData().toString().replace("addfrno", "");
                    User user2 = User.getUser(Bukkit.getPlayer(friendname).getUniqueId());
                    User user1 = User.getOnlineUser(update.getCallbackQuery().getMessage().getChatId());
                    user2.player.sendMessage(ChatColor.RED + "[MT] Вам отказали в добавлении в друзья");
                    user1.sendMessage("Заявка успешно отклонена");
                    this.deleteMessage(update.getCallbackQuery().getMessage());
                }
            }
            if (update.getCallbackQuery().getData().toString().startsWith("chfr")) {
                String friendname = update.getCallbackQuery().getData().toString().replace("chfr", "");
                this.friendAction(friendname, update.getCallbackQuery().getMessage());
            }
            if (update.getCallbackQuery().getData().toString().startsWith("delfr")) {
                String friendname = update.getCallbackQuery().getData().toString().replace("delfr", "");
                User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
                this.deleteMessage(update.getCallbackQuery().getMessage());
                this.sendMessage(update.getCallbackQuery().getMessage().getChatId(), "[Бот] " + user.remFriend(friendname));
            }
            if (update.getCallbackQuery().getData().toString().startsWith("sndmsg")) {
                String friendname = update.getCallbackQuery().getData().toString().replace("sndmsg", "");
                this.deleteMessage(update.getCallbackQuery().getMessage());
                User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
                this.sendMessage(update.getCallbackQuery().getMessage().getChatId(), "[Бот@" + user.playername + "] Отправьте текст сообщения");
                nextStep.put(update.getCallbackQuery().getMessage().getChatId().toString(), "sendmsg");
                sendMessageData.put(update.getCallbackQuery().getMessage().getChatId().toString(), friendname);
            }
            if (update.getCallbackQuery().getData().toString().startsWith("sndmcmsg")) {
                String friendname = update.getCallbackQuery().getData().toString().replace("sndmcmsg", "");
                this.deleteMessage(update.getCallbackQuery().getMessage());
                User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
                this.sendMessage(update.getCallbackQuery().getMessage().getChatId(), "[Бот@" + user.playername + "] Отправьте текст сообщения");
                nextStep.put(update.getCallbackQuery().getMessage().getChatId().toString(), "sendmcmsg");
                sendMessageData.put(update.getCallbackQuery().getMessage().getChatId().toString(), friendname);
            }
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

    public void chosePlayer(Long chatID) {
        InlineKeyboardMarkup players = new InlineKeyboardMarkup();
        List<String> playernames = User.getPlayerNames(chatID);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String name : playernames) {
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton playerbtn = new InlineKeyboardButton();
            playerbtn.setText(name);
            playerbtn.setCallbackData("acc" + name);
            colkeyb.add(playerbtn);
            keyboard.add(colkeyb);
        }

        
        players.setKeyboard(keyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText("Выберите игрока");
        sendMessage.setReplyMarkup(players);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
           System.out.println("Error sending message: " + e);
        }
    }
    private String dispPlayer(Long chatId){
        User user = User.getUser(curentplayer.get(chatId.toString()));
        return user.player.getName();
    }
    private void showFriendsList(Message message) {
        User user = User.getCurrentUser(message.getChatId());
        List<List<InlineKeyboardButton>> friends = new ArrayList<>();

        if (user.friends.size() == 0) {
            this.sendMessage(message.getChatId(), "У вас нет друзей");
            this.deleteMessage(message);
            return;
        }

        for(String friendname : user.friends){
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton freeplayerbtn = new InlineKeyboardButton();
            freeplayerbtn.setText(friendname + User.getplayerstatus(friendname));
            freeplayerbtn.setCallbackData("chfr" + friendname);
            colkeyb.add(freeplayerbtn);
            friends.add(colkeyb);
        }
        InlineKeyboardMarkup friendskeyb = new InlineKeyboardMarkup();
        friendskeyb.setKeyboard(friends);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Ваши друзья");
        sendMessage.setReplyMarkup(friendskeyb);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }
        this.deleteMessage(message);
    }

    private void friendAction(String friendname, Message message) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton delFriendButton = new InlineKeyboardButton();
        InlineKeyboardButton sendMessageButton = new InlineKeyboardButton();
        InlineKeyboardButton sendMCMessageButton = new InlineKeyboardButton();
        InlineKeyboardMarkup actionsKeyboard = new InlineKeyboardMarkup();

        delFriendButton.setText("Удалить из друзей");
        delFriendButton.setCallbackData("delfr" + friendname);
        List<InlineKeyboardButton> delfriendcolkeyb = new ArrayList<>();
        delfriendcolkeyb.add(delFriendButton);
        keyboard.add(delfriendcolkeyb);
        sendMessageButton.setText("Отправитиь личное сообщение");
        sendMessageButton.setCallbackData("sndmsg" + friendname);
        List<InlineKeyboardButton> sendmsgcolkeyb = new ArrayList<>();
        sendmsgcolkeyb.add(sendMessageButton);
        keyboard.add(sendmsgcolkeyb);
        if (User.getUser(friendname).player != null){
            sendMCMessageButton.setText("Отправить сообщение в Minecraft");
            sendMCMessageButton.setCallbackData("sndmcmsg" + friendname);
            List<InlineKeyboardButton> sendmcmsgcolkeyb = new ArrayList<>();
            sendmcmsgcolkeyb.add(sendMCMessageButton);
            keyboard.add(sendmcmsgcolkeyb);
        }
        actionsKeyboard.setKeyboard(keyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Действия с " + friendname);
        sendMessage.setReplyMarkup(actionsKeyboard);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }
        this.deleteMessage(message);

    }

}