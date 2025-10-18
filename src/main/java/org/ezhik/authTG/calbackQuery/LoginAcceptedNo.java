package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class LoginAcceptedNo implements CallbackQueryHandler{
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        User user = User.getUser(UUID.fromString(str[1]));
        AuthHandler.removeTimeout(user.uuid);
        Handler.kick(user.playername, AuthTG.getMessage("loginnosuccess", "MC"));
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
