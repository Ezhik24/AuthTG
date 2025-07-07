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
        User user;
        User friendUser;
        if (!(commandSender instanceof Player)) {
            System.out.println(AuthTG.config.get("messages.console.notplayer"));
            return false;
        }
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
            if (player.getName().equals(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы не можете добавить себя в друзья!"));
                return false;
            }
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
            AuthTG.loader.setCurrentUUID(friendUser.uuid, friendUser.chatid);
            InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> colkeyb = new ArrayList<>();
            InlineKeyboardButton yesbtn = new InlineKeyboardButton();
            InlineKeyboardButton nobtn = new InlineKeyboardButton();
            yesbtn.setText("Да");
            yesbtn.setCallbackData("addfrys_" + player.getUniqueId());
            nobtn.setText("Нет");
            nobtn.setCallbackData("addfrno_" + player.getUniqueId());
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
        if (strings[0].equals("list")) {
            player = (Player) commandSender;
            user = User.getUser(player.getUniqueId());
            if (!user.activetg) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы не привязали аккаунт!"));
                return false;
            }
            if (user.friends == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lУ вас нет друзей!"));
                return false;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВаши друзья:"));
            for (String fr : user.friends) {
                friendUser = User.getUser(fr);
                if (friendUser.player != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l" + fr + " &a&l[Online]"));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l" + fr + " &c&l[Offline]"));
                }
            }
        }
        if (strings[0].equals("rem")) {
            if (strings.length != 2) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lИспользуйте: /friend rem <игрок>!"));
                return false;
            }
            player = (Player) commandSender;
            user = User.getUser(player.getUniqueId());
            if (!user.activetg) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы не привязали аккаунт!"));
                return false;
            }
            if (user.friends == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lУ вас нет друзей!"));
                return false;
            }
            if (!user.friends.contains(strings[1])) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lУ вас нет друзей с именем &f&l" + strings[1] + "&c&l!"));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            AuthTG.loader.removeFriend(user.uuid, strings[1]);
            AuthTG.loader.removeFriend(friendUser.uuid, player.getName());
            friendUser.sendMessage("Ваш друг " + player.getName() + " удалил вас из друзей!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы удалили друга &f&l" + strings[1] + "&c&l!"));
        }
        if (strings[0].equals("tell")) {
            if (strings.length != 3) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lИспользуйте: /friend tell <игрок> <сообщение>!"));
                return false;
            }
            player = (Player) commandSender;
            user = User.getUser(player.getUniqueId());
            if (!user.activetg) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы не привязали аккаунт!"));
                return false;
            }
            if (user.friends == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lУ вас нет друзей!"));
                return false;
            }
            if (!user.friends.contains(strings[1])) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lУ вас нет друзей с именем &f&l" + strings[1] + "&c&l!"));
                return false;
            }
            if (strings[2] == null) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы не ввели сообщение!"));
                return false;
            }
            friendUser = User.getUser(strings[1]);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы отправили сообщение &f&l" + strings[1] + "&c&l!"));
            AuthTG.loader.setCurrentUUID(friendUser.uuid, friendUser.chatid);
            friendUser.sendMessageFriend("Ваш друг " + player.getName() + " отправил вам сообщение: " + strings[2], player.getUniqueId());
        }
        return true;
    }
}
