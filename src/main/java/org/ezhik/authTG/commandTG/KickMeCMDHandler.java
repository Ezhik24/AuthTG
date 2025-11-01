package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class KickMeCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user != null) {
            if (user.player != null) {
                Handler.kick(user.playername, AuthTG.getMessage("kickmeplayer", "TG"));
                user.sendMessage(AuthTG.getMessage("kickmesuccess", "TG"));
            } else {
                user.sendMessage(AuthTG.getMessage("kickmeplnotonline", "TG"));
            }
        } else {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(),AuthTG.getMessage("kickmenotactive", "TG"));
        }
    }
}
