package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.User;

public class RemFriendCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lКоманда введена неверно. Введите команду так: /remfriend <ник>"));
        } else {
            Player player = (Player) commandSender;
            User user = User.getUser(player.getUniqueId());
            if (user == null){
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lПривяжите аккаунт к телеграмму"));
            }else{
                commandSender.sendMessage(user.remFriend(strings[0]));
            }

        }
        return true;
    }
}
