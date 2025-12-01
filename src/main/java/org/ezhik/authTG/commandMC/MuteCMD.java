package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MuteCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("authtg.mute")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mutenoperm", "MC")));
                return false;
            }
            if (strings.length < 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteusage", "MC")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteplnotfound", "MC")));
                return false;
            }
            Player target = Bukkit.getPlayer(targetuuid);
            if (AuthTG.loader.isMuted(targetuuid)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteplmuted", "MC")));
                return false;
            }
            if (reason.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteusage", "MC")));
                return false;
            }
            if (reason.length() > 120) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mutetoolong", "MC")));
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
            String formattedDate = "", message = "";
            if (lettersCount > 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mutetimeformat", "MC")));
            }
            else if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", player.getName());
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", player.getName());
            }
            else if (strings[1].equals("-s")) {
                formattedDate = "0";
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", player.getName());
            }
            else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", player.getName());
            }
            else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}",time).replace("{ADMIN}", player.getName());
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("successmute", "MC").replace("{PLAYER}", strings[0])));
            if (target != null) target.sendMessage(message);
            AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, player.getName());
            return true;
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length < 3) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteusage", "MC")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteplnotfound", "MC")));
                return false;
            }
            Player target = Bukkit.getPlayer(targetuuid);
            if (AuthTG.loader.isMuted(targetuuid)) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteplmuted", "MC")));
                return false;
            }
            if (reason.isEmpty()) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("muteusage", "MC")));
                return false;
            }
            if (reason.length() > 120) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mutetoolong", "MC")));
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
            String formattedDate = "", message = "";
            if (lettersCount > 1) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mutetimeformat", "MC")));
            }
            else if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", "CONSOLE");
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                formattedDate = date.format(formatter);
                    message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", "CONSOLE");
            }
            else if (strings[1].equals("-s")) {
                formattedDate = "0";
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", "CONSOLE");
            }
            else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}", time).replace("{ADMIN}", "CONSOLE");
            }
            else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                formattedDate = date.format(formatter);
                message = ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("mute", "MC")).replace("{TIMEMUTE}", formattedDate).replace("{REASON}", reason).replace("{TIME}",time).replace("{ADMIN}", "CONSOLE");
            }
            if (target != null) target.sendMessage(message);
            AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, "CONSOLE");
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("successmute", "MC").replace("{PLAYER}", strings[0])));
        }
        return true;
    }
}
