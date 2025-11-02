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
            user.sendMessage(AuthTG.getMessage("muteformat","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (target == null) {
            user.sendMessage(AuthTG.getMessage("muteplayernotfound","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (AuthTG.loader.isMuted(target.uuid)) {
            user.sendMessage(AuthTG.getMessage("mutealready","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        if (args[1].equals("0")) {
            user.sendMessage(AuthTG.getMessage("mutetimezero","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        if (reason.length() > 120) {
            user.sendMessage(AuthTG.getMessage("mutereasonlong", "TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
            return;
        }
        int lettersCount = 0;
        for (int i = 0; i < args[1].length(); i++) {
            if (Character.isAlphabetic(args[1].charAt(i))) {
                lettersCount++;
            }
        }
        LocalDateTime timedate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        String time = timedate.format(formatter);
        String adminmsg = AuthTG.getMessage("mutesuccess", "TG").replace("{PLAYER}", target.playername), formattedDate = "0", message = "";
        if (lettersCount > 2) {
            user.sendMessage(AuthTG.getMessage("mutetimeformat","TG"));
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        }
        else if (args[1].contains("d")) {
            LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(args[1].replace("d", "")));
            formattedDate = date.format(formatter);
            message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
        } else if (args[1].equals("-s")) {
            formattedDate = "0";
            message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
        } else if (args[1].contains("h")) {
            LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(args[1].replace("h", "")));
            formattedDate = date.format(formatter);
            message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
        } else if (args[1].contains("m")) {
            LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(args[1].replace("m", "")));
            formattedDate = date.format(formatter);
            message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
        } else if (args[1].contains("s")) {
            LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(args[1].replace("s", "")));
            formattedDate = date.format(formatter);
            message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
        }
        AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
        if (target.player != null) target.player.sendMessage(message);
        user.sendMessage(adminmsg);
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
    }
}
