package org.ezhik.authTG.commandTG;

import org.bukkit.ChatColor;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandMC.CodeCMD;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnLinkCMDHandler implements CommandHandler {
    @Override
    public void execute(Update update) {
        if (AuthTG.globalConfig.authNecessarily || AuthTG.globalConfig.notRegAndLogin) {
            AuthTG.bot.deleteMessage(update.getMessage());
        } else {
            User user = User.getCurrentUser(update.getMessage().getChatId());
            if (user != null) {
                String code = User.generateConfirmationCode();
                user.sendMessage("[Бот] Что бы отвязать аккаунт, введите /code " + code);
                user.player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lЧто бы отвязать аккаунт, введите /code <код из Телеграма>, если это не вы,то проигнорируйте это сообщение."));
                CodeCMD.code.put(user.uuid, code);
            } else {
                AuthTG.bot.sendMessage(update.getMessage().getChatId(), "[Бот] Вы не привязали аккаунт!");
            }
        }
    }
}
