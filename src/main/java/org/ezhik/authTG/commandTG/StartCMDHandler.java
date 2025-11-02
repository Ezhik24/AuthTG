package org.ezhik.authTG.commandTG;

import org.apache.http.auth.AUTH;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.BotTelegram;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.AskPlayernameHandler;
import org.ezhik.authTG.nextStep.NextStepHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCMDHandler implements CommandHandler{
    @Override
    public void execute(Update update) {
        AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("startlinkacc", "TG"));
        AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new AskPlayernameHandler());
    }
}
