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

import java.util.UUID;

public class KickCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("authtg.kick")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cВы не можете использовать эту команду!"));
                return false;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно! Введите /kick <игрок> <причина>"));
                return false;
            }
            if (strings.length < 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно! Введите /kick <игрок> <причина>"));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + 1);
            UUID uuidtarget = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (uuidtarget == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cИгрок не найден!"));
                return false;
            }
            Player target = Bukkit.getPlayer(uuidtarget);
            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cИгрок не в сети!"));
                return false;
            }
            if (reason.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cВы не указали причину!"));
                return false;
            }
            Handler.kick(target.getName(), reason);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cВы успешно кикнули игрока " + target.getName() + "!"));
            return true;
        }else {
            ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
            if (strings.length == 0) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно! Введите /kick <игрок> <причина>"));
                return false;
            }
            if (strings.length < 2) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cКоманда введена неверно! Введите /kick <игрок> <причина>"));
                return false;
            }
            String reason = String.join(" ", strings).substring(strings[0].length() + 1);
            UUID uuidtarget = AuthTG.loader.getUUIDbyPlayerName(strings[0]);
            if (uuidtarget == null) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cИгрок не найден!"));
                return false;
            }
            Player target = Bukkit.getPlayer(uuidtarget);
            if (target == null) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cИгрок не в сети!"));
                return false;
            }
            if (reason.isEmpty()) {
                consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cВы не указали причину!"));
                return false;
            }
            Handler.kick(target.getName(), reason);
            consoleCommandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &cВы успешно кикнули игрока " + target.getName() + "!"));
            return true;
        }
    }
}
