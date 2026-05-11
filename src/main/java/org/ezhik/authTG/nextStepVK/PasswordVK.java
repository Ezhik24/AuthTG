package org.ezhik.authTG.nextStepVK;

import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

import java.util.UUID;

public class PasswordVK implements NextStepVK {
    UUID uuid;
    public PasswordVK(UUID uuid) {
        this.uuid = uuid;
    }
    @Override
    public void execute(int peerid, String message) {
        if (AuthTG.loader.passwordValid(uuid, message)) {
            User.registerVK(peerid, uuid);
        } else {
            AuthTG.vk.sendMessage(peerid, AuthTG.getMessage("activevkwrongpass", "VK"));
        }
        AuthTG.vk.remNextStepHandler(peerid);
    }
}
