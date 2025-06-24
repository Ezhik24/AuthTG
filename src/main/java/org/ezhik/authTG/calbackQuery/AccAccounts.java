package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class AccAccounts implements CallbackQueryHandler{
    @Override
    public void execute(Update update, UUID uuid) {
        AuthTG.loader.setCurrentUUID(uuid, update.getCallbackQuery().getMessage().getChatId());
        User user = User.getUser(uuid);
        AuthTG.bot.sendMessage(update.getCallbackQuery().getMessage().getChatId(), "[Бот] Вы успешно выбрали аккаунт: " + user.playername);
    }
}
