package org.ezhik.authTG.callbackQueryTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.util.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class FriendYes implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().split("_");
        User playerUser = User.getUser(UUID.fromString(str[1]));
        User friendUser = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());

        AuthTG.loader.addFriend(playerUser.uuid, friendUser.playername);
        AuthTG.loader.addFriend(friendUser.uuid, playerUser.playername);

        playerUser.sendMessage(AuthTG.getMessage("addfriendyes", "TG").replace("{PLAYER}", friendUser.playername));
        friendUser.sendMessage(AuthTG.getMessage("addfriendyessuccess", "TG").replace("{PLAYER}", playerUser.playername));

        if (playerUser.player != null) {
            MessageHelper.send(
                    playerUser.player,
                    AuthTG.getMessage("addfriendyes", "MC").replace("{PLAYER}", friendUser.playername)
            );
        }

        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}