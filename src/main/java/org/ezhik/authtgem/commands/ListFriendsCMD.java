package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.User;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class ListFriendsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        User user = User.getUser(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&b&lMT&f&l] &c&lСписок друзей:"));
        for (String friend : user.friends) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l" + friend + " " + User.getplayerstatus(friend)));
        }

        return true;
    }


}
