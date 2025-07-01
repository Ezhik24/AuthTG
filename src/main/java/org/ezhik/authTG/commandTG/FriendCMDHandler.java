package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class FriendCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        Message message = update.getMessage();
        User user = User.getCurrentUser(message.getChatId());
        List<List<InlineKeyboardButton>> friends = new ArrayList<>();
        if (user.friends.size() == 0) {
            AuthTG.bot.sendMessage(message.getChatId(), "[Бот] У вас нет друзей!");
            AuthTG.bot.deleteMessage(message);
            return;
        }
        for (String friendname : user.friends) {
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton freeplayerbtn = new InlineKeyboardButton();
            User friend = User.getUser(friendname);
            freeplayerbtn.setText(friendname + ((friend.player != null) ? " [Online]" : "   [Offline]"));
            freeplayerbtn.setCallbackData("chfr_" + friend.uuid);
            colkeyb.add(freeplayerbtn);
            friends.add(colkeyb);
        }
        InlineKeyboardMarkup friendskeyb = new InlineKeyboardMarkup();
        friendskeyb.setKeyboard(friends);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("[Бот@" + user.playername + "] Выберите друга");
        sendMessage.setReplyMarkup(friendskeyb);
        try {
            AuthTG.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e);
        }
        AuthTG.bot.deleteMessage(message);
    }
}
