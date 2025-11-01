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
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("cmdnull", "TG"));
            return;
        }
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("cmdnotactive", "TG"));
            return;
        }
        commands.remove(update.getMessage().getChatId());
        if (!user.isadmin) {
            user.sendMessage(AuthTG.getMessage("cmdnotadmin", "TG"));
            return;
        }
        String[] args = update.getMessage().getText().split(" ");
        if (args.length == 1) {
            List<List<InlineKeyboardButton>> commands = new ArrayList<>();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton buttonadd = new InlineKeyboardButton();
            buttonadd.setText(AuthTG.getMessage("cmdaddcommand", "TG"));
            buttonadd.setCallbackData("cmdfirst_add");
            InlineKeyboardButton buttonrem = new InlineKeyboardButton();
            buttonrem.setText(AuthTG.getMessage("cmdremcommand", "TG"));
            buttonrem.setCallbackData("cmdfirst_rem");
            InlineKeyboardButton buttonlist = new InlineKeyboardButton();
            buttonlist.setText(AuthTG.getMessage("cmdlistcommand", "TG"));
            buttonlist.setCallbackData("cmdfirst_list");
            buttons.add(buttonadd);
            buttons.add(buttonrem);
            buttons.add(buttonlist);
            commands.add(buttons);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(commands);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(AuthTG.getMessage("cmdfirst", "TG"));
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            if (args[1].equals("add")) {
                if (args.length != 4) {
                    user.sendMessage(AuthTG.getMessage("cmdaddusage", "TG"));
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(AuthTG.getMessage("cmdusernotfound", "TG"));
                    return;
                }
                if (args[3].equals("ban") || args[3].equals("mute") || args[3].equals("kick")) {
                    if (target.commands != null && target.commands.contains(args[3])) {
                        user.sendMessage(AuthTG.getMessage("cmdalreadyhas", "TG"));
                        return;
                    }
                    AuthTG.loader.addCommand(target.uuid, args[3]);
                    user.sendMessage(AuthTG.getMessage("cmdaddsuccess", "TG").replace("{PERMISSION}", args[3]).replace("{PLAYER}", target.playername));
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdadded", "TG").replace("{COMMAND}", args[3]));
                    if (target.player != null) target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdadded", "TG").replace("{COMMAND}", args[3])));
                } else user.sendMessage(AuthTG.getMessage("cmdaddusage", "TG"));
            } else if (args[1].equals("rem")) {
                if (args.length != 4) {
                    user.sendMessage(AuthTG.getMessage("cmdremusage", "TG"));
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(AuthTG.getMessage("cmdusernotfound", "TG"));
                    return;
                }
                if (args[3].equals("ban") || args[3].equals("mute") || args[3].equals("kick")) {
                    if (target.commands != null && !target.commands.contains(args[3])) {
                        user.sendMessage(AuthTG.getMessage("cmdnoperm", "TG"));
                        return;
                    }
                    AuthTG.loader.removeCommand(target.uuid, args[3]);
                    if (target.activetg) target.sendMessage(AuthTG.getMessage("cmdrem", "TG").replace("{COMMAND}", args[3]));
                    if (target.player != null) target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("cmdrem", "TG").replace("{COMMAND}", args[3])));
                    user.sendMessage(AuthTG.getMessage("cmdremsuccess", "TG").replace("{PERMISSION}", args[3]).replace("{PLAYER}", target.playername));
                } else user.sendMessage(AuthTG.getMessage("cmdremusage", "TG"));
            } else if (args[1].equals("list")) {
                if (args.length != 3) {
                    user.sendMessage(AuthTG.getMessage("cmdlistusage", "TG"));
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(AuthTG.getMessage("cmdusernotfound", "TG"));
                    return;
                }
                if (target.commands != null && target.commands.isEmpty()) {
                    user.sendMessage(AuthTG.getMessage("cmdlistempty", "TG"));
                    return;
                }
                String commands = target.commands.toString().replace("[", "").replace("]", "");
                user.sendMessage(AuthTG.getMessage("cmdlist", "TG").replace("{PLAYER}", target.playername).replace("{COMMANDS}", commands));
            } else {
                user.sendMessage(AuthTG.getMessage("cmdusage", "TG"));
            }
        }
    }
}
