package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

public class BanCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("authtg.ban")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bannoperm")));
                return false;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banusage")));
                return false;
            }
            if (strings.length < 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banusage")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banplayernotfound")));
                return false;
            }
            if (AuthTG.loader.isBanned(targetuuid)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banalreadybanned")));
                return false;
            }
            if (reason.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banusage")));
                return false;
            }
            if (reason.length() > 120) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banreasonlong")));
                return false;
            }
            Player target = Bukkit.getPlayer(targetuuid);
            if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("-s")) {
                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, "0", reason, formattedDate, player.getName());
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}",formattedDate).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bannotimeformat")));
            }
            return true;
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            if (strings.length == 0) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banusage")));
                return false;
            }
            if (strings.length < 3) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banusage")));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banplayernotfound")));
                return false;
            }
            if (AuthTG.loader.isBanned(targetuuid)) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banalreadybanned")));
                return false;
            }
            if (reason.isEmpty()) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banusage")));
                return false;
            }
            if (reason.length() > 120) {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.banreasonlong")));
                return false;
            }
            Player target = Bukkit.getPlayer(targetuuid);
            if (strings[1].contains("d")) {
                LocalDateTime date = LocalDateTime.now().plusDays(Integer.parseInt(strings[1].replace("d", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", "CONSOLE").replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", "CONSOLE").replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("m")) {
                LocalDateTime date = LocalDateTime.now().plusMinutes(Integer.parseInt(strings[1].replace("m", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", "CONSOLE").replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("s")) {
                LocalDateTime date = LocalDateTime.now().plusSeconds(Integer.parseInt(strings[1].replace("s", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, "CONSOLE");
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bansuccess").replace("{PLAYER}", strings[0])));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", "CONSOLE").replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else if (strings[1].contains("-s")) {
                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, "0", reason, formattedDate, "CONSOLE");
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", "CONSOLE").replace("{REASON}", reason).replace("{TIMEBAN}", "навсегда").replace("{TIME}",formattedDate).replace("{BR}", "\n");
                if (target != null) Handler.kick(target.getName(), message);
            }
            else {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.bannotimeformat")));
            }
        }
        return true;
    }
}
