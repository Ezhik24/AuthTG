package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class FriendCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player;
        User user;
        if (strings.length == 0) {
            player = (Player) commandSender;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("friend_lenght")));
            return false;
        }
        switch (strings[0]) {
            case "tell":
                if (strings.length == 1) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_wrong_command")));
                    return false;
                }
                user = User.getUser(commandSender.getName());
                if (!user.friends.contains(strings[1])) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_friends_notfound")));
                    return false;
                }
                User friend = User.getUser(strings[1]);
                if (friend == null) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_friends_tgasign")));
                    return false;
                }
                player = (Player) commandSender;
                String message = AuthTGEM.messageTG.getFriendPNTell(player) + " " + String.join(" ", strings).substring(strings[0].length() + strings[1].length() + 2);
                friend.sendMessageB(message, commandSender.getName());
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("tellfriends_sendmessage_succes")));
                return true;
            case "list":
                player = (Player) commandSender;
                user = User.getUser(player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("listfriends_list")));
                for (String friend1 : user.friends) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("listfriends_friend")) + friend1 + " " + User.getplayerstatus(friend1));
                }
                return true;
            case "add":
                if (strings.length == 1) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("addfriends_wrong_command")));
                } else {
                    Player player1 = (Player) commandSender;
                    User user1 = User.getUser(player1.getUniqueId());
                    if (user1 == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("addfriends_tg_noasign")));
                    } else {
                        if (user1.friends.contains(strings[1]) || user1.playername == strings[1]) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("friend_already_added")));
                        } else {
                            InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
                            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
                            InlineKeyboardButton yesbtn = new InlineKeyboardButton();
                            InlineKeyboardButton nobtn = new InlineKeyboardButton();
                            yesbtn.setText(AuthTGEM.messageTG.get("addfriends_yes"));
                            yesbtn.setCallbackData("addfrys" + commandSender.getName());
                            nobtn.setText(AuthTGEM.messageTG.get("addfriends_no"));
                            nobtn.setCallbackData("addfrno" + commandSender.getName());
                            colkeyb.add(yesbtn);
                            colkeyb.add(nobtn);
                            List<List<InlineKeyboardButton>> rowkeyb = new ArrayList<>();
                            rowkeyb.add(colkeyb);
                            keyb.setKeyboard(rowkeyb);
                            SendMessage sendMessage = new SendMessage();
                            user = User.getUser(strings[1]);
                            if (user != null) {
                                sendMessage.setChatId(user.chatid);
                                sendMessage.setText(AuthTGEM.messageTG.getAddFriendsReq(commandSender));
                                sendMessage.setReplyMarkup(keyb);
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("addfriend_request_success")));
                                try {
                                    AuthTGEM.bot.execute(sendMessage);
                                } catch (TelegramApiException e) {
                                    System.out.println("Error sending message: " + e);
                                }
                            } else {
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("friend_tg_noasign")));
                            }
                        }

                    }
                }
                return true;
            case "rem":
                if (strings.length == 1) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("removefriend_wrong_command")));
                    return false;
                }
                player = (Player) commandSender;
                user = User.getUser(player.getUniqueId());
                if (user == null){
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("removefriend_tg_noasign")));
                }else{
                    commandSender.sendMessage(user.remFriend(strings[1]));
                }
                return true;
            default:
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTGEM.messageMC.get("friend_lenght")));
                break;
        }
        return true;
    }
}
