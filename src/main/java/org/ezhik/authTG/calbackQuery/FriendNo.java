package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        friendUser.sendMessage("Вы успешно отклонили запрос дружбы от " + playerUser.playername);
        if (playerUser.player != null) {
            playerUser.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВам отказали в дружбе"));
        }
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
