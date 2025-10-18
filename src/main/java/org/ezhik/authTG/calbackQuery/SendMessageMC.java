package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.SendMessageMCHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class SendMessageMC implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
        User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
        user.sendMessage(AuthTG.getMessage("sndmsgmctext", "MC"));
        AuthTG.bot.setNextStepHandler(update.getCallbackQuery().getMessage().getChatId(), new SendMessageMCHandler());
        AuthTG.bot.setUserData(update.getCallbackQuery().getMessage().getChatId().toString(), UUID.fromString(str[1]));
    }
}
