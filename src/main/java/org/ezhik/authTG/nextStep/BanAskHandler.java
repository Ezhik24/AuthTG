package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BanAskHandler implements NextStepHandler {

    @Override
    public void execute(Update update) {
        String playername = update.getMessage().getText().split(" ")[0];
        User user1 = User.getUser(playername);
        User user = User.getCurrentUser(update.getMessage().getChatId());
        String[] args = update.getMessage().getText().split(" ");
        if (args.length < 3) {
            user.sendMessage("Неверный формат команды");
        } else {
            if (user1 == null) {
                user.sendMessage("Пользователь не авторизован");
            } else {
                if (args[1].equals("0")) {
                    user.sendMessage("Вы не можете забанить пользователя на 0");
                } else {
                    String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
                    if (reason.length() > 120) {
                        user.sendMessage("Слишком длинная причина");
                    } else {
                        if (user1 != null) {
                            if (user1.player != null) {
                                Handler.kick(user1.playername, "Бан: " + reason + " (Автор: " + user.playername + ")");
                            }
                            if (args[1].contains("d")) {
                                LocalDateTime date = LocalDateTime.now();
                                LocalDateTime dateTime = LocalDateTime.now().plusDays(Long.parseLong(args[1].replace("d", "")));
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                                String formattedDate = dateTime.format(formatter);
                                String time = date.format(formatter);
                                AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                            } else if (args[1].contains("h")) {
                                LocalDateTime date = LocalDateTime.now();
                                LocalDateTime dateTime = LocalDateTime.now().plusHours(Long.parseLong(args[1].replace("h", "")));
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                                String formattedDate = dateTime.format(formatter);
                                String time = date.format(formatter);
                                AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                            } else if (args[1].contains("m")) {
                                LocalDateTime date = LocalDateTime.now();
                                LocalDateTime dateTime = LocalDateTime.now().plusMinutes(Long.parseLong(args[1].replace("m", "")));
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                                String formattedDate = dateTime.format(formatter);
                                String time = date.format(formatter);
                                AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                            } else if (args[1].contains("s")) {
                                LocalDateTime date = LocalDateTime.now();
                                LocalDateTime dateTime = LocalDateTime.now().plusSeconds(Long.parseLong(args[1].replace("s", "")));
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                                String formattedDate = dateTime.format(formatter);
                                String time = date.format(formatter);
                                AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                            } else {
                                user.sendMessage("Неверный формат времени (Используйте d, h, m, s)");
                            }
                        } else {
                            user.sendMessage("Пользователь не авторизован");
                        }
                    }
                }
            }
        }
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
