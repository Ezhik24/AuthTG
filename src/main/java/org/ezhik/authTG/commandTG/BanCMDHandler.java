package org.ezhik.authTG.commandTG;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.nextStep.BanAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BanCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getFrom().getId());
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), "[Бот] Вы не авторизованы");
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("ban")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage("Напишите: <никнейм> <время> <причина> или <никнейм> <причина>");
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new BanAskHandler());
            } else {
                if (args.length < 3) {
                    user.sendMessage("Использование: /ban <никнейм> <время> <причина> или <никнейм> <причина>");
                    return;
                }
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage("Пользователь не авторизован");
                    return;
                }
                if (AuthTG.loader.isBanned(user1.uuid)) {
                    user.sendMessage("Пользователь уже забанен");
                    return;
                }
                if (args[2].startsWith("0")) {
                    user.sendMessage("Вы не можете забанить пользователя на 0");
                    return;
                }
                String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3);
                if (reason.length() > 120) {
                    user.sendMessage("Слишком длинная причина");
                    return;
                }
                if (args[2].contains("d")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusDays(Long.parseLong(args[2].replace("d", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage("Вы забанили " + user1.playername + " на " + args[2].replace("d", "") + " дней");
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].contains("h")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusHours(Long.parseLong(args[2].replace("h", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage("Вы забанили " + user1.playername + " на " + args[2].replace("h", "") + " часов");
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].contains("m")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusMinutes(Long.parseLong(args[2].replace("m", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage("Вы забанили " + user1.playername + " на " + args[2].replace("m", "") + " минут");
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].contains("s")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusSeconds(Long.parseLong(args[2].replace("s", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage("Вы забанили " + user1.playername + " на " + args[2].replace("s", "") + " секунд");
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].equals("-s")) {
                    LocalDateTime date = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, "0", reason, formattedDate, user.playername);
                    user.sendMessage("Вы забанили " + user1.playername + " на навсегда");
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}",formattedDate).replace("{BR}", "\n"));
                } else {
                    user.sendMessage("Неверный формат времени (Используйте d, h, m, s или -s)");
                }
            }
        } else user.sendMessage("У вас недостаточно прав");
    }
}
