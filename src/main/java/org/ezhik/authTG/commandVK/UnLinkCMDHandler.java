package org.ezhik.authTG.commandVK;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.commandMC.CodeCMD;
import org.ezhik.authTG.commandMC.VKCMD;
import org.ezhik.authTG.util.MessageHelper;

public class UnLinkCMDHandler implements VKCommandHandler{
    @Override
    public void execute(int peerid, String message) {
        if (AuthTG.notRegAndLogin) {
            return;
        }

        User user = User.getCurrentUser(peerid);
        if (user == null) {
            AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("unlinknotactive", "VK"));
            return;
        }

        String code = User.generateConfirmationCode();

        if (user.player != null) {
            MessageHelper.send(user.player, AuthTG.getMessage("msgdeactivatedvk", "MC"));
            VKCMD.code.put(user.uuid, code);
            user.sendMessage(AuthTG.getMessage("unlinkcode", "VK").replace("{CODE}", code));
        } else {
            user.sendMessage(AuthTG.getMessage("unlinkplntonline", "VK"));
        }
    }
}
