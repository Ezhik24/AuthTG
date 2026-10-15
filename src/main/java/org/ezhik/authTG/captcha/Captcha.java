package org.ezhik.authTG.captcha;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
<<<<<<< HEAD
=======
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
>>>>>>> 1d55b6e (3.2)
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.OnJoinEvent;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.util.MessageHelper;

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
<<<<<<< HEAD
=======
    private static final Map<UUID, String> TEXT_CODES = new ConcurrentHashMap<>();
    private static final Map<UUID, ItemStack> PREVIOUS_ITEMS = new ConcurrentHashMap<>();
>>>>>>> 1d55b6e (3.2)
    private static final Map<UUID, Material> CORRECTS = new ConcurrentHashMap<>();
    private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> OPENING = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> CLOSING_AFTER_CLICK = ConcurrentHashMap.newKeySet();

    private static final int MAX_ATTEMPTS = 3;

    private Captcha() {
    }

    public static boolean isPending(UUID uuid) {
        return uuid != null && PENDING.contains(uuid);
    }

    public static boolean isOpening(UUID uuid) {
        return uuid != null && OPENING.contains(uuid);
    }

    public static boolean isCaptchaInventory(InventoryView view) {
        if (view == null || view.getTopInventory() == null) {
            return false;
        }

        if (view.getTopInventory().getHolder() instanceof CaptchaHolder) {
            return true;
        }

        String title = view.getTitle();
        if (title == null) {
            return false;
        }

        for (Material material : MATERIALS) {
            if (title.equals(buildInventoryTitle(material))) {
                return true;
            }
        }

        return false;
    }

    public static void beginChallenge(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        PENDING.add(uuid);
        ATTEMPTS.putIfAbsent(uuid, MAX_ATTEMPTS);
    }

<<<<<<< HEAD
    public static void openFor(Player player) {
=======
    public static boolean isTextMode() {
        return "TEXT_MAP".equalsIgnoreCase(AuthTG.getInstance().getConfig().getString("captcha.type", "TEXT_MAP"));
    }

    public static void openFor(Player player) {
        if (isTextMode()) { openTextMap(player); return; }
>>>>>>> 1d55b6e (3.2)
        if (player == null || !player.isOnline()) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (!PENDING.contains(uuid)) {
            beginChallenge(player);
        }

        if (isCaptchaInventory(player.getOpenInventory())) {
            return;
        }

        if (!OPENING.add(uuid)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(AuthTG.getInstance(), () -> {
            Player online = Bukkit.getPlayer(uuid);
            if (online == null || !online.isOnline() || !PENDING.contains(uuid)) {
                OPENING.remove(uuid);
                return;
            }

            online.openInventory(buildCaptcha(online));
            Bukkit.getScheduler().runTaskLater(AuthTG.getInstance(), () -> OPENING.remove(uuid), 5L);
        }, 1L);
    }

    public static void reopenIfClosedWithoutClick(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (!PENDING.contains(uuid)) {
            return;
        }

        if (CLOSING_AFTER_CLICK.contains(uuid)) {
            return;
        }

        long delay = OPENING.contains(uuid) ? 6L : 1L;

        Bukkit.getScheduler().runTaskLater(AuthTG.getInstance(), () -> {
            Player online = Bukkit.getPlayer(uuid);
            if (online == null || !online.isOnline() || !PENDING.contains(uuid)) {
                return;
            }

            if (CLOSING_AFTER_CLICK.contains(uuid) || isCaptchaInventory(online.getOpenInventory())) {
                return;
            }

            openFor(online);
        }, delay);
    }

    public static void clear(UUID uuid) {
        if (uuid == null) {
            return;
        }

        PENDING.remove(uuid);
        OPENING.remove(uuid);
        CLOSING_AFTER_CLICK.remove(uuid);
        ATTEMPTS.remove(uuid);
        CORRECTS.remove(uuid);
<<<<<<< HEAD
=======
        TEXT_CODES.remove(uuid);
        Player online = Bukkit.getPlayer(uuid);
        ItemStack previous = PREVIOUS_ITEMS.remove(uuid);
        if (online != null && previous != null) online.getInventory().setItemInMainHand(previous);
    }

    public static void checkText(Player player, String answer) {
        if (player == null || !isPending(player.getUniqueId())) return;
        String expected = TEXT_CODES.get(player.getUniqueId());
        if (expected != null && expected.equalsIgnoreCase(answer == null ? "" : answer.trim())) { complete(player); return; }
        fail(player);
    }

    private static void openTextMap(Player player) {
        if (player == null || !player.isOnline()) return;
        UUID uuid = player.getUniqueId();
        if (!PENDING.contains(uuid)) beginChallenge(player);
        String code = randomCode();
        TEXT_CODES.put(uuid, code);
        PREVIOUS_ITEMS.putIfAbsent(uuid, player.getInventory().getItemInMainHand().clone());
        MapView view = Bukkit.createMap(player.getWorld());
        for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);
        view.addRenderer(new TextCaptchaRenderer(code));
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) map.getItemMeta();
        meta.setMapView(view);
        map.setItemMeta(meta);
        player.getInventory().setItemInMainHand(map);
        MessageHelper.send(player, "<yellow>Введите код с карты: <white>/captcha <код>");
    }

    private static String randomCode() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) code.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        return code.toString();
