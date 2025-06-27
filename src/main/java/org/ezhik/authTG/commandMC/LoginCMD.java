package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class LoginCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println("[AuthTG] This command can only be used by players!");
            return false;
        }
        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &c&lКоманда введена неверна,введите: /login <пароль>"));
            return false;
        }
        Player player = (Player) commandSender;
        if (!AuthTG.loader.passwordWalid(player.getUniqueId(), strings[0])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &c&lНеверный пароль"));
            return false;
        }
        User user = User.getUser(player.getUniqueId());
        if (user.activetg && user.twofactor) {
            user.sendLoginAccepted("[Бот@" + user.playername +  "] Это вы вошли в игру?");
            MuterEvent.mute(player.getName(), ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &a&lПодтвердите вход через Телеграм"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &a&lПодтвердите вход через Телеграмм"));
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c&lПодтвердите вход"), "через Телеграм", 0,1000000000,0);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lУспешная авторизация"));
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
        }
        return true;
    }
}
