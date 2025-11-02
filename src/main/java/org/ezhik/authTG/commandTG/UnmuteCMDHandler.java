package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.nextStep.UnmuteAskHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnmuteCMDHandler implements CommandHandler{
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user == null) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("unmuteactive", "TG"));
            return;
        }
        if (!user.activetg) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("unmutenottgactive", "TG"));
            return;
        }
        if (user.isadmin || user.commands != null && user.commands.contains("mute")) {
            String[] args = update.getMessage().getText().split(" ");
            if (args.length < 2) {
                user.sendMessage(AuthTG.getMessage("unmute", "TG"));
                AuthTG.bot.setNextStepHandler(update.getMessage().getChatId(), new UnmuteAskHandler());
            } else {
                User user1 = User.getUser(args[1]);
                if (user1 == null) {
                    user.sendMessage(AuthTG.getMessage("unmuteusernotfound", "TG"));
                    return;
                }
                if (!AuthTG.loader.isMuted(user1.uuid)) {
                    user.sendMessage(AuthTG.getMessage("unmuteusernotmuted", "TG"));
                    return;
                }
                AuthTG.loader.deleteMute(user1.uuid);
                user.sendMessage(AuthTG.getMessage("unmuteuser", "TG").replace("{PLAYER}", user1.playername));
            }
        } else {
            user.sendMessage(AuthTG.getMessage("unmuteadmin", "TG"));
        }

    }
}
