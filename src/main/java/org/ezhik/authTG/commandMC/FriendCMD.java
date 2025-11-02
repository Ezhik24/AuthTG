package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FriendCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO,AuthTG.getMessage("notplayer", "CE"));
            return false;
        }
        User friendUser;
        Player player = (Player) commandSender;
        User user = User.getUser(player.getUniqueId());
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendusage", "MC")));
            return false;
        }
        if (!user.activetg) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendnotg", "MC")));
            return false;
        }
        if (strings[0].equals("add")) {
            if (strings.length != 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendaddusage", "MC")));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            if (player.getName().equals(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendself", "MC")));
                return false;
            }
            if (friendUser == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendnotfound", "MC").replace("{PLAYER}", strings[1])));
                return false;
            }
            if (user.friends != null && user.friends.contains(friendUser.uuid)) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendalready", "MC").replace("{PLAYER}", strings[1])));
                return false;
            }
            AuthTG.loader.setCurrentUUID(friendUser.uuid, friendUser.chatid);
            InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton yesbtn = new InlineKeyboardButton();
            InlineKeyboardButton nobtn = new InlineKeyboardButton();
            yesbtn.setText(AuthTG.getMessage("friendaddyes", "TG"));
            yesbtn.setCallbackData("addfrys_" + player.getUniqueId());
            nobtn.setText(AuthTG.getMessage("friendaddno", "TG"));
            nobtn.setCallbackData("addfrno_" + player.getUniqueId());
            colkeyb.add(yesbtn);
            colkeyb.add(nobtn);
            List<List<InlineKeyboardButton>> rowkeyb = new ArrayList<>();
            rowkeyb.add(colkeyb);
            keyb.setKeyboard(rowkeyb);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(friendUser.chatid);
            sendMessage.setText(AuthTG.getMessage("friendaddtext", "TG").replace("{PLAYER}", player.getName()));
            sendMessage.setReplyMarkup(keyb);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendaddtext", "MC").replace("{PLAYER}", friendUser.playername)));
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e);
            }
        }
        if (strings[0].equals("list")) {
            if (user.friends == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendtellnofriends", "MC")));
                return false;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendlist", "MC")));
            for (String fr : user.friends) {
                friendUser = User.getUser(fr);
                if (friendUser.player != null) player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendlistonline", "MC").replace("{PLAYER}", fr)));
                else player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendlistoffline", "MC").replace("{PLAYER}", fr)));
            }
        }
        if (strings[0].equals("rem")) {
            if (strings.length != 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendremusage", "MC")));
                return false;
            }
            if (user.friends == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendremnofriends", "MC")));
                return false;
            }
            if (!user.friends.contains(strings[1])) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("frieendremnotfriend", "MC").replace("{PLAYER}", strings[1])));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            AuthTG.loader.removeFriend(user.uuid, strings[1]);
            AuthTG.loader.removeFriend(friendUser.uuid, player.getName());
            friendUser.sendMessage(AuthTG.getMessage("friendremfriend", "TG").replace("{PLAYER}", player.getName()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendremfriend", "MC").replace("{PLAYER}", strings[1])));
        }
        if (strings[0].equals("tell")) {
            if (strings.length != 3) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendtellusage", "MC")));
                return false;
            }
            if (user.friends == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendtellnofriends", "MC")));
                return false;
            }
            if (!user.friends.contains(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendtellnotfriend", "MC").replace("{PLAYER}", strings[1])));
                return false;
            }
            if (strings[2] == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendtelllenght", "MC")));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendtell", "MC").replace("{PLAYER}", strings[1])));
            AuthTG.loader.setCurrentUUID(friendUser.uuid, friendUser.chatid);
            friendUser.sendMessageFriend(AuthTG.getMessage("friendtell", "TG").replace("{PLAYER}", player.getName()).replace("{MESSAGE}", strings[2]), player.getUniqueId());
        }
        return true;
    }
}