>>>>>>> 1d55b6e (3.2)
    }

    public static void checkCaptcha(Player player, Material clickedMaterial) {
        if (player == null || clickedMaterial == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Material correct = CORRECTS.get(uuid);

        if (correct == null) {
            closeAfterClick(player);
            return;
        }

        if (clickedMaterial == correct) {
            closeAfterClick(player);
            MessageHelper.send(player, mc("captchasuccess"));

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
            Handler.kick(player.getName(), MessageHelper.legacySection(mc("captchakick")));
            return;
        }

        ATTEMPTS.put(uuid, triesLeft);
        closeAfterClick(player);

        MessageHelper.send(
                player,
                mc("captchafail").replace("{ATTEMPTS}", String.valueOf(triesLeft))
        );
        MessageHelper.send(player, mc("captchareopen"));

        Bukkit.getScheduler().runTaskLater(AuthTG.getInstance(), () -> {
            Player online = Bukkit.getPlayer(uuid);
            if (online == null || !online.isOnline() || !isPending(uuid)) {
                return;
            }

            MessageHelper.showTitle(
                    online,
                    mc("captchatitlemain"),
                    mc("captchatitlesub")
            );
        }, 2L);
    }

<<<<<<< HEAD
=======
    private static void complete(Player player) {
        UUID uuid = player.getUniqueId();
        MessageHelper.send(player, mc("captchasuccess"));
        clear(uuid);
        int timeoutDays = Math.max(0, AuthTG.getInstance().getConfig().getInt("captcha.timeoutCaptcha", 3));
        CaptchaTimeoutStore.setTimeout(uuid, LocalDateTime.now().plusDays(timeoutDays));
        OnJoinEvent.loadRegistration(player, User.getUser(uuid));
    }

    private static void fail(Player player) {
        UUID uuid = player.getUniqueId();
        int triesLeft = ATTEMPTS.getOrDefault(uuid, MAX_ATTEMPTS) - 1;
        if (triesLeft <= 0) {
            clear(uuid);
            Handler.kick(player.getName(), MessageHelper.legacySection(mc("captchakick")));
            return;
        }
        ATTEMPTS.put(uuid, triesLeft);
        MessageHelper.send(player, mc("captchafail").replace("{ATTEMPTS}", String.valueOf(triesLeft)));
        openTextMap(player);
    }

>>>>>>> 1d55b6e (3.2)
    private static void closeAfterClick(Player player) {
        UUID uuid = player.getUniqueId();
        CLOSING_AFTER_CLICK.add(uuid);
        player.closeInventory();

        Bukkit.getScheduler().runTaskLater(AuthTG.getInstance(), () -> CLOSING_AFTER_CLICK.remove(uuid), 3L);
    }

    private static Inventory buildCaptcha(Player player) {
        Material correct = MATERIALS.get(ThreadLocalRandom.current().nextInt(MATERIALS.size()));
        CORRECTS.put(player.getUniqueId(), correct);

        CaptchaHolder holder = new CaptchaHolder();
        Inventory inventory = Bukkit.createInventory(
                holder,
                45,
                buildInventoryTitle(correct)
        );
        holder.setInventory(inventory);

        for (int i = 0; i < inventory.getSize(); i++) {
            Material randomMaterial = MATERIALS.get(ThreadLocalRandom.current().nextInt(MATERIALS.size()));
            inventory.setItem(i, new ItemStack(randomMaterial));
        }

        return inventory;
    }

    private static String buildInventoryTitle(Material material) {
        return MessageHelper.legacySection(
                mc("captchainventory")
                        .replace("{WOOL}", woolName(material))
        );
    }

    private static String woolName(Material material) {
        return switch (material) {
            case RED_WOOL -> mc("captchawoolred");
            case WHITE_WOOL -> mc("captchawoolwhite");
            case BLACK_WOOL -> mc("captchawoolblack");
            case YELLOW_WOOL -> mc("captchawoolyellow");
            case PURPLE_WOOL -> mc("captchawoolpurple");
            case BLUE_WOOL -> mc("captchawoolblue");
            case GREEN_WOOL -> mc("captchawoolgreen");
            default -> mc("captchawoolunknown");
        };
    }

    private static String mc(String key) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null ? key : value;
    }
}