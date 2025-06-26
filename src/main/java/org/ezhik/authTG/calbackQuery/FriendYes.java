package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class FriendYes implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        User playerUser = User.getUser(str[1]);
        User friendUser = User.getUser(str[2]);
        AuthTG.loader.addFriend(playerUser.uuid, friendUser.uuid);
        AuthTG.loader.addFriend(friendUser.uuid, playerUser.uuid);
        playerUser.sendMessage("Вам добавили в друзья " + friendUser.playername);
        friendUser.sendMessage("Вы добавили в друзья " + playerUser.playername);
        if (playerUser.player != null) {
            playerUser.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &a&lВы добавили в друзья " + friendUser.playername));
        }
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
