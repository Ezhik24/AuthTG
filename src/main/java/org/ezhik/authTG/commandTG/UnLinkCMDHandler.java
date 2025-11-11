package org.ezhik.authTG.commandTG;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandMC.CodeCMD;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnLinkCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        if (AuthTG.notRegAndLogin) {
            AuthTG.bot.deleteMessage(update.getMessage());
        } else {
            User user = User.getCurrentUser(update.getMessage().getChatId());
            if (user != null) {
                String code = User.generateConfirmationCode();
                if (user.player != null) {
                    user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("codemsgdeactivated", "MC")));
                    CodeCMD.code.put(user.uuid, code);
                    user.sendMessage(AuthTG.getMessage("unlinkcode", "TG").replace("{CODE}", code));
                } else {
                    user.sendMessage(AuthTG.getMessage("unlinkplntonline", "TG"));
                }
            } else {
                AuthTG.bot.sendMessage(update.getMessage().getChatId(), AuthTG.getMessage("unlinknotactive", "TG"));
            }
        }
    }
}
