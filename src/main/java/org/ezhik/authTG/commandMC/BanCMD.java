package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.util.MessageHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BanCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (!player.hasPermission("authtg.ban")) {
                MessageHelper.send(player, AuthTG.getMessage("bannoperm", "MC"));
                return false;
            }
            if (strings.length == 0) {
                MessageHelper.send(player, AuthTG.getMessage("banusage", "MC"));
                return false;
            }
            if (strings.length < 3) {
                MessageHelper.send(player, AuthTG.getMessage("banusage", "MC"));
                return false;
            }

            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(player, AuthTG.getMessage("banplayernotfound", "MC"));
                return false;
            }
            if (AuthTG.loader.isBanned(targetuuid)) {
                MessageHelper.send(player, AuthTG.getMessage("banalreadybanned", "MC"));
                return false;
            }
            if (reason.isEmpty()) {
                MessageHelper.send(player, AuthTG.getMessage("banusage", "MC"));
                return false;
            }
            if (reason.length() > 120) {
                MessageHelper.send(player, AuthTG.getMessage("banreasonlong", "MC"));
                return false;
            }

            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            Player target = Bukkit.getPlayer(targetuuid);
            String time = timedate.format(formatter);

            int lettersCount = 0;
            for (int i = 0; i < strings[1].length(); i++) {
                if (Character.isAlphabetic(strings[1].charAt(i))) {
                    lettersCount++;
                }
            }

            String formattedDate = "";
            String message = "";

            if (lettersCount > 1) {
                MessageHelper.send(player, AuthTG.getMessage("bannotimeformat", "MC"));
            } else if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage(player.getName(), reason, formattedDate, time);
            } else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage(player.getName(), reason, formattedDate, time);
            } else if (strings[1].equals("-s")) {
                formattedDate = "0";
                message = buildBanMessage(player.getName(), reason, "навсегда", formattedDate);
            } else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage(player.getName(), reason, formattedDate, time);
            } else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage(player.getName(), reason, formattedDate, time);
            }

            AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, player.getName());
            if (target != null) {
                Handler.kick(target.getName(), MessageHelper.legacySection(message));
            }
            MessageHelper.send(player, AuthTG.getMessage("bansuccess", "MC").replace("{PLAYER}", strings[0]));
            return true;
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            if (strings.length == 0) {
                MessageHelper.send(console, AuthTG.getMessage("banusage", "MC"));
                return false;
            }
            if (strings.length < 3) {
                MessageHelper.send(console, AuthTG.getMessage("banusage", "MC"));
                return false;
            }

            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                MessageHelper.send(console, AuthTG.getMessage("banplayernotfound", "MC"));
                return false;
            }
            if (AuthTG.loader.isBanned(targetuuid)) {
                MessageHelper.send(console, AuthTG.getMessage("banalreadybanned", "MC"));
                return false;
            }
            if (reason.isEmpty()) {
                MessageHelper.send(console, AuthTG.getMessage("banusage", "MC"));
                return false;
            }
            if (reason.length() > 120) {
                MessageHelper.send(console, AuthTG.getMessage("banreasonlong", "MC"));
                return false;
            }

            Player target = Bukkit.getPlayer(targetuuid);
            LocalDateTime timedate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
            String time = timedate.format(formatter);

            String formattedDate = "";
            String message = "";
            int lettersCount = 0;

            for (int i = 0; i < strings[1].length(); i++) {
                if (Character.isAlphabetic(strings[1].charAt(i))) {
                    lettersCount++;
                }
            }

            if (lettersCount > 1) {
                MessageHelper.send(console, AuthTG.getMessage("bannotimeformat", "MC"));
            } else if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage("CONSOLE", reason, formattedDate, time);
            } else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage("CONSOLE", reason, formattedDate, time);
            } else if (strings[1].equals("-s")) {
                formattedDate = "0";
                message = buildBanMessage("CONSOLE", reason, "навсегда", formattedDate);
            } else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage("CONSOLE", reason, formattedDate, time);
            } else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                formattedDate = date.format(formatter);
                message = buildBanMessage("CONSOLE", reason, formattedDate, time);
            }

            AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, "CONSOLE");
            MessageHelper.send(console, AuthTG.getMessage("bansuccess", "MC").replace("{PLAYER}", strings[0]));
            if (target != null) {
                Handler.kick(target.getName(), MessageHelper.legacySection(message));
            }
        }

        return true;
    }

    private String buildBanMessage(String admin, String reason, String timeBan, String time) {
        return AuthTG.getMessage("ban", "MC")
                .replace("{ADMIN}", admin)
                .replace("{REASON}", reason)
                .replace("{TIMEBAN}", timeBan)
                .replace("{TIME}", time);
    }
}