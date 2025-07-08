package org.ezhik.authTG.nextStep;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AskPlayernameHandler implements NextStepHandler {
    @Override
    public void execute(Update update) {
        Long key = update.getMessage().getChatId();
        Player player = Bukkit.getPlayer(update.getMessage().getText().toString());
        if (player != null) {
            User user = User.getUser(player.getUniqueId());
            if (user != null && user.activetg) {
                if (user.chatid.equals(update.getMessage().getChatId())) {
                    AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.activetgalready"));
                } else{
                    AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.activetgalreadyanother"));
                }
            } else {
                if (AuthTG.config.getBoolean("notRegAndLogin")) {
                     User.register(update.getMessage(), player.getUniqueId());
                } else {
                    AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.activetgpasswd"));
                    AuthTG.bot.setNextStepHandler(key, new AskPasswordHandler(player.getUniqueId()));
                }
            }
        } else {
            AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.playernotonlineactive"));
        }
    }
}
