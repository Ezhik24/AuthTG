package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;

public class RegisterCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println("[AuthTG] This command can only be used by players!");
            return false;
        }
        if (AuthTG.config.getBoolean("notRegAndLogin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lКоманда отключена"));
            return false;
        }
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lКоманда введена неверно,введите: /register <пароль> <повтор пароля>"));
            return false;
        }
        Player player = (Player) commandSender;
        if (AuthTG.loader.isActive(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &c&lВы уже зарегистрированы.Если Вы хотите сбросить аккаунт обратитесь к Администратору"));
            return false;
        }
        if (strings[0].length() < AuthTG.config.getInt("minLenghtPassword") || strings[0].length() > AuthTG.config.getInt("maxLenghtPassword")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lДлина пароля должна быть от " + AuthTG.config.getInt("minLenghtPassword") + " до " + AuthTG.config.getInt("maxLenghtPassword")+ " символов"));
            return false;
        }
        if (!strings[0].equals(strings[1])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &c&lПароли не совпадают"));
            return false;
        }
        AuthTG.loader.setPlayerName(player.getUniqueId(), player.getName());
        AuthTG.loader.setPasswordHash(player.getUniqueId(),strings[0]);
        AuthTG.loader.setActive(player.getUniqueId(), true);
        if (AuthTG.config.getBoolean("authNecessarily")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &a&lПривяжите аккаунт к Телеграмму"));
            MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &a&lПривяжите аккаунт к Телеграмму"));
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c&lПривяжите аккаунт"), "через Телеграм", 0,1000000000,0);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lВы успешно зарегистрировались"));
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
        }
        return true;
    }
}
