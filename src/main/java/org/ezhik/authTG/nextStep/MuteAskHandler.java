package org.ezhik.authTG.nextStep;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MuteAskHandler implements NextStepHandler {
    @Override
    public void execute(Update update) {
        String playername = update.getMessage().getText().split(" ")[0];
        User user = User.getCurrentUser(update.getMessage().getChatId());
        User target = User.getUser(playername);
        String[] args = update.getMessage().getText().split(" ");
        if (args.length < 3) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.muteformat"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (target == null) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.muteplayernotfound"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (AuthTG.loader.isMuted(target.uuid)) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutealready"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (args[1].equals("0")) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutetimezero"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        if (reason.length() > 120) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutereasonlong"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (args[1].contains("d")) {
            LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(args[1].replace("d", "")));
            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = date.format(formatter);
            String time = timedate.format(formatter);
            AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
            String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
            if (target.player != null) target.player.sendMessage(message);
        } else if (args[1].contains("h")) {
            LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(args[1].replace("h", "")));
            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = date.format(formatter);
            String time = timedate.format(formatter);
            AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
            String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
            if (target.player != null) target.player.sendMessage(message);
        } else if (args[1].contains("m")) {
            LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(args[1].replace("m", "")));
            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = date.format(formatter);
            String time = timedate.format(formatter);
            AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
            String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
            if (target.player != null) target.player.sendMessage(message);
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
        } else if (args[1].contains("s")) {
            LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(args[1].replace("s", "")));
            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String formattedDate = date.format(formatter);
            String time = timedate.format(formatter);
            AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
            String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
            if (target.player != null) target.player.sendMessage(message);
        } else if (args[1].equals("-s")) {
            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String time = timedate.format(formatter);
            AuthTG.loader.setMuteTime(target.uuid, "0", reason, time, user.playername);
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
            String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
            if (target.player != null) target.player.sendMessage(message);
        } else {
            user.sendMessage(AuthTG.config.getString("messages.telegram.mutetimeformat"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
