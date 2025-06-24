package org.ezhik.authTG.calbackQuery;

import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.Handler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class LoginAcceptedNo implements CallbackQueryHandler{
    @Override
    public void execute(Update update, UUID uuid) {
        User user = User.getUser(uuid);
        Handler.kick(user.playername, "Владелец отклонил вход в учетную запись");
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
    }
}
