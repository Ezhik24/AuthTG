package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public class LoginAcceptedYes implements CallbackQueryHandler{
    @Override
    public void execute(Update update, UUID uuid) {
        User user = User.getUser(uuid);
        FreezerEvent.unfreezeplayer(user.playername);
        MuterEvent.unmute(user.playername);
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
        Player player = Bukkit.getPlayer(user.playername);
        player.resetTitle();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &a&lУспешная авторизация"));
    }
}
