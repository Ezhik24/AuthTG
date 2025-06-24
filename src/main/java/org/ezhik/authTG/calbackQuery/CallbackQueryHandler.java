package org.ezhik.authTG.calbackQuery;

import org.bukkit.entity.Player;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public interface CallbackQueryHandler {
    void execute(Update update, UUID uuid);
}
