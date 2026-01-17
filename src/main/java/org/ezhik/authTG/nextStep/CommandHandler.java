package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandTG.CommandCMDHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements NextStepHandler{
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        User target = User.getUser(update.getMessage().getText().toString());
        if (target == null) {
            user.sendMessage(AuthTG.getMessage("cmdusernotfound", "TG"));
            AuthTG.bot.remNextStepHandler(user.chatid);
            return;
        }
        List<String> commands = CommandCMDHandler.commands.get(user.chatid);
        commands.add(1, target.playername);
        CommandCMDHandler.commands.put(user.chatid, commands);
        if (commands.get(0).equals("list")) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton addbutton = new InlineKeyboardButton();
            InlineKeyboardButton removebutton = new InlineKeyboardButton();
            addbutton.setText(AuthTG.getMessage("cmdaddcommand", "TG"));
            removebutton.setText(AuthTG.getMessage("cmdremcommand", "TG"));
            addbutton.setCallbackData("cmdfirst_add");
            removebutton.setCallbackData("cmdfirst_rem");
            row.add(addbutton);
            row.add(removebutton);
            keyboard.add(row);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.chatid);
            String command = target.commands.toString().replace("[", "").replace("]", "");
            sendMessage.setText(AuthTG.getMessage("cmdlist", "TG").replace("{PLAYER}", target.playername).replace("{COMMANDS}", command));
            sendMessage.setReplyMarkup(markup);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error: " + e);
            }
            CommandCMDHandler.commands.remove(user.chatid);
            AuthTG.bot.remNextStepHandler(user.chatid);
        }
        if (commands.get(0).equals("add")) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton banbutton = new InlineKeyboardButton();
            InlineKeyboardButton mutebutton = new InlineKeyboardButton();
            InlineKeyboardButton kickbutton = new InlineKeyboardButton();
            banbutton.setText(AuthTG.getMessage("cmdban", "TG"));
            mutebutton.setText(AuthTG.getMessage("cmdmute", "TG"));
            kickbutton.setText(AuthTG.getMessage("cmdkick", "TG"));
            banbutton.setCallbackData("cmdsecond_ban");
            mutebutton.setCallbackData("cmdsecond_mute");
            kickbutton.setCallbackData("cmdsecond_kick");
            for (String key : AuthTG.macro.getKeys(false)) {
                InlineKeyboardButton keyButton = new InlineKeyboardButton();
                keyButton.setCallbackData("cmdsecond_" + key);
                keyButton.setText(AuthTG.macro.getString(key));
                row.add(keyButton);
            }
            row.add(banbutton);
            row.add(mutebutton);
            row.add(kickbutton);
            keyboard.add(row);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.chatid);
            sendMessage.setText(AuthTG.getMessage("cmdaddquestion", "TG").replace("{PLAYER}", target.playername));
            sendMessage.setReplyMarkup(markup);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error: " + e);
            }
            AuthTG.bot.remNextStepHandler(user.chatid);
        }
        if (commands.get(0).equals("rem")) {
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton banbutton = new InlineKeyboardButton();
            InlineKeyboardButton mutebutton = new InlineKeyboardButton();
            InlineKeyboardButton kickbutton = new InlineKeyboardButton();
            banbutton.setText(AuthTG.getMessage("cmdban", "TG"));
            mutebutton.setText(AuthTG.getMessage("cmdmute", "TG"));
            kickbutton.setText(AuthTG.getMessage("cmdkick", "TG"));
            banbutton.setCallbackData("cmdsecond_ban");
            mutebutton.setCallbackData("cmdsecond_mute");
            kickbutton.setCallbackData("cmdsecond_kick");
            for (String key : AuthTG.macro.getKeys(false)) {
                InlineKeyboardButton keyButton = new InlineKeyboardButton();
                keyButton.setCallbackData("cmdsecond_" + key);
                keyButton.setText(AuthTG.macro.getString(key));
                row.add(keyButton);
            }
            row.add(banbutton);
            row.add(mutebutton);
            row.add(kickbutton);
            keyboard.add(row);
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.chatid);
            sendMessage.setText(AuthTG.getMessage("cmdremquestion", "TG").replace("{PLAYER}", target.playername));
            sendMessage.setReplyMarkup(markup);
            try {
                AuthTG.bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("Error: " + e);
            }
            AuthTG.bot.remNextStepHandler(user.chatid);
        }
    }
}
