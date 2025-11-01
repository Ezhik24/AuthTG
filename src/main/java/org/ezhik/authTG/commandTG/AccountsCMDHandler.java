package org.ezhik.authTG.commandTG;

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

public class AccountsCMDHandler implements CommandHandler{
    @Override
    public void execute(Update update) {
        InlineKeyboardMarkup players = new InlineKeyboardMarkup();
        List<UUID> uuids = AuthTG.loader.getPlayerNames(update.getMessage().getChatId());
        if (uuids != null) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (UUID uuid : uuids) {
                List<InlineKeyboardButton> colkeyb = new ArrayList<>();
                InlineKeyboardButton playerbtn = new InlineKeyboardButton();
                User user = User.getUser(uuid);
                playerbtn.setText(user.playername);
                playerbtn.setCallbackData("acc_" + uuid);
                colkeyb.add(playerbtn);
                keyboard.add(colkeyb);
            }
            players.setKeyboard(keyboard);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(AuthTG.getMessage("accountschange", "TG"));
            sendMessage.setReplyMarkup(players);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                AuthTG.logger.log(Level.SEVERE, "Error while sending message", e);
            }
        } else {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("accounschangenotfound", "TG"));
        }

    }
}
