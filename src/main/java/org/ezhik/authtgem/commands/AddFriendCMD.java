package org.ezhik.authtgem.commands;

import org.bukkit.Bukkit;
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

public class AddFriendCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "[MT] Команда введена неверно. Введите команду так: /addfriend <ник>");
        } else {
            Player player1 = (Player) commandSender;
            User user1 = User.getUser(player1.getUniqueId());
            if (user1 == null) {
                commandSender.sendMessage(ChatColor.RED + "[MT] Привяжите аккаунт к телеграмму!");
            } else {
                if (user1.friends.contains(strings[0]) || user1.playername == strings[0]) {
                    commandSender.sendMessage(ChatColor.RED + "[MT] Такой игрок уже есть в друзьях");
                } else {
                    InlineKeyboardMarkup keyb = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> colkeyb = new ArrayList<>();
                    InlineKeyboardButton yesbtn = new InlineKeyboardButton();
                    InlineKeyboardButton nobtn = new InlineKeyboardButton();
                    yesbtn.setText("Да");
                    yesbtn.setCallbackData("addfrys" + commandSender.getName());
                    nobtn.setText("Нет");
                    nobtn.setCallbackData("addfrno" + commandSender.getName());
                    colkeyb.add(yesbtn);
                    colkeyb.add(nobtn);
                    List<List<InlineKeyboardButton>> rowkeyb = new ArrayList<>();
                    rowkeyb.add(colkeyb);
                    keyb.setKeyboard(rowkeyb);
                    SendMessage sendMessage = new SendMessage();

                    User user = User.getUser(strings[0]);
                    if (user != null) {
                        sendMessage.setChatId(user.chatid);
                        sendMessage.setText("Вы хотите добавить " + commandSender.getName() + " в друзья?");
                        sendMessage.setReplyMarkup(keyb);
                        try {
                            AuthTGEM.bot.execute(sendMessage);
                        } catch (TelegramApiException e) {
                            System.out.println("Error sending message: " + e);
                            }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "[MT] У игрока не привязан аккаунт к Телеграмму!");
                        }
                    }

            }
        }
        return true;
    }
}
