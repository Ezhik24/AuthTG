package org.ezhik.authTG.commandTG;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCMDHandler implements CommandHandler {
    public static Map<Long, List<String>> commands = new HashMap<>();
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user == null) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), "[Бот] Вы не авторизованы!");
            return;
        }
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), "[Бот] Вы не привязали аккаунт!");
            return;
        }
        commands.remove(update.getMessage().getChatId());
        if (!user.isadmin) {
            user.sendMessage("Вы не являетесь администратором!");
            return;
        }
        String[] args = update.getMessage().getText().split(" ");
        if (args.length == 1) {
            List<List<InlineKeyboardButton>> commands = new ArrayList<>();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton buttonadd = new InlineKeyboardButton();
            buttonadd.setText("Добавить");
            buttonadd.setCallbackData("cmdfirst_add");
            InlineKeyboardButton buttonrem = new InlineKeyboardButton();
            buttonrem.setText("Удалить");
            buttonrem.setCallbackData("cmdfirst_rem");
            InlineKeyboardButton buttonlist = new InlineKeyboardButton();
            buttonlist.setText("Список");
            buttonlist.setCallbackData("cmdfirst_list");
            buttons.add(buttonadd);
            buttons.add(buttonrem);
            buttons.add(buttonlist);
            commands.add(buttons);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(commands);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("[Бот] Выберите действие:");
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            if (args[1].equals("add")) {
                if (args.length != 4) {
                    user.sendMessage("Введите /command add <никнейм> <ban | kick | mute>!");
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(" Пользователь не найден!");
                    return;
                }
                if (args[3].equals("ban") || args[3].equals("mute") || args[3].equals("kick")) {
                    if (target.commands != null && target.commands.contains(args[3])) {
                        user.sendMessage(" Пользователь уже имеет право на это действие!");
                        return;
                    }
                    AuthTG.loader.addCommand(target.uuid, args[3]);
                    user.sendMessage(" Вы успешно добавили право " + args[3] + " пользователю " + target.username + "!");
                    if (target.activetg) target.sendMessage(" Вы получили право " + args[3] + "!");
                    if (target.player != null) target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВы получили право " + args[3] + "!"));
                } else user.sendMessage("Введите /command add <никнейм> <ban | kick | mute>!");
            } else if (args[1].equals("rem")) {
                if (args.length != 4) {
                    user.sendMessage("Введите /command rem <никнейм> <ban | kick | mute>!");
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage("Пользователь не найден!");
                    return;
                }
                if (args[3].equals("ban") || args[3].equals("mute") || args[3].equals("kick")) {
                    if (target.commands != null && !target.commands.contains(args[3])) {
                        user.sendMessage("Пользователь не имеет право на это действие!");
                        return;
                    }
                    AuthTG.loader.removeCommand(target.uuid, args[3]);
                    if (target.activetg) target.sendMessage(" Вы потеряли право " + args[3] + "!");
                    if (target.player != null) target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[AuthTG] &cВы потеряли право " + args[3] + "!"));
                    user.sendMessage(" Вы успешно удалили право " + args[3] + " у пользователя " + target.username + "!");
                } else user.sendMessage(" Введите /command rem <никнейм> <ban | kick | mute>!");
            } else if (args[1].equals("list")) {
                if (args.length != 3) {
                    user.sendMessage(" Введите /command list <никнейм>!");
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(" Пользователь не найден!");
                    return;
                }
                if (target.commands != null && target.commands.isEmpty()) {
                    user.sendMessage(" Пользователь не имеет прав!");
                    return;
                }
                user.sendMessage(" Пользователь " + target.username + " имеет права: " + target.commands.toString().replace("[", "").replace("]", "") + "!");
            } else {
                user.sendMessage(" Введите /command <add | rem | list>!");
            }
        }
    }
}
