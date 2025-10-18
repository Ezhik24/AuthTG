package org.ezhik.authTG.calbackQuery;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class FriendYes implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        User playerUser = User.getUser(UUID.fromString(str[1]));
        User friendUser = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
        AuthTG.loader.addFriend(playerUser.uuid, friendUser.playername);
        AuthTG.loader.addFriend(friendUser.uuid, playerUser.playername);
        playerUser.sendMessage(AuthTG.getMessage("addfriendyes", "TG").replace("{PLAYER}", friendUser.playername));
        friendUser.sendMessage(AuthTG.getMessage("addfriendyessuccess", "TG").replace("{PLAYER}", playerUser.playername));
        if (playerUser.player != null) playerUser.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("addfriendyes", "MC").replace("{PLAYER}", friendUser.playername)));
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
