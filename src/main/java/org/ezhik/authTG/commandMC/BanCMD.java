package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BanCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("authtg.ban")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cУ вас нет прав."));
                return false;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно. Введите /ban <игрок> <время> <причина>"));
                return false;
            }
            if (strings.length < 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно. Введите /ban <игрок> <время> <причина>"));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
            UUID targetuuid = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (targetuuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cИгрок не найден."));
                return false;
            }
            if (AuthTG.loader.isBanned(targetuuid)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cИгрок уже забанен."));
                return false;
            }
            if (reason.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно. Введите /ban <игрок> <время> <причина>"));
                return false;
            }
            if (reason.length() > 120) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cПричина слишком длинная."));
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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &aИгрок " + strings[0] + " успешно забанен"));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else if (strings[1].contains("h")) {
                LocalDateTime date = LocalDateTime.now().plusHours(Integer.parseInt(strings[1].replace("h", "")));
                LocalDateTime timedate = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
                String formattedDate = date.format(formatter);
                String time = timedate.format(formatter);
                AuthTG.loader.setBanTime(targetuuid, formattedDate, reason, time, player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &aИгрок " + strings[0] + " успешно забанен"));
                String message = ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.ban")).replace("{ADMIN}", player.getName()).replace("{REASON}", reason).replace("{TIMEBAN}", formattedDate).replace("{TIME}", time).replace("{BR}", "\n");
                if (target != null) target.sendMessage(message);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cНеверный формат времени, используете: d,h,m,s или -s!"));
            }
            return true;
        }
        return true;
    }
}
