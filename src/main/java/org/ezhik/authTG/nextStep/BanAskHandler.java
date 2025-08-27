package org.ezhik.authTG.nextStep;

import org.bukkit.ChatColor;
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
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (user1 == null) {
            user.sendMessage("Пользователь не авторизован");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (AuthTG.loader.isBanned(user1.uuid)) {
            user.sendMessage("Пользователь уже забанен");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (args[1].equals("0")) {
            user.sendMessage("Вы не можете забанить пользователя на 0");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        if (reason.length() > 120) {
            user.sendMessage("Слишком длинная причина");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (args[1].contains("d")) {
            LocalDateTime date = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(Long.parseLong(args[1].replace("d", "")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = dateTime.format(formatter);
            String time = date.format(formatter);
            AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage("Вы забанили " + user1.playername + " на " + args[1] + " дней");
            if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
        } else if (args[1].contains("h")) {
            LocalDateTime date = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.now().plusHours(Long.parseLong(args[1].replace("h", "")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = dateTime.format(formatter);
            String time = date.format(formatter);
            AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage("Вы забанили " + user1.playername + " на " + args[1] + " часов");
            if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
        } else if (args[1].contains("m")) {
            LocalDateTime date = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.now().plusMinutes(Long.parseLong(args[1].replace("m", "")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = dateTime.format(formatter);
            String time = date.format(formatter);
            AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage("Вы забанили " + user1.playername + " на " + args[1] + " минут");
            if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
        } else if (args[1].contains("s")) {
            LocalDateTime date = LocalDateTime.now();
            LocalDateTime dateTime = LocalDateTime.now().plusSeconds(Long.parseLong(args[1].replace("s", "")));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = dateTime.format(formatter);
            String time = date.format(formatter);
            AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage("Вы забанили " + user1.playername + " на " + args[1] + " секунд");
            if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
        } else if (args[1].equals("-s")) {
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = date.format(formatter);
            AuthTG.loader.setBanTime(user1.uuid, "0", reason, formattedDate, user.playername);
            user.sendMessage("Вы забанили " + user1.playername + " на навсегда");
            if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}", formattedDate).replace("{BR}", "\n"));
        } else {
            user.sendMessage("Неверный формат времени (Используйте d, h, m, s)");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
