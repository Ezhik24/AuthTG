package org.ezhik.authTG.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.IPManager;
import org.ezhik.authTG.User;
import org.ezhik.authTG.captcha.Captcha;
import org.ezhik.authTG.captcha.CaptchaTimeoutStore;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OnJoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player.getUniqueId());
        LocalDateTime now = LocalDateTime.now();

        if (AuthTG.loader.getBanTime(player.getUniqueId()) != null) {
            if (AuthTG.loader.getBanTime(player.getUniqueId()).equals("0")) {
                event.setJoinMessage(null);
                Handler.kick(
                        player.getName(),
                        ChatColor.translateAlternateColorCodes('&',
                                        AuthTG.getMessage("ban", "MC"))
                                .replace("{REASON}", AuthTG.loader.getBanReason(player.getUniqueId()))
                                .replace("{TIMEBAN}", "навсегда")
                                .replace("{TIME}", AuthTG.loader.getBanTimeAdmin(player.getUniqueId()))
                                .replace("{ADMIN}", AuthTG.loader.getBanAdmin(player.getUniqueId()))
                );
                return;
            }

            LocalDateTime bannedUntil = LocalDateTime.parse(
                    AuthTG.loader.getBanTime(player.getUniqueId()),
                    DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            );

            if (now.isAfter(bannedUntil)) {
                AuthTG.loader.deleteBan(player.getUniqueId());
            } else {
                event.setJoinMessage(null);
                Handler.kick(
                        player.getName(),
                        ChatColor.translateAlternateColorCodes('&',
                                        AuthTG.getMessage("ban", "MC"))
                                .replace("{REASON}", AuthTG.loader.getBanReason(player.getUniqueId()))
                                .replace("{TIMEBAN}", AuthTG.loader.getBanTime(player.getUniqueId()))
                                .replace("{TIME}", AuthTG.loader.getBanTimeAdmin(player.getUniqueId()))
                                .replace("{ADMIN}", AuthTG.loader.getBanAdmin(player.getUniqueId()))
                );
                return;
            }
        }

        if (IPManager.isAuthorized(player)) {
            if (AuthTG.isTelegramEnabled() && user != null && user.activetg) {
                user.sendMessage(
                        AuthTG.getMessage("joinacc", "TG")
                                .replace("{IP}", player.getAddress().getAddress().toString())
                                .replace("/", "")
                );
            }
            return;
        }

        if ("none".equals(AuthTG.world)) {
            FreezerEvent.freezeplayer(player, player.getLocation());
        } else {
            Location playerLocation = player.getLocation();
            Location target = new Location(
                    Bukkit.getWorld(AuthTG.world),
                    AuthTG.locationX,
                    AuthTG.locationY,
                    AuthTG.locationZ
            );
            FreezerEvent.beforeFreeze.put(player.getName(), playerLocation);
            Handler.teleport(player.getName(), target);
            FreezerEvent.freezeplayer(player, target);
        }

        if (AuthTG.forbiddenNicknames.contains(player.getName())) {
            Handler.kick(player.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("forbiddennickname", "MC")));
            return;
        }

        if (player.getName().length() < AuthTG.minLenghtNickname || player.getName().length() > AuthTG.maxLenghtNickname) {
            Handler.kick(
                    player.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("nicknamelenght", "MC")
                                    .replace("{MIN}", String.valueOf(AuthTG.minLenghtNickname))
                                    .replace("{MAX}", String.valueOf(AuthTG.maxLenghtNickname)))
            );
            return;
        }

        if (AuthTG.getInstance().getConfig().getBoolean("captcha.enabled", false)
                && CaptchaTimeoutStore.shouldShowCaptcha(player.getUniqueId())) {
            Captcha.openFor(player);
            return;
        }

        loadRegistration(player, user);
    }

    public static void loadRegistration(Player player, User user) {
        if (AuthTG.kickTimeout != 0) {
            AuthHandler.setTimeout(player.getUniqueId(), AuthTG.kickTimeout);
        }

        if (AuthTG.notRegAndLogin && !AuthTG.authNecessarily) {
            FreezerEvent.unfreezeplayer(player.getName());
            if (AuthTG.kickTimeout != 0) {
                AuthHandler.removeTimeout(player.getUniqueId());
            }
            return;
        }

        if (AuthTG.notRegAndLogin && AuthTG.authNecessarily) {
            if (AuthTG.isTelegramEnabled()) {
                if (user != null && user.activetg) {
                    MuterEvent.mute(player.getName(),
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("joininaccounttext", "MC")));
                    player.sendTitle(
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("joininaccounts1", "MC")),
                            AuthTG.getMessage("joininaccounts2", "MC"),
                            20,
                            10000000,
                            0
                    );

                    user.sendLoginAcceptedAsync(
                            AuthTG.getMessage("loginaccept", "TG")
                                    .replace("{PLAYER}", user.playername)
                                    .replace("{IP}", player.getAddress().getAddress().getHostAddress())
                    );
                } else {
                    MuterEvent.mute(player.getName(),
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("authtgactivetext", "MC")));
                    player.sendTitle(
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("authtgactives1", "MC")),
                            AuthTG.getMessage("authtgactives2", "MC"),
                            20,
                            10000000,
                            0
                    );
                }
            } else {
                FreezerEvent.unfreezeplayer(player.getName());
                MuterEvent.unmute(player.getName());
                player.resetTitle();
                if (AuthTG.kickTimeout != 0) {
                    AuthHandler.removeTimeout(player.getUniqueId());
                }
            }
            return;
        }

        if (user != null) {
            MuterEvent.mute(player.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("loginmessage", "MC")));
            player.sendTitle(
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("logintitles1", "MC")),
                    AuthTG.getMessage("logintitles2", "MC"),
                    20,
                    10000000,
                    0
            );
        } else {
            MuterEvent.mute(player.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("registermessage", "MC")));
            player.sendTitle(
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("registertitles1", "MC")),
                    AuthTG.getMessage("registertitles2", "MC"),
                    20,
                    10000000,
                    0
            );
        }
    }
}
