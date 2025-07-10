package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ezhik.authTG.nextStep.SendMessageTGHandler;

import java.util.UUID;

public class SendMessageTG implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
        User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
        user.sendMessage(AuthTG.config.getString("messages.telegram.sndmsgtgtext"));
        AuthTG.bot.setNextStepHandler(update.getCallbackQuery().getMessage().getChatId(), new SendMessageTGHandler());
        AuthTG.bot.setUserData(update.getCallbackQuery().getMessage().getChatId().toString(), UUID.fromString(str[1]));
    }
}
