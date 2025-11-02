package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.KickAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class KickCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("kicknotactive", "TG"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("kick")) {
            String[] args = update.getMessage().getText().toString().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.getMessage("kick", "TG"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new KickAskHandler());
            } else {
                if (args.length < 3) {
                    user.sendMessage(AuthTG.getMessage("kickusage", "TG"));
                    return;
                }
                String message = String.join(" ", args).substring(args[0].length() + 1).substring(args[1].length() + 1);
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage(AuthTG.getMessage("kickusernotfound", "TG"));
                    return;
                }
                if (user1.player == null) {
                    user.sendMessage(AuthTG.getMessage("kickusernotonline", "TG"));
                    return;
                }
                Handler.kick(user1.playername, message);
                user.sendMessage(AuthTG.getMessage("kickuser", "TG").replace("{PLAYER}", user1.playername));
            }
        } else user.sendMessage(AuthTG.getMessage("kicknoperm", "TG"));
    }
}
