package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.KickAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class KickCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), "[Бот] Вы не привязывали аккаунт");
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("kick")) {
            String[] args = update.getMessage().getText().toString().split(" ");
            if (args.length < 2) {
                user.sendMessage("Введите никнейм и причину");
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new KickAskHandler());
            } else {
                if (args.length < 3) {
                    user.sendMessage("Команда введена не полностью, введите: /kick <никнейм> <причина>");
                    return;
                }
                String message = String.join(" ", args).substring(args[0].length() + 1).substring(args[1].length() + 1);
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage("Игрок не авторизован");
                    return;
                }
                if (user1.player == null) {
                    user.sendMessage("Игрок не онлайн");
                    return;
                }
                Handler.kick(user1.playername, message);
                user.sendMessage("Игрок " + user1.playername + " был успешно кикнут");
            }
        } else user.sendMessage("У вас нет прав");
    }
}
