package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.User;

public class TellFriendsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_wrong_command")));
        } else {
            User user = User.getUser(commandSender.getName());
            if (!user.friends.contains(strings[0])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_friends_notfound")));
            } else {
                User friend = User.getUser(strings[0]);
                if (friend == null) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_friends_tgasign")));
                } else {
                    Player player = (Player) commandSender;
                    String message = String.join(" ", strings).replace(strings[0], AuthTGEM.messageTG.getFriendPNTell(player));
                    friend.sendMessageB(message, commandSender.getName());
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_sendmessage_succes")));
                }
            }
        }
        return true;
    }
}
