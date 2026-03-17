package org.ezhik.authTG.commandTG;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandMC.CodeCMD;
import org.ezhik.authTG.util.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnLinkCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        if (AuthTG.notRegAndLogin) {
            AuthTG.bot.deleteMessage(update.getMessage());
            return;
        }

        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (user == null) {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("unlinknotactive", "TG"));
            return;
        }

        String code = User.generateConfirmationCode();

        if (user.player != null) {
            MessageHelper.send(user.player, AuthTG.getMessage("codemsgdeactivated", "MC"));
            CodeCMD.code.put(user.uuid, code);
            user.sendMessage(AuthTG.getMessage("unlinkcode", "TG").replace("{CODE}", code));
        } else {
            user.sendMessage(AuthTG.getMessage("unlinkplntonline", "TG"));
        }
    }
}