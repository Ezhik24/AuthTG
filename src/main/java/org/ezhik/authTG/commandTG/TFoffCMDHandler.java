package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TFoffCMDHandler implements CommandHandler{

    @Override
    public void execute(Update update) {
        if (AuthTG.authNecessarily || AuthTG.notRegAndLogin) {
            AuthTG.bot.deleteMessage(update.getMessage());
        } else {
            User user = User.getCurrentUser(update.getMessage().getChatId());
            if (user != null) {
                AuthTG.loader.setTwofactor(user.uuid, false);
                user.sendMessage(AuthTG.getMessage("tfoffsuccess", "TG"));
            } else {
                AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("tfoffntactive", "TG"));
            }
        }
    }
}
