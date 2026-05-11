package org.ezhik.authTG.nextStepVK;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

public class PlayerNameVK implements NextStepVK {
    @Override
    public void execute(int peerid, String message) {
        Player player = Bukkit.getPlayer(message);
        if (player != null) {
            User user = User.getUser(player.getUniqueId());
            if (user != null && user.activetg) {
                if (user.peerid == peerid) {
                    AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("activevkalready", "VK"));
                } else{
                    AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("activevkalreadyanother", "VK"));
                }
            } else {
                if (AuthTG.notRegAndLogin) {
                    User.registerVK(peerid, player.getUniqueId());
                } else {
                    AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("activevkpasswd", "VK"));
                    AuthTG.vk.setNextStepHandler(peerid, new PasswordVK(player.getUniqueId()));
                }
            }
        } else {
            AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("playernotonlineactive", "VK"));
        }
    }
}
