package org.ezhik.authTG.captcha;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickInventoryEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!(event.getView().getTopInventory().getHolder() instanceof CaptchaHolder)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null) {
            return;
        }

        if (!(event.getClickedInventory().getHolder() instanceof CaptchaHolder)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        Material clicked = event.getCurrentItem().getType();
        Captcha.checkCaptcha(player, clicked);
    }
}
