package org.ezhik.authTG.commandVK;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.Handler;

public class KickMeCMDHandler implements VKCommandHandler{
    @Override
    public void execute(int peerid, String message) {
        User user = User.getCurrentUser(peerid);
        if (user != null) {
            if (user.player != null) {
                Handler.kick(user.playername, AuthTG.getMessage("kickmeplayer", "VK").replace("{PLAYER}", user.playername));
                user.sendMessage(AuthTG.getMessage("kickmesuccess", "VK").replace("{PLAYER}", user.playername));
            } else {
                user.sendMessage(AuthTG.getMessage("kickmeplnotonline", "VK").replace("{PLAYER}", user.playername));
            }
        } else {
            AuthTG.vk.sendMessage(peerid,AuthTG.getMessage("kickmenotactive", "VK"));
        }
    }
}
