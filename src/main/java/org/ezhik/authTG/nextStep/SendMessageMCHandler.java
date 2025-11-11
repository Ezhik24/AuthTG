package org.ezhik.authTG.nextStep;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendMessageMCHandler implements NextStepHandler {
    @Override
    public void execute(Update update) {
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        User senderuser = User.getCurrentUser(update.getMessage().getChatId());
        User frienduser = User.getUser(AuthTG.bot.getUserData(update.getMessage().getChatId().toString()));
        if (frienduser == null) {
            senderuser.sendMessage(AuthTG.getMessage("sendmsgmcerror", "TG"));
        } else {
            frienduser.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("sendmsgmc", "MC").replace("{PLAYER}", senderuser.playername) + update.getMessage().getText()));
            senderuser.sendMessage(AuthTG.getMessage("sendmsgmcsuccess","TG"));
        }
        AuthTG.bot.remUserData(update.getMessage().getChatId().toString());
    }
}
