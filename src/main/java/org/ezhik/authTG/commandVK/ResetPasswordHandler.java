package org.ezhik.authTG.commandVK;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

public class ResetPasswordHandler implements VKCommandHandler{
    @Override
    public void execute(int peerid, String message) {
        User user = User.getCurrentUser(peerid);
        if (user != null) {
            String password = User.generateConfirmationCode();
            AuthTG.loader.setPasswordHash(user.uuid, password);
            AuthTG.vk.sendMessage(peerid ,AuthTG.getMessage("resetpasssuccess", "VK").replace("{PASSWORD}", password));
        } else {
            AuthTG.vk.sendMessage(peerid,AuthTG.getMessage("resetpassnotactive", "VK"));
        }
    }
}
