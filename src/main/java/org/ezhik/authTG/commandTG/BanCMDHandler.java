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
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.bannoactivetg"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("ban")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.config.getString("messages.telegram.ban"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new BanAskHandler());
            } else {
                if (args.length < 4) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.banusage"));
                    return;
                }
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.banusernotfound"));
                    return;
                }
                if (AuthTG.loader.isBanned(user1.uuid)) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.banalreadybanned"));
                    return;
                }
                if (args[2].startsWith("0")) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bantimenull"));
                    return;
                }
                String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3);
                if (reason.length() > 120) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.banreasonlong"));
                    return;
                }
                if (args[2].contains("d")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusDays(Long.parseLong(args[2].replace("d", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bansuccess").replace("{PLAYER}", user1.playername));
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].contains("h")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusHours(Long.parseLong(args[2].replace("h", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bansuccess").replace("{PLAYER}", user1.playername));
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].contains("m")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusMinutes(Long.parseLong(args[2].replace("m", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bansuccess").replace("{PLAYER}", user1.playername));
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].contains("s")) {
                    LocalDateTime date = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.now().plusSeconds(Long.parseLong(args[2].replace("s", "")));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = dateTime.format(formatter);
                    String time = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bansuccess").replace("{PLAYER}", user1.playername));
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n"));
                } else if (args[2].equals("-s")) {
                    LocalDateTime date = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = date.format(formatter);
                    AuthTG.loader.setBanTime(user1.uuid, "0", reason, formattedDate, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bansuccess").replace("{PLAYER}", user1.playername));
                    if (user1.player != null) Handler.kick(user1.playername, ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", user.playername).replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}",formattedDate).replace("{BR}", "\n"));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.bantimeformat"));
                }
            }
        } else user.sendMessage(AuthTG.config.getString("messages.telegram.bannoperm"));
    }
}
