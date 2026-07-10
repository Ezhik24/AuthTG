package org.ezhik.authTG.api;

import org.ezhik.authTG.BotTelegram;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface TelegramUpdateInterceptor {
    /**
     * Runs before AuthTG routes Telegram commands and callbacks.
     *
     * @return true when the update was handled and AuthTG should stop processing it.
     */
    boolean onTelegramUpdate(Update update, BotTelegram bot) throws Exception;
}
