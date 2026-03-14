package org.ezhik.authTG.captcha;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.events.OnJoinEvent;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

public class Captcha {
    private static final Random r = new Random();
    public static Map<UUID,Integer> attempts = new HashMap<>();
    public static Map<UUID, Material> corrects = new HashMap<>();
    public static List<UUID> list  =new ArrayList<>();

    public static Inventory loadCaptcha(Player player) {
        Material[] materials = {
                Material.RED_WOOL,
                Material.WHITE_WOOL,
                Material.YELLOW_WOOL,
                Material.BLUE_WOOL,
                Material.PURPLE_WOOL,
                Material.GREEN_WOOL,
                Material.BLACK_WOOL
        };
        Material correct = materials[r.nextInt(materials.length)];
        corrects.put(player.getUniqueId(), correct);
        if (!attempts.containsKey(player.getUniqueId())) attempts.put(player.getUniqueId(), 3);
        String name;
        switch (correct) {
            case RED_WOOL -> name = "красную";
            case WHITE_WOOL -> name = "белую";
            case BLACK_WOOL -> name = "черную";
            case YELLOW_WOOL -> name = "желтую";
            case PURPLE_WOOL -> name = "фиолетовую";
            case BLUE_WOOL -> name = "синию";
            case GREEN_WOOL -> name = "зеленую";
            default -> name = " ";
        }
        String title = "§aВыберите " + name + " шерсть";
        Inventory inv = Bukkit.createInventory(new CaptchaHolder(), 45, title);
        for (int i = 0; i < inv.getSize(); i ++) {
            inv.setItem(i, new ItemStack(materials[r.nextInt(materials.length)]));
        }
        return inv;
    }

    public static void checkCaptcha(Player p, Material material) {
        Material correct = corrects.get(p.getUniqueId());

        if (material == correct) {
            p.closeInventory();
            p.sendMessage("§aКаптча успешно пройдена!");

            corrects.remove(p.getUniqueId());
            attempts.remove(p.getUniqueId());
            User user = User.getUser(p.getUniqueId());
            AuthTG.loader.setCaptchaTimeout(p.getUniqueId(), LocalDateTime.now().plusDays(AuthTG.getInstance().getConfig().getInt("captcha.timeoutCaptcha")));
            OnJoinEvent.loadRegistration(p,user);
        } else {
            int tries = attempts.get(p.getUniqueId()) - 1;

            if (tries <= 0) {
                Handler.kick(p.getName(), "§aПопробуйте еще раз пройти каптчу!");
            } else {
                p.closeInventory();
                attempts.put(p.getUniqueId(), tries);
                p.sendMessage("§aОсталось попыток: " + tries);
                p.openInventory(loadCaptcha(p)) ;
            }
        }
    }


}
