package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class AccAccounts implements CallbackQueryHandler{
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        AuthTG.loader.setCurrentUUID(UUID.fromString(str[1]), update.getCallbackQuery().getMessage().getChatId());
        User user = User.getUser(UUID.fromString(str[1]));
        AuthTG.bot.sendMessage(update.getCallbackQuery().getMessage().getChatId(), AuthTG.getMessage("accaccounts", "TG").replace("{PLAYER}", user.playername));
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
