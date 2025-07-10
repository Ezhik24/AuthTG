package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    void execute(Update update);
}
