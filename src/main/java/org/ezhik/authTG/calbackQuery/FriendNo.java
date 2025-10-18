package org.ezhik.authTG.calbackQuery;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class FriendNo implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] args = update.getCallbackQuery().getData().toString().split("_");
        User playerUser = User.getUser(UUID.fromString(args[1]));
        User friendUser = User.getCurrentUser(update.getCallbackQuery().getMessage().getChatId());
        friendUser.sendMessage(AuthTG.getMessage("addfriendnosuccess", "TG").replace("{PLAYER}", playerUser.playername));
        if (playerUser.player != null) playerUser.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("addfriendno", "MC")));
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
