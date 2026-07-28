package org.ezhik.authTG.callbackQueryTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.util.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class FriendNo implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] args = update.getCallbackQuery().getData().split("_");
        User playerUser = User.getUser(UUID.fromString(args[1]));
        User friendUser = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());

        friendUser.sendMessage(AuthTG.getMessage("addfriendnosuccess", "TG").replace("{PLAYER}", playerUser.playername));

        if (playerUser.player != null) {
            MessageHelper.send(playerUser.player, AuthTG.getMessage("addfriendno", "MC"));
        }

        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}