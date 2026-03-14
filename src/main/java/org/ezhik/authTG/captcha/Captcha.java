package org.ezhik.authTG.captcha;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.OnJoinEvent;
import org.ezhik.authTG.handlers.Handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class Captcha {

    private static final List<Material> MATERIALS = List.of(
            Material.RED_WOOL,
            Material.WHITE_WOOL,
            Material.YELLOW_WOOL,
            Material.BLUE_WOOL,
            Material.PURPLE_WOOL,
            Material.GREEN_WOOL,
            Material.BLACK_WOOL
    );

    private static final Map<UUID, Integer> ATTEMPTS = new ConcurrentHashMap<>();
    private static final Map<UUID, Material> CORRECTS = new ConcurrentHashMap<>();
    private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();

    private static final int MAX_ATTEMPTS = 3;

    private Captcha() {
    }

    public static boolean isPending(UUID uuid) {
        return uuid != null && PENDING.contains(uuid);
    }

    public static void clear(UUID uuid) {
        if (uuid == null) {
            return;
        }

        PENDING.remove(uuid);
        ATTEMPTS.remove(uuid);
        CORRECTS.remove(uuid);
    }

    public static void openFor(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        ATTEMPTS.putIfAbsent(uuid, MAX_ATTEMPTS);
        PENDING.add(uuid);

        player.openInventory(buildCaptcha(player));
    }

    public static void checkCaptcha(Player player, Material clickedMaterial) {
        if (player == null || clickedMaterial == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Material correct = CORRECTS.get(uuid);
        if (correct == null) {
            clear(uuid);
            player.closeInventory();
            return;
        }

        if (clickedMaterial == correct) {
            player.closeInventory();
            player.sendMessage("§aКапча успешно пройдена.");

            clear(uuid);

            int timeoutDays = Math.max(0, AuthTG.getInstance().getConfig().getInt("captcha.timeoutCaptcha", 3));
            CaptchaTimeoutStore.setTimeout(uuid, LocalDateTime.now().plusDays(timeoutDays));

            User user = User.getUser(uuid);
            OnJoinEvent.loadRegistration(player, user);
            return;
        }

        int triesLeft = ATTEMPTS.getOrDefault(uuid, MAX_ATTEMPTS) - 1;
        if (triesLeft <= 0) {
            clear(uuid);
            Handler.kick(player.getName(), "§cПопробуйте снова пройти капчу.");
            return;
        }

        ATTEMPTS.put(uuid, triesLeft);
        player.closeInventory();
        player.sendMessage("§eКапча не пройдена. Осталось попыток: " + triesLeft);
        player.openInventory(buildCaptcha(player));
    }

    private static Inventory buildCaptcha(Player player) {
        Material correct = MATERIALS.get(ThreadLocalRandom.current().nextInt(MATERIALS.size()));
        CORRECTS.put(player.getUniqueId(), correct);

        String title = "§aВыберите " + russianName(correct) + " шерсть";
        Inventory inventory = Bukkit.createInventory(new CaptchaHolder(), 45, title);

        for (int i = 0; i < inventory.getSize(); i++) {
            Material randomMaterial = MATERIALS.get(ThreadLocalRandom.current().nextInt(MATERIALS.size()));
            inventory.setItem(i, new ItemStack(randomMaterial));
        }

        return inventory;
    }

    private static String russianName(Material material) {
        return switch (material) {
            case RED_WOOL -> "красную";
            case WHITE_WOOL -> "белую";
            case BLACK_WOOL -> "черную";
            case YELLOW_WOOL -> "желтую";
            case PURPLE_WOOL -> "фиолетовую";
            case BLUE_WOOL -> "синюю";
            case GREEN_WOOL -> "зеленую";
            default -> "";
        };
    }
}
