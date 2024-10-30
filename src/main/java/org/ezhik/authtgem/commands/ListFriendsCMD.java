package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.User;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class ListFriendsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        User user = User.getUser(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("listfriends_list")));
        for (String friend : user.friends) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("listfriends_friend")) + friend + " " + User.getplayerstatus(friend));
        }

        return true;
    }


}
