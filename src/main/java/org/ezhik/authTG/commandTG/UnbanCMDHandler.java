package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.UnbanAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnbanCMDHandler implements CommandHandler{
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user == null) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("unbanactive", "TG"));
            return;
        }
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("unbannottgactive", "TG"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("ban")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.getMessage("unban", "TG"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new UnbanAskHandler());
            } else {
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage(AuthTG.getMessage("unbanusernotfound", "TG"));
                    return;
                }
                if (!AuthTG.loader.isBanned(user1.uuid)) {
                    user.sendMessage(AuthTG.getMessage("unbanusernotbanned", "TG"));
                    return;
                }
                AuthTG.loader.deleteBan(user1.uuid);
                user.sendMessage(AuthTG.getMessage("unbanuser", "TG").replace("{PLAYER}", user1.playername));
            }
        } else {
            user.sendMessage(AuthTG.getMessage("unbannotadmin", "TG"));
        }
    }
}
