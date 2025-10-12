package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnbanAskHandler implements NextStepHandler{
    @Override
    public void execute(Update update) {
        User user1 = User.getUser(update.getMessage().getText().toString());
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user1 == null) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.unbanusernotfound"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (!AuthTG.loader.isBanned(user1.uuid)) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.unbanusernotbanned"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        AuthTG.loader.deleteBan(user1.uuid);
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        user.sendMessage(AuthTG.config.getString("messages.telegram.unbanuser").replace("{PLAYER}", user1.playername));
    }
}
