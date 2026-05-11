package org.ezhik.authTG.callbackQueryVK;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.AuthHandler;

import java.util.UUID;

public class NoCallbackQuery implements CallbackQueryVK{
    @Override
    public void execute(int peerid, UUID uuid) {

        User user = User.getUser(uuid);
        if (user == null) {
            return;
        }

        Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
            AuthHandler.removeTimeout(user.uuid);

            Player player = Bukkit.getPlayer(user.playername);
            if (player != null) {
                player.kickPlayer(AuthTG.getMessage("loginnosuccess", "MC"));
            }
        });
    }
}
