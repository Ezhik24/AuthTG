package org.ezhik.authTG.calbackQuery;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackQueryHandler {
    void execute(Update update);
}
