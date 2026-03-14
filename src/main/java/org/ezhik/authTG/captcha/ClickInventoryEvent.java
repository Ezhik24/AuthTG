package org.ezhik.authTG.captcha;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickInventoryEvent implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();

        if (!(event.getInventory().getHolder() instanceof CaptchaHolder)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        Material click = event.getCurrentItem().getType();
        Captcha.checkCaptcha(p, click);
    }
}
