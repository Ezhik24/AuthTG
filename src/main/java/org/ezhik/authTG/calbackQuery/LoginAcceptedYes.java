package org.ezhik.authTG.calbackQuery;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginAcceptedYes implements CallbackQueryHandler{
    @Override
    public void execute(Update update) {
        String[] str = update.getCallbackQuery().getData().toString().split("_");
        User user = User.getUser(UUID.fromString(str[1]));
        Player player = Bukkit.getPlayer(user.playername);
        FreezerEvent.unfreezeplayer(player.getName());
        if (FreezerEvent.beforeFreeze.containsKey(player.getName())) {
            Handler.teleport(player.getName(), FreezerEvent.beforeFreeze.get(player.getName()));
            FreezerEvent.beforeFreeze.remove(player.getName());
        }
        player.resetTitle();
        for (String friend : user.friends) {
            User friendUser = User.getUser(friend);
            if (friendUser.activetg) {
                friendUser.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("friendjoin", "TG").replace("{PLAYER}", user.playername)));
            } else {
                AuthTG.loader.removeFriend(friendUser.uuid, user.playername);
                AuthTG.loader.removeFriend(user.uuid, friendUser.playername);
            }
        }
        FreezerEvent.beforeFreeze.remove(player.getName());
        MuterEvent.unmute(user.playername);
        AuthTG.bot.deleteMessage(update.getCallbackQuery().getMessage());
        if (AuthTG.kickTimeout != 0) {
            AuthHandler.removeTimeout(player.getUniqueId());
        }
        LocalDateTime time = LocalDateTime.now().plusMinutes(AuthTG.timeoutSession);
        AuthTG.sessionManager.addAuthorized(player.getUniqueId(), player.getAddress().getAddress().toString(),time);
        player.resetTitle();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("loginsuccess", "MC")));
    }
}
