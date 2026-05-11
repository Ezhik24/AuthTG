package org.ezhik.authTG.commandVK;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.nextStep.AskPlayernameHandler;
import org.ezhik.authTG.nextStepVK.PlayerNameVK;

public class StartCMDHandler implements VKCommandHandler {
    @Override
    public void execute(int peerid, String message) {
        if (AuthTG.maxAccountVKCount > 0) {
            if (AuthTG.loader.getPlayerNames(peerid) != null && !AuthTG.loader.getPlayerNames(peerid).isEmpty() && AuthTG.loader.getPlayerNames(peerid).size() >= AuthTG.maxAccountTGCount) {
                AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("startmaxacc", "VK"));
                return;
            }
        }
        AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("startlinkacc", "VK"));
        AuthTG.vk.setNextStepHandler(peerid, new PlayerNameVK());
    }
}
