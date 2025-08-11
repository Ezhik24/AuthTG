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

public class FriendCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
        User friendUser;
        Player player = (Player) commandSender;
        User user = User.getUser(player.getUniqueId());
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendusage")));
            return false;
        }
        if (!user.activetg) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendnotg")));
            return false;
        }
        if (strings[0].equals("add")) {
            if (strings.length != 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendaddusage")));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            if (player.getName().equals(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendself")));
                return false;
            }
            if (friendUser == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendnotfound").replace("{PLAYER}", strings[1])));
                return false;
            }
            if (user.friends != null && user.friends.contains(friendUser.uuid)) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendalready").replace("{PLAYER}", strings[1])));
                return false;
            }
            AuthTG.loader.setCurrentUUID(friendUser.uuid, friendUser.chatid);
            InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton yesbtn = new InlineKeyboardButton();
            InlineKeyboardButton nobtn = new InlineKeyboardButton();
            yesbtn.setText(AuthTG.config.getString("messages.telegram.friendaddyes"));
            yesbtn.setCallbackData("addfrys_" + player.getUniqueId());
            nobtn.setText(AuthTG.config.getString("messages.telegram.friendaddno"));
            nobtn.setCallbackData("addfrno_" + player.getUniqueId());
            colkeyb.add(yesbtn);
            colkeyb.add(nobtn);
            List<List<InlineKeyboardButton>> rowkeyb = new ArrayList<>();
            rowkeyb.add(colkeyb);
            keyb.setKeyboard(rowkeyb);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(friendUser.chatid);
            sendMessage.setText(AuthTG.config.getString("messages.telegram.friendaddtext").replace("{PLAYER}", player.getName()));
            sendMessage.setReplyMarkup(keyb);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendaddtext").replace("{PLAYER}", friendUser.playername)));
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e);
            }
        }
        if (strings[0].equals("list")) {
            if (user.friends == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendtellnofriends")));
                return false;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendlist")));
            for (String fr : user.friends) {
                friendUser = User.getUser(fr);
                if (friendUser.player != null) player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendlistonline").replace("{PLAYER}", fr)));
                else player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendlistoffline").replace("{PLAYER}", fr)));
            }
        }
        if (strings[0].equals("rem")) {
            if (strings.length != 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendremusage")));
                return false;
            }
            if (user.friends == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendremnofriends")));
                return false;
            }
            if (!user.friends.contains(strings[1])) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.frieendremnotfriend").replace("{PLAYER}", strings[1])));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            AuthTG.loader.removeFriend(user.uuid, strings[1]);
            AuthTG.loader.removeFriend(friendUser.uuid, player.getName());
            friendUser.sendMessage(AuthTG.config.getString("messages.telegram.friendremfriend").replace("{PLAYER}", player.getName()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendremfriend").replace("{PLAYER}", strings[1])));
        }
        if (strings[0].equals("tell")) {
            if (strings.length != 3) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendtellusage")));
                return false;
            }
            if (user.friends == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendtellnofriends")));
                return false;
            }
            if (!user.friends.contains(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendtellnotfriend").replace("{PLAYER}", strings[1])));
                return false;
            }
            if (strings[2] == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendtelllenght")));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.friendtell").replace("{PLAYER}", strings[1])));
            AuthTG.loader.setCurrentUUID(friendUser.uuid, friendUser.chatid);
            friendUser.sendMessageFriend(AuthTG.config.getString("messages.telegram.friendtell").replace("{PLAYER}", player.getName()).replace("{MESSAGE}", strings[2]), player.getUniqueId());
        }
        return true;
    }
}
