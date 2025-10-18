package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandTG.CommandCMDHandler;
import org.ezhik.authTG.nextStep.CommandHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class CMDFirstStep implements CallbackQueryHandler{
    @Override
    public void execute(Update update) {
        CommandCMDHandler.commands.remove(update.getCallbackQuery().getFrom().getId());
        User user = User.getCurrentUser(update.getCallbackQuery().getFrom().getId());
        user.sendMessage(AuthTG.getMessage("cmdfirstnick", "TG"));
        List<String> commands = new ArrayList<>();
        String[] command = update.getCallbackQuery().getData().split("_");
        if (command[1].equals("add")) commands.add(0, "add");
        if (command[1].equals("rem")) commands.add(0, "rem");
        if (command[1].equals("list")) commands.add(0, "list");
        CommandCMDHandler.commands.put(user.chatid, commands);
        AuthTG.bot.setNextStepHandler(user.chatid, new CommandHandler());
    }
}
