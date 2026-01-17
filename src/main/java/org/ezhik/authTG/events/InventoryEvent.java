package org.ezhik.authTG.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryEvent implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        System.out.println("test1");
        System.out.println(event.getPlayer().getName());
        if (FreezerEvent.isFreeze(event.getPlayer().getName())) {
            event.getView().close();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (FreezerEvent.isFreeze(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }
}
