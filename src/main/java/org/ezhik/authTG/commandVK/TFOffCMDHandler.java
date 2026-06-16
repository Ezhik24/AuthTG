package org.ezhik.authTG.commandVK;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.TwoFactorMethod;
import org.ezhik.authTG.TwoFactorPreferenceRepository;
import org.ezhik.authTG.User;

public class TFOffCMDHandler implements VKCommandHandler{
    @Override
    public void execute(int peerid, String message) {
        if (AuthTG.authNecessarily || AuthTG.notRegAndLogin) {
            return;
        }
        User user = User.getCurrentUser(peerid);
        if (user != null) {
            AuthTG.loader.setTwofactor(user.uuid, false);
            if (AuthTG.getDataSource() != null) {
                TwoFactorPreferenceRepository.set(user.uuid, TwoFactorMethod.OFF);
            }
            AuthTG.vk.sendMessage(peerid,AuthTG.getMessage("tfoffsuccess", "VK").replace("{PLAYER}", user.playername));
        } else {
            AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("tfoffntactive", "VK"));
        }
    }
}
