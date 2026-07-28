package org.ezhik.authTG.callbackQueryTG;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackQueryHandler {
    void execute(Update update);
}
