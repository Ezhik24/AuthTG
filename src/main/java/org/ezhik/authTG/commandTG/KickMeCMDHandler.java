package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.Handler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class KickMeCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user != null) {
            if (user.player != null) {
                Handler.kick(user.playername, "Вы были кикнуты через Телеграм!");
                user.sendMessage("Вы успешно кикнули свой Аккаунт!");
            } else {
                user.sendMessage("Вы не в игре!");
            }
        } else {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(),"[Бот] Вы не привязывали аккаунт!");
        }
    }
}
