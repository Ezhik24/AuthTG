package org.ezhik.authTG.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ezhik.authTG.captcha.CaptchaHolder;

public class InventoryEvent implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!FreezerEvent.isFreeze(event.getPlayer().getName())) {
            return;
        }

        if (event.getView().getTopInventory().getHolder() instanceof CaptchaHolder) {
            return;
        }

        event.getView().close();
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!FreezerEvent.isFreeze(event.getWhoClicked().getName())) {
            return;
        }

        if (event.getView().getTopInventory().getHolder() instanceof CaptchaHolder) {
            return;
        }

        event.setCancelled(true);
    }
}
