package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
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
import java.util.UUID;

public class FriendCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player;
        Player friend;
        User user;
        User friendUser;
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lИспользуйте: /friend <add | rem | list | tell>!"));
            return false;
        }
        if (strings[0].equals("add")) {
            if (strings.length != 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lИспользуйте: /friend add <player>!"));
                return false;
            }
            player = (Player) commandSender;
            user = User.getUser(player.getUniqueId());
            friendUser = User.getUser(strings[1]);
            if (!user.activetg) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы не привязали аккаунт!"));
                return false;
            }
            if (friendUser == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lУ игрока &f&l" + strings[1] + " &c&lне привязан аккаунт!"));
                return false;
            }
            if (user.friends != null && user.friends.contains(friendUser.uuid)) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы уже друзья с &f&l" + friendUser.playername + "&c&l!"));
                return false;
            }
            InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton yesbtn = new InlineKeyboardButton();
            InlineKeyboardButton nobtn = new InlineKeyboardButton();
            yesbtn.setText("Да");
            yesbtn.setCallbackData("addfrys_" + player.getName() + "_" + friendUser.playername);
            nobtn.setText("Нет");
            nobtn.setCallbackData("addfrno_" + player.getName() + "_" + friendUser.playername);
            colkeyb.add(yesbtn);
            colkeyb.add(nobtn);
            List<List<InlineKeyboardButton>> rowkeyb = new ArrayList<>();
            rowkeyb.add(colkeyb);
            keyb.setKeyboard(rowkeyb);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(friendUser.chatid);
            sendMessage.setText("[Бот] Вы получили запрос дружбы от " + player.getName() + "! Хотите его принять?");
            sendMessage.setReplyMarkup(keyb);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы отправили запрос дружбы &f&l" + friendUser.playername + "&c&l!"));
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e);
            }
        }
        return true;
    }
}
