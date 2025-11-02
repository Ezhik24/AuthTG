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
            user.sendMessage(AuthTG.getMessage("banformat", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (user1 == null) {
            user.sendMessage(AuthTG.getMessage("banusernotfound", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (AuthTG.loader.isBanned(user1.uuid)) {
            user.sendMessage(AuthTG.getMessage("banalreadybanned", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (args[1].equals("0")) {
            user.sendMessage(AuthTG.getMessage("bantimenull", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        if (reason.length() > 120) {
            user.sendMessage(AuthTG.getMessage("banreasonlong", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        int lettersCount = 0;
        for (int i = 0; i < args[1].length(); i++) {
            if (Character.isAlphabetic(args[1].charAt(i))) {
                lettersCount++;
            }
        }
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        String time = date.format(formatter);
        String formattedDate = "", adminmsg = AuthTG.getMessage("bansuccess", "TG").replace("{PLAYER}", user1.playername), message = "";
        if (lettersCount > 2) {
            user.sendMessage(AuthTG.getMessage("bantimeformat", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        }
        else if (args[1].contains("d")) {
            LocalDateTime dateTime = LocalDateTime.now().plusDays(Long.parseLong(args[1].replace("d", "")));
            formattedDate = dateTime.format(formatter);
            message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
        } else if (args[1].contains("h")) {
            LocalDateTime dateTime = LocalDateTime.now().plusHours(Long.parseLong(args[1].replace("h", "")));
            formattedDate = dateTime.format(formatter);
            message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
        } else if (args[1].equals("-s")) {
            formattedDate = "0";
            message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}", formattedDate);
        } else if (args[1].contains("m")) {
            LocalDateTime dateTime = LocalDateTime.now().plusMinutes(Long.parseLong(args[1].replace("m", "")));
            formattedDate = dateTime.format(formatter);
            message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
        } else if (args[1].contains("s")) {
            LocalDateTime dateTime = LocalDateTime.now().plusSeconds(Long.parseLong(args[1].replace("s", "")));
            formattedDate = dateTime.format(formatter);
            message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
        }
        user.sendMessage(adminmsg);
        AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&',message));
    }
}
