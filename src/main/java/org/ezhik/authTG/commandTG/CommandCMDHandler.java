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
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.cmdnull"));
            return;
        }
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.cmdnotactive"));
            return;
        }
        commands.remove(update.getMessage().getChatId());
        if (!user.isadmin) {
            user.sendMessage(AuthTG.config.getString("messages.telegram.cmdnotadmin"));
            return;
        }
        String[] args = update.getMessage().getText().split(" ");
        if (args.length == 1) {
            List<List<InlineKeyboardButton>> commands = new ArrayList<>();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton buttonadd = new InlineKeyboardButton();
            buttonadd.setText(AuthTG.config.getString("messages.telegram.cmdaddcommand"));
            buttonadd.setCallbackData("cmdfirst_add");
            InlineKeyboardButton buttonrem = new InlineKeyboardButton();
            buttonrem.setText(AuthTG.config.getString("messages.telegram.cmdremcommand"));
            buttonrem.setCallbackData("cmdfirst_rem");
            InlineKeyboardButton buttonlist = new InlineKeyboardButton();
            buttonlist.setText(AuthTG.config.getString("messages.telegram.cmdlistcommand"));
            buttonlist.setCallbackData("cmdfirst_list");
            buttons.add(buttonadd);
            buttons.add(buttonrem);
            buttons.add(buttonlist);
            commands.add(buttons);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(commands);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(AuthTG.config.getString("messages.telegram.cmdfirst"));
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            if (args[1].equals("add")) {
                if (args.length != 4) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdaddusage"));
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdusernotfound"));
                    return;
                }
                if (args[3].equals("ban") || args[3].equals("mute") || args[3].equals("kick")) {
                    if (target.commands != null && target.commands.contains(args[3])) {
                        user.sendMessage(AuthTG.config.getString("messages.telegram.cmdalreadyhas"));
                        return;
                    }
                    AuthTG.loader.addCommand(target.uuid, args[3]);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdaddsuccess").replace("{PERMISSION}", args[3]).replace("{PLAYER}", target.playername));
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdadded").replace("{COMMAND}", args[3]));
                    if (target.player != null) target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.telegram.cmdadded").replace("{COMMAND}", args[3])));
                } else user.sendMessage(AuthTG.config.getString("messages.telegram.cmdaddusage"));
            } else if (args[1].equals("rem")) {
                if (args.length != 4) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdremusage"));
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdusernotfound"));
                    return;
                }
                if (args[3].equals("ban") || args[3].equals("mute") || args[3].equals("kick")) {
                    if (target.commands != null && !target.commands.contains(args[3])) {
                        user.sendMessage(AuthTG.config.getString("messages.telegram.cmdnoperm"));
                        return;
                    }
                    AuthTG.loader.removeCommand(target.uuid, args[3]);
                    if (target.activetg) target.sendMessage(AuthTG.config.getString("messages.telegram.cmdrem").replace("{COMMAND}", args[3]));
                    if (target.player != null) target.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.telegram.cmdrem").replace("{COMMAND}", args[3])));
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdremsuccess").replace("{PERMISSION}", args[3]).replace("{PLAYER}", target.playername));
                } else user.sendMessage(AuthTG.config.getString("messages.telegram.cmdremusage"));
            } else if (args[1].equals("list")) {
                if (args.length != 3) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdlistusage"));
                    return;
                }
                User target = User.getUser(args[2]);
                if (target == null) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdusernotfound"));
                    return;
                }
                if (target.commands != null && target.commands.isEmpty()) {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.cmdlistempty"));
                    return;
                }
                String commands = target.commands.toString().replace("[", "").replace("]", "");
                user.sendMessage(AuthTG.config.getString("messages.telegram.cmdlist").replace("{PLAYER}", target.playername).replace("{COMMANDS}", commands));
            } else {
                user.sendMessage(AuthTG.config.getString("messages.telegram.cmdusage"));
            }
        }
    }
}
