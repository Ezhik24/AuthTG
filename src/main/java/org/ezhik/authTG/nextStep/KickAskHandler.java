package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class KickAskHandler implements NextStepHandler {
    @Override
    public void execute(Update update) {
        String playername = update.getMessage().getText().toString().split(" ")[0];
        User user1 = User.getUser(playername);
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user1 == null) {
            user.sendMessage(AuthTG.getMessage("kickusernotfound", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (user1.player == null) {
            user.sendMessage(AuthTG.getMessage("kickusernotonline", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        Handler.kick(user1.playername, update.getMessage().getText().toString().substring(playername.length() + 1));
        user.sendMessage(AuthTG.getMessage("kickuser","TG").replace("{PLAYER}", user1.playername));
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
