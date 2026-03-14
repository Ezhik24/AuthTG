package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.TwoFactorAuthService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class LoginAcceptedYes implements CallbackQueryHandler {
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().split("_");
        if (str.length < 2) {
            return;
        }

        User user = User.getUser(UUID.fromString(str[1]));
        if (user == null) {
            AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
            return;
        }

        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());

        Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
            Player player = Bukkit.getPlayer(user.playername);
            if (player == null) {
                return;
            }

            TwoFactorAuthService.completeLogin(player);
        });
    }
}
