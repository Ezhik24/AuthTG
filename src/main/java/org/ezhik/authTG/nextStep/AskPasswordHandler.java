package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class AskPasswordHandler implements NextStepHandler {
    UUID uuid;
    public AskPasswordHandler(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void execute(Update update) {
        Long key = update.getMessage().getChatId();
        if (AuthTG.loader.passwordValid(uuid, update.getMessage().getText().toString())) {
            User.register(update.getMessage(), uuid);
        } else {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.activetgwrongpass"));
        }
        AuthTG.bot.deleteMessage(update.getMessage());
        AuthTG.bot.remNextStepHandler(key);
    }
}
