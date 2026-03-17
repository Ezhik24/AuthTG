package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MuteCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (!player.hasPermission("authtg.mute")) {
                MessageHelper.send(player, AuthTG.getMessage("mutenoperm", "MC"));
                return false;
            }
            if (strings.length < 3) {
                MessageHelper.send(player, AuthTG.getMessage("muteusage", "MC"));
                return false;
            }

            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(player, AuthTG.getMessage("muteplnotfound", "MC"));
                return false;
            }

            Player target = Bukkit.getPlayer(targetuuid);
            if (AuthTG.loader.isMuted(targetuuid)) {
                MessageHelper.send(player, AuthTG.getMessage("muteplmuted", "MC"));
                return false;
            }
            if (reason.isEmpty()) {
                MessageHelper.send(player, AuthTG.getMessage("muteusage", "MC"));
                return false;
            }
            if (reason.length() > 120) {
                MessageHelper.send(player, AuthTG.getMessage("mutetoolong", "MC"));
                return false;
            }

            int lettersCount = 0;
            for (int i = 0; i < strings[1].length(); i++) {
                if (Character.isAlphabetic(strings[1].charAt(i))) {
                    lettersCount++;
                }
            }

            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String time = timedate.format(formatter);

            String formattedDate = "";
            String message = "";

            if (lettersCount > 1) {
                MessageHelper.send(player, AuthTG.getMessage("mutetimeformat", "MC"));
            } else if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage(player.getName(), reason, formattedDate, time);
            } else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage(player.getName(), reason, formattedDate, time);
            } else if (strings[1].equals("-s")) {
                formattedDate = "0";
                message = buildMuteMessage(player.getName(), reason, "навсегда", time);
            } else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage(player.getName(), reason, formattedDate, time);
            } else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage(player.getName(), reason, formattedDate, time);
            }

            MessageHelper.send(player, AuthTG.getMessage("successmute", "MC").replace("{PLAYER}", strings[0]));
            if (target != null) {
                MessageHelper.send(target, message);
            }
            AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, player.getName());
            return true;
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            if (strings.length < 3) {
                MessageHelper.send(console, AuthTG.getMessage("muteusage", "MC"));
                return false;
            }

            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(console, AuthTG.getMessage("muteplnotfound", "MC"));
                return false;
            }

            Player target = Bukkit.getPlayer(targetuuid);
            if (AuthTG.loader.isMuted(targetuuid)) {
                MessageHelper.send(console, AuthTG.getMessage("muteplmuted", "MC"));
                return false;
            }
            if (reason.isEmpty()) {
                MessageHelper.send(console, AuthTG.getMessage("muteusage", "MC"));
                return false;
            }
            if (reason.length() > 120) {
                MessageHelper.send(console, AuthTG.getMessage("mutetoolong", "MC"));
                return false;
            }

            int lettersCount = 0;
            for (int i = 0; i < strings[1].length(); i++) {
                if (Character.isAlphabetic(strings[1].charAt(i))) {
                    lettersCount++;
                }
            }

            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String time = timedate.format(formatter);

            String formattedDate = "";
            String message = "";

            if (lettersCount > 1) {
                MessageHelper.send(console, AuthTG.getMessage("mutetimeformat", "MC"));
            } else if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage("CONSOLE", reason, formattedDate, time);
            } else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage("CONSOLE", reason, formattedDate, time);
            } else if (strings[1].equals("-s")) {
                formattedDate = "0";
                message = buildMuteMessage("CONSOLE", reason, "навсегда", time);
            } else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage("CONSOLE", reason, formattedDate, time);
            } else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                formattedDate = date.format(formatter);
                message = buildMuteMessage("CONSOLE", reason, formattedDate, time);
            }

            if (target != null) {
                MessageHelper.send(target, message);
            }
            AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, "CONSOLE");
            MessageHelper.send(console, AuthTG.getMessage("successmute", "MC").replace("{PLAYER}", strings[0]));
        }

        return true;
    }

    private String buildMuteMessage(String admin, String reason, String timeMute, String time) {
        return AuthTG.getMessage("mute", "MC")
                .replace("{TIMEMUTE}", timeMute)
                .replace("{REASON}", reason)
                .replace("{TIME}", time)
                .replace("{ADMIN}", admin);
    }
}