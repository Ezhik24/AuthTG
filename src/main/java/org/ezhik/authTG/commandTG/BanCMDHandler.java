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
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("bannoactivetg", "TG"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("ban")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.getMessage("ban", "TG"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new BanAskHandler());
            } else {
                if (args.length < 4) {
                    user.sendMessage(AuthTG.getMessage("banusage", "TG"));
                    return;
                }
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage(AuthTG.getMessage("banusernotfound", "TG"));
                    return;
                }
                if (AuthTG.loader.isBanned(user1.uuid)) {
                    user.sendMessage(AuthTG.getMessage("banalreadybanned", "TG"));
                    return;
                }
                if (args[2].startsWith("0")) {
                    user.sendMessage(AuthTG.getMessage("bantimenull", "TG"));
                    return;
                }
                String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3);
                if (reason.length() > 120) {
                    user.sendMessage(AuthTG.getMessage("banreasonlong", "TG"));
                    return;
                }
                int lettersCount = 0;
                for (int i = 0; i < args[2].length(); i++) {
                    if (Character.isAlphabetic(args[2].charAt(i))) {
                        lettersCount++;
                    }
                }
                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String time = date.format(formatter);
                String formattedDate = "", message = "", adminmsg = AuthTG.getMessage("bansuccess", "TG").replace("{PLAYER}", user1.playername);
                if (lettersCount > 2) {
                    user.sendMessage(AuthTG.getMessage("bantimeformat", "TG"));
                }
                else if (args[2].contains("d")) {
                    LocalDateTime dateTime = LocalDateTime.now().plusDays(Long.parseLong(args[2].replace("d", "")));
                    formattedDate = dateTime.format(formatter);
                    message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
                } else if (args[2].contains("h")) {
                    LocalDateTime dateTime = LocalDateTime.now().plusHours(Long.parseLong(args[2].replace("h", "")));
                    formattedDate = dateTime.format(formatter);
                    message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
                } else if (args[2].equals("-s")) {
                    formattedDate = "0";
                    message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}",time);
                }else if (args[2].contains("m")) {
                    LocalDateTime dateTime = LocalDateTime.now().plusMinutes(Long.parseLong(args[2].replace("m", "")));
                    formattedDate = dateTime.format(formatter);
                    message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
                } else if (args[2].contains("s")) {
                    LocalDateTime dateTime = LocalDateTime.now().plusSeconds(Long.parseLong(args[2].replace("s", "")));
                    formattedDate = dateTime.format(formatter);
                    message = AuthTG.getMessage("ban", "MC").replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time);
                }
                user.sendMessage(adminmsg);
                if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&',message));
                AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
            }
        } else user.sendMessage(AuthTG.getMessage("bannoperm", "TG"));
    }
}
