package org.ezhik.authTG.nextStep;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendMessageTGHandler implements NextStepHandler {
    @Override
    public void execute(Update update) {
        AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        User senderuser = User.getCurrentUser(update.getMessage().getChatId());
        User frienduser = User.getUser(AuthTG.bot.getUserData(update.getMessage().getChatId().toString()));
        frienduser.sendMessageFriend(AuthTG.getMessage("sendmessagetg", "TG").replace("{PLAYER}", senderuser.playername) + update.getMessage().getText().toString(), senderuser.uuid);
        senderuser.sendMessage(AuthTG.getMessage("sendmessagetgsuccess", "TG"));
        AuthTG.bot.remUserData(update.getMessage().getChatId().toString());
    }
}
