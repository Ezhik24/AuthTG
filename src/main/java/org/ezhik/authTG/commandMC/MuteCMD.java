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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mutenoperm")));
                return false;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteusage")));
                return false;
            }
            if (strings.length < 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteusage")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteplnotfound")));
                return false;
            }
            Player target = Bukkit.getPlayer(targetuuid);
            if (AuthTG.loader.isMuted(targetuuid)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteplmuted")));
                return false;
            }
            if (reason.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteusage")));
                return false;
            }
            if (reason.length() > 120) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mutetoolong")));
                return false;
            }
            if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("-s")) {
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, "0", reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mutetimeformat")));
            }
            return true;
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length == 0) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteusage")));
                return false;
            }
            if (strings.length < 3) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteusage")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteplnotfound")));
                return false;
            }
            Player target = Bukkit.getPlayer(targetuuid);
            if (AuthTG.loader.isMuted(targetuuid)) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteplmuted")));
                return false;
            }
            if (reason.isEmpty()) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.muteusage")));
                return false;
            }
            if (reason.length() > 120) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mutetoolong")));
                return false;
            }
            if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(targetuuid)).replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("-s")) {
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String time = timedate.format(formatter);
                AuthTG.loader.setMuteTime(targetuuid, "0", reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.successmute").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mute")).replace("{TIMEMUTE}", "навсегда").replace("{REASON}", AuthTG.loader.getMuteReason(targetuuid)).replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(targetuuid)).replace("{ADMIN}", AuthTG.loader.getMuteAdmin(targetuuid)).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.mutetimeformat")));
            }
        }
        return true;
    }
}
