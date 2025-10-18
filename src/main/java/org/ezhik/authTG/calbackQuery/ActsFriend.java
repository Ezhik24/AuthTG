package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ActsFriend implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
        User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton delFriendButton = new InlineKeyboardButton();
        InlineKeyboardButton sendMessageButton = new InlineKeyboardButton();
        InlineKeyboardButton sendMCMessageButton = new InlineKeyboardButton();
        InlineKeyboardMarkup actionsKeyboard = new InlineKeyboardMarkup();
        delFriendButton.setText(AuthTG.getMessage("actsdelfrbut", "TG"));
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        delFriendButton.setCallbackData("delfr_" + str[1]);
        List<InlineKeyboardButton> delFriendCoolkeyb = new ArrayList<>();
        delFriendCoolkeyb.add(delFriendButton);
        keyboard.add(delFriendCoolkeyb);
        sendMessageButton.setText(AuthTG.getMessage("actssndtgbut", "TG"));
        sendMessageButton.setCallbackData("sndtg_" + str[1]);
        List<InlineKeyboardButton> sendMessageCoolkeyb = new ArrayList<>();
        sendMessageCoolkeyb.add(sendMessageButton);
        keyboard.add(sendMessageCoolkeyb);
        if (User.getUser(UUID.fromString(str[1])).player != null) {
            sendMCMessageButton.setText(AuthTG.getMessage("actssndmcbut", "TG"));
            sendMCMessageButton.setCallbackData("sndmc_" + str[1]);
            List<InlineKeyboardButton> sendMCMessageCoolkeyb = new ArrayList<>();
            sendMCMessageCoolkeyb.add(sendMCMessageButton);
            keyboard.add(sendMCMessageCoolkeyb);
        }
        actionsKeyboard.setKeyboard(keyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText(AuthTG.getMessage("actssendmsg", "TG").replace("{PLAYER}", user.playername) + User.getUser(UUID.fromString(str[1])).playername);
        sendMessage.setReplyMarkup(actionsKeyboard);
        try {
            AuthTG.bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            AuthTG.logger.log(Level.SEVERE, "Error while sending message", e);
        }
    }
}
