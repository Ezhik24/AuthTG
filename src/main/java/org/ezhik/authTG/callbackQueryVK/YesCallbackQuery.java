package org.ezhik.authTG.callbackQueryVK;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.TwoFactorAuthService;

import java.util.UUID;

public class YesCallbackQuery implements CallbackQueryVK{
    @Override
    public void execute(int peerid, UUID uuid) {
        User user = User.getUser(uuid);
        if (user == null) {
            return;
        }

        Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
            Player player = Bukkit.getPlayer(user.playername);
            if (player == null) {
                return;
            }

            TwoFactorAuthService.completeLogin(player);
        });
    }
}
