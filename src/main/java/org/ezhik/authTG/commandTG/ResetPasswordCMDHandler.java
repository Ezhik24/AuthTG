package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ResetPasswordCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user != null) {
            String password = User.generateConfirmationCode();
            AuthTG.loader.setPasswordHash(user.uuid, password);
            user.sendMessage(AuthTG.config.getString("messages.telegram.resetpasssuccess").replace("{PASSWORD}", password));
        } else {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(),AuthTG.config.getString("messages.telegram.resetpassnotactive"));
        }
    }
}
