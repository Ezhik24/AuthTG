package org.ezhik.authTG.calbackQuery;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class DelFriends implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
        User user = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
        User friend = User.getUser(UUID.fromString(str[1]));
        AuthTG.loader.removeFriend(user.uuid, friend.playername);
        AuthTG.loader.removeFriend(friend.uuid, user.playername);
        user.sendMessage(AuthTG.getMessage("delfrmessage", "TG").replace("{PLAYER}", friend.playername));
        friend.sendMessage(AuthTG.getMessage("delfrmessagefr", "TG").replace("{PLAYER}", user.playername));
    }
}
