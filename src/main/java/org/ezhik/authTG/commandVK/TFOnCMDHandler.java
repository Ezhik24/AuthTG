package org.ezhik.authTG.commandVK;

import org.ezhik.authTG.*;

public class TFOnCMDHandler implements VKCommandHandler{
    @Override
    public void execute(int peerid, String message) {
        if (AuthTG.authNecessarily || AuthTG.notRegAndLogin) {
            return;
        }
        User user = User.getCurrentUser(peerid);
        if (user != null) {
            AuthTG.loader.setTwofactor(user.uuid, true);
            if (AuthTG.getDataSource() != null) {
                TwoFactorPreferenceRepository.set(user.uuid, TwoFactorMethod.VK);
            }
            AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("tfonsuccess", "VK").replace("{PLAYER}", user.playername));
        } else {
            AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("tfonntactive", "VK"));
        }
    }
}
