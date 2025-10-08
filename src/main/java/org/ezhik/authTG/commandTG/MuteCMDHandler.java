package org.ezhik.authTG.commandTG;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.nextStep.MuteAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MuteCMDHandler implements CommandHandler {

    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getFrom().getId());
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.mutetgactive"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("mute")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.config.getString("messages.telegram.mute"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new MuteAskHandler());
            } else {
                if (args.length < 3) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.muteusage"));
                    return;
                }
                User target = User.getUser(args[1]);
                if (target == null) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.muteplayernotfound"));
                    return;
                }
                if (AuthTG.loader.isMuted(target.uuid)) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutealready"));
                    return;
                }
                if (args[2].equals("0")) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutetimezero"));
                    return;
                }
                String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3);
                if (reason.length() > 120) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutereasonlong"));
                    return;
                }
                if (args[2].contains("d")) {
                    LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(args[2].replace("d", "")));
                    LocalDateTime timedate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = date.format(formatter);
                    String time = timedate.format(formatter);
                    AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
                    if (target.player != null) target.player.sendMessage(message);
                } else if (args[2].contains("h")) {
                    LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(args[2].replace("h", "")));
                    LocalDateTime timedate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = date.format(formatter);
                    String time = timedate.format(formatter);
                    AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
                    if (target.player != null) target.player.sendMessage(message);
                } else if (args[2].contains("m")) {
                    LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(args[2].replace("m", "")));
                    LocalDateTime timedate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = date.format(formatter);
                    String time = timedate.format(formatter);
                    AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
                    if (target.player != null) target.player.sendMessage(message);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
                } else if (args[2].contains("s")) {
                    LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(args[2].replace("s", "")));
                    LocalDateTime timedate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String formattedDate = date.format(formatter);
                    String time = timedate.format(formatter);
                    AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
                    if (target.player != null) target.player.sendMessage(message);
                } else if (args[2].equals("-s")) {
                    LocalDateTime timedate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                    String time = timedate.format(formatter);
                    AuthTG.loader.setMuteTime(target.uuid, "0", reason, time, user.playername);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutesuccess").replace("{PLAYER}", target.playername));
                    String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid)).replace("{BR}", "\n");
                    if (target.player != null) target.player.sendMessage(message);
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.mutetimeformat"));
                }
            }
        } else user.sendMessage(AuthTG.config.getString("messages.telegram.mutenoperm"));
    }
}
