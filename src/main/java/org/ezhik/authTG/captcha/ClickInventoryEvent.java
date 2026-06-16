package org.ezhik.authTG.captcha;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClickInventoryEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!Captcha.isCaptchaInventory(event.getView())) {
            return;
        }

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        if (!clickedInventory.equals(event.getView().getTopInventory())) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType().isAir()) {
            return;
        }

        Material clicked = currentItem.getType();
        Captcha.checkCaptcha(player, clicked);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (Captcha.isCaptchaInventory(event.getView())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!Captcha.isCaptchaInventory(event.getView())) {
            return;
        }

        Captcha.reopenIfClosedWithoutClick(player);
    }
}