package org.ezhik.authTG.nextStep;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.Handler;
import org.ezhik.authTG.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public class KickAskHandler implements NextStepHandler {
    @Override
    public void execute(Update update) {
        String playername = update.getMessage().getText().toString().split(" ")[0];
        Player player = Bukkit.getPlayer(playername);
        User user = User.getCurrentUser(update.getMessage().getChatId());
        if (player != null) {
            Handler.kick(player.getName(), update.getMessage().getText().toString().substring(playername.length() + 1));
            user.sendMessage("Игрок " + playername + " успешно кикнут!");
            AuthTG.bot.remNextStepHandler(update.getMessage().getChatId());
        } else {
            user.sendMessage("Игрок не онлайн!");
        }
    }
}
