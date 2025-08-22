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
            user.sendMessage("Пользователь не авторизован");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        }
        if (user1.player == null) {
            user.sendMessage("Игрок не онлайн!");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        }
        Handler.kick(user1.playername, update.getMessage().getText().toString().substring(playername.length() + 1));
        user.sendMessage("Игрок " + user1.playername + " успешно кикнут!");
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
