package org.ezhik.authTG.callbackQueryVK;

import java.util.UUID;

public interface CallbackQueryVK {
    void execute(int peerid, UUID uuid);
}
