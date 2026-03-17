package org.ezhik.authTG.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ezhik.authTG.captcha.Captcha;

public class InventoryEvent implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!FreezerEvent.isFreeze(event.getPlayer().getName())) {
            return;
        }

        if (Captcha.isOpening(event.getPlayer().getUniqueId())) {
            return;
        }

        if (Captcha.isCaptchaInventory(event.getView())) {
            return;
        }

        event.setCancelled(true);
        event.getView().close();
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!FreezerEvent.isFreeze(event.getWhoClicked().getName())) {
            return;
        }

        if (Captcha.isCaptchaInventory(event.getView())) {
            return;
        }

        event.setCancelled(true);
    }
}