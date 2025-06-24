package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface NextStepHandler {
    void execute(Update update);
}
