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
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("mutetgactive", "TG"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("mute")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.getMessage("mute", "TG"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new MuteAskHandler());
            } else {
                if (args.length < 3) {
                    user.sendMessage(AuthTG.getMessage("muteusage", "TG"));
                    return;
                }
                User target = User.getUser(args[1]);
                if (target == null) {
                    user.sendMessage(AuthTG.getMessage("muteplayernotfound", "TG"));
                    return;
                }
                if (AuthTG.loader.isMuted(target.uuid)) {
                    user.sendMessage(AuthTG.getMessage("mutealready", "TG"));
                    return;
                }
                if (args[2].equals("0")) {
                    user.sendMessage(AuthTG.getMessage("mutetimezero", "TG"));
                    return;
                }
                String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3);
                if (reason.length() > 120) {
                    user.sendMessage(AuthTG.getMessage("mutereasonlong", "TG"));
                    return;
                }
                int lettersCount = 0;
                for (int i = 0; i < args[2].length(); i++) {
                    if (Character.isAlphabetic(args[2].charAt(i))) {
                        lettersCount++;
                    }
                }
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String time = timedate.format(formatter);
                String formattedDate = "", message = "", adminmsg = AuthTG.getMessage("mutesuccess", "TG").replace("{PLAYER}", target.playername);
                if (lettersCount > 2) {
                    user.sendMessage(AuthTG.getMessage("mutetimeformat", "TG"));
                }
                else if (args[2].contains("d")) {
                    LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(args[2].replace("d", "")));
                    formattedDate = date.format(formatter);
                    message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
                } else if (args[2].contains("h")) {
                    LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(args[2].replace("h", "")));
                    formattedDate = date.format(formatter);
                    message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
                }else if (args[2].equals("-s")) {
                    formattedDate = "0";
                    message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
                } else if (args[2].contains("m")) {
                    LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(args[2].replace("m", "")));
                    formattedDate = date.format(formatter);
                    message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
                } else if (args[2].contains("s")) {
                    LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(args[2].replace("s", "")));
                    formattedDate = date.format(formatter);
                    message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(target.uuid)).replace("{REASON}", AuthTG.loader.getMuteReason(target.uuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(target.uuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(target.uuid));
                }
                if (target.player != null) target.player.sendMessage(message);
                AuthTG.loader.setMuteTime(target.uuid, formattedDate, reason, time, user.playername);
                user.sendMessage(adminmsg);
            }
        } else user.sendMessage(AuthTG.getMessage("mutenoperm", "TG"));
    }
}
