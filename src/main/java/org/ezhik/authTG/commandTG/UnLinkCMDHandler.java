package org.ezhik.authTG.commandTG;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandMC.CodeCMD;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnLinkCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        if (AuthTG.config.getBoolean("authNecessarily") || AuthTG.config.getBoolean("notRegAndLogin")) {
            AuthTG.bot.deleteMessage(update.getMessage());
        } else {
            User user = User.getCurrentUser(update.getMessage().getChatId());
            if (user != null) {
                String code = User.generateConfirmationCode();
                if (user.player != null) {
                    user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.config.getString("messages.minecraft.codemsgdeactivated")));
                    CodeCMD.code.put(user.uuid, code);
                    user.sendMessage(AuthTG.config.getString("messages.telegram.unlinkcode").replace("{CODE}", code));
                } else {
                    user.sendMessage(AuthTG.config.getString("messages.telegram.unlinkplntonline"));
                }
            } else {
                AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.config.getString("messages.telegram.unlinknotactive"));
            }
        }
    }
}
