package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class LoginAcceptedNo implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().split("_");
        if (str.length < 2) return;

        User user = User.getUser(UUID.fromString(str[1]));
        if (user == null) {
            AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
            return;
        }

        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());

        Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
            AuthHandler.removeTimeout(user.uuid);

            Player player = Bukkit.getPlayer(user.playername);
            if (player != null) {
                player.kickPlayer(AuthTG.getMessage("loginnosuccess", "MC"));
            }
        });
    }
}
