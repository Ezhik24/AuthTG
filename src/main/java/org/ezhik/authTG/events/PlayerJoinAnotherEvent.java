package org.ezhik.authTG.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.security.NicknameCaseGuard;
import org.ezhik.authTG.util.MessageHelper;

<<<<<<< HEAD
=======
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
>>>>>>> 1d55b6e (3.2)
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerJoinAnotherEvent implements Listener {

    private final NicknameCaseGuard nicknameCaseGuard = new NicknameCaseGuard();
    private final Set<String> onlineNamesLowerCase = ConcurrentHashMap.newKeySet();

    public PlayerJoinAnotherEvent() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlineNamesLowerCase.add(NicknameCaseGuard.normalize(player.getName()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
<<<<<<< HEAD
=======
        if (denyBannedPlayer(event)) return;

>>>>>>> 1d55b6e (3.2)
        String requestedName = event.getName();
        String normalizedName = NicknameCaseGuard.normalize(requestedName);

        if (isOnlineSameNameCheckEnabled() && onlineNamesLowerCase.contains(normalizedName)) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    getOnlineDuplicateMessage()
            );
            return;
        }

        if (!isNicknameCaseCheckEnabled()) {
            return;
        }

        NicknameCaseGuard.CheckResult result = nicknameCaseGuard.check(requestedName, event.getUniqueId());

        if (result.status() == NicknameCaseGuard.Status.CASE_MISMATCH) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    getCaseMismatchMessage(requestedName, result.registeredName())
            );
            return;
        }

        if (result.status() == NicknameCaseGuard.Status.STORAGE_ERROR) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    getStorageErrorMessage()
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        onlineNamesLowerCase.add(NicknameCaseGuard.normalize(event.getPlayer().getName()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onlineNamesLowerCase.remove(NicknameCaseGuard.normalize(event.getPlayer().getName()));
    }

<<<<<<< HEAD
=======
    private boolean denyBannedPlayer(AsyncPlayerPreLoginEvent event) {
        String banTime = AuthTG.loader.getBanTime(event.getUniqueId());
        if (banTime == null) return false;

        if (!"0".equals(banTime)) {
            try {
                LocalDateTime expires = LocalDateTime.parse(banTime, DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
                if (LocalDateTime.now().isAfter(expires)) {
                    AuthTG.loader.deleteBan(event.getUniqueId());
                    return false;
                }
            } catch (RuntimeException exception) {
                AuthTG.logger.warning("Invalid ban expiration for " + event.getUniqueId() + ": " + banTime);
            }
        }

        String message = MessageHelper.legacySection(AuthTG.getMessage("ban", "MC"))
                .replace("{REASON}", String.valueOf(AuthTG.loader.getBanReason(event.getUniqueId())))
                .replace("{TIMEBAN}", "0".equals(banTime) ? "навсегда" : banTime)
                .replace("{TIME}", String.valueOf(AuthTG.loader.getBanTimeAdmin(event.getUniqueId())))
                .replace("{ADMIN}", String.valueOf(AuthTG.loader.getBanAdmin(event.getUniqueId())));
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
        return true;
    }

>>>>>>> 1d55b6e (3.2)
    private boolean isNicknameCaseCheckEnabled() {
        return AuthTG.getInstance().getConfig().getBoolean("nicknameCase.enabled", true);
    }

    private boolean isOnlineSameNameCheckEnabled() {
        return AuthTG.getInstance().getConfig().getBoolean("nicknameCase.blockOnlineSameNameIgnoreCase", true);
    }

    private String getOnlineDuplicateMessage() {
        String configured = AuthTG.getMessage("joinanother", "MC");
        if (configured == null || configured.isBlank()) {
            configured = "<#F4FBFF><bold>[<#FFB26F><bold>AuthTG<#F4FBFF><bold>] " +
                    "<#FFB26F><bold>Игрок с таким ником уже находится на сервере.";
        }
        return MessageHelper.legacySection(configured);
    }

    private String getCaseMismatchMessage(String requestedName, String correctName) {
        String configured = AuthTG.getMessage("nicknamecaseinvalid", "MC");
        if (configured == null || configured.isBlank()) {
            configured = "<#F4FBFF><bold>[<#FFB26F><bold>AuthTG<#F4FBFF><bold>] " +
                    "<#FFB26F><bold>Ник введён в неверном регистре. " +
                    "<#65E0E6><bold>Правильно: {CORRECT_PLAYER}. " +
                    "<#FFB26F><bold>Вы ввели: {PLAYER}.";
        }

        return MessageHelper.legacySection(configured
                .replace("{PLAYER}", requestedName)
                .replace("{CORRECT_PLAYER}", correctName == null ? "" : correctName));
    }

    private String getStorageErrorMessage() {
        String configured = AuthTG.getMessage("nicknamecasecheckerror", "MC");
        if (configured == null || configured.isBlank()) {
            configured = "<#F4FBFF><bold>[<#FFB26F><bold>AuthTG<#F4FBFF><bold>] " +
                    "<#FFB26F><bold>Не удалось проверить регистр ника. Попробуйте позже.";
        }
        return MessageHelper.legacySection(configured);
    }
}