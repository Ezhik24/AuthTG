package org.ezhik.authTG.commandTG;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.Handler;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.KickAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;

public class KickCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        System.out.println("test1");
        if (user.activetg) {
            System.out.println("test2");
            if (user.isadmin) {
                System.out.println("test3");
                String[] args = update.getMessage().getText().toString().split(" ");
                if (args.length < 2) {
                    System.out.println("test4");
                    user.sendMessage("Введите никнейм и причину");
                    AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new KickAskHandler());
                } else {
                    System.out.println("test5");
                    String message = String.join(" ", args).substring(args[0].length() + 1).substring(args[1].length() + 1);
                    System.out.println(message);
                    System.out.println(message.isEmpty());
                    if (message.isEmpty()) {
                        user.sendMessage("Команда введена не полностью, введите: /kick <никнейм> <причина>");
                    } else {
                        Player player = Bukkit.getPlayer(args[1]);
                        System.out.println(player == null);
                        if (player == null) {
                            user.sendMessage("Игрок не найден");
                        } else {
                            Handler.kick(player.getName(), message);
                            user.sendMessage("Игрок " + player.getName() + " был успешно кикнут");
                        }
                    }
                }
            } else user.sendMessage("У вас нет прав");
        } else AuthTG.bot.sendMessage(update.getMessage().getChatId(), "[Бот] Вы не привязывали аккаунт");
    }
}
