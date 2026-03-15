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
        Player p = event.getPlayer();
        User user = User.getUser(p.getUniqueId());
        LocalDateTime date = LocalDateTime.now();

        if (AuthTG.loader.getBanTime(p.getUniqueId()) != null) {
            if (AuthTG.loader.getBanTime(p.getUniqueId()).equals("0")) {
                event.setJoinMessage(null);
                Handler.kick(
                        p.getName(),
                        ChatColor.translateAlternateColorCodes('&',
                                        AuthTG.getMessage("ban", "MC"))
                                .replace("{REASON}", AuthTG.loader.getBanReason(p.getUniqueId()))
                                .replace("{TIMEBAN}", "навсегда")
                                .replace("{TIME}", AuthTG.loader.getBanTimeAdmin(p.getUniqueId()))
                                .replace("{ADMIN}", AuthTG.loader.getBanAdmin(p.getUniqueId()))
                );
                return;
            }

            LocalDateTime date1 = LocalDateTime.parse(
                    AuthTG.loader.getBanTime(p.getUniqueId()),
                    DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")
            );

            if (date.isAfter(date1)) {
                AuthTG.loader.deleteBan(p.getUniqueId());
            } else {
                event.setJoinMessage(null);
                Handler.kick(
                        p.getName(),
                        ChatColor.translateAlternateColorCodes('&',
                                        AuthTG.getMessage("ban", "MC"))
                                .replace("{REASON}", AuthTG.loader.getBanReason(p.getUniqueId()))
                                .replace("{TIMEBAN}", AuthTG.loader.getBanTime(p.getUniqueId()))
                                .replace("{TIME}", AuthTG.loader.getBanTimeAdmin(p.getUniqueId()))
                                .replace("{ADMIN}", AuthTG.loader.getBanAdmin(p.getUniqueId()))
                );
                return;
            }
        }

        if (IPManager.isAuthorized(p)) {
            if (AuthTG.isTelegramEnabled() && user != null && user.activetg) {
                user.sendMessage(
                        AuthTG.getMessage("joinacc", "TG")
                                .replace("{IP}", p.getAddress().getAddress().toString())
                                .replace("/", "")
                );
            }
            return;
        }

        if (AuthTG.world.equals("none")) {
            FreezerEvent.freezeplayer(p, p.getLocation());
        } else {
            Location playerloc = p.getLocation();
            Location loc = new Location(
                    Bukkit.getWorld(AuthTG.world),
                    AuthTG.locationX,
                    AuthTG.locationY,
                    AuthTG.locationZ
            );
            FreezerEvent.beforeFreeze.put(p.getName(), playerloc);
            Handler.teleport(p.getName(), loc);
            FreezerEvent.freezeplayer(p, loc);
        }

        if (AuthTG.forbiddenNicknames.contains(p.getName())) {
            Handler.kick(p.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("forbiddennickname", "MC")));
            return;
        }

        if (p.getName().length() < AuthTG.minLenghtNickname || p.getName().length() > AuthTG.maxLenghtNickname) {
            Handler.kick(
                    p.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("nicknamelenght", "MC")
                                    .replace("{MIN}", String.valueOf(AuthTG.minLenghtNickname))
                                    .replace("{MAX}", String.valueOf(AuthTG.maxLenghtNickname)))
            );
            return;
        }

        if (AuthTG.kickTimeout != 0) {
            AuthHandler.setTimeout(p.getUniqueId(), AuthTG.kickTimeout);
        }

        if (AuthTG.getInstance().getConfig().getBoolean("captcha.enabled", false)
                && CaptchaTimeoutStore.shouldShowCaptcha(p.getUniqueId())) {

            Captcha.beginChallenge(p);

            String text = color(mc("captchawaittext",
                    "&aПройдите капчу, чтобы продолжить вход. Используйте &e/captcha&a."));
            MuterEvent.mute(p.getName(), text);
            p.sendMessage(text);
            p.sendTitle(
                    color(mc("captchawaittitle", "&c&lПройдите капчу")),
                    color(mc("captchawaitsubtitle", "&7Напишите /captcha, чтобы открыть меню")),
                    20, 10000000, 0
            );
            return;
        }

        loadRegistration(p, user);
    }

    public static void loadRegistration(Player p, User user) {
        if (AuthTG.kickTimeout != 0) {
            AuthHandler.setTimeout(p.getUniqueId(), AuthTG.kickTimeout);
        }

        if (AuthTG.notRegAndLogin && !AuthTG.authNecessarily) {
            FreezerEvent.unfreezeplayer(p.getName());
            if (AuthTG.kickTimeout != 0) {
                AuthHandler.removeTimeout(p.getUniqueId());
            }
            return;
        }

        if (AuthTG.notRegAndLogin && AuthTG.authNecessarily) {
            if (AuthTG.isTelegramEnabled()) {
                if (user != null && user.activetg) {
                    MuterEvent.mute(p.getName(),
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("joininaccounttext", "MC")));
                    p.sendTitle(
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("joininaccounts1", "MC")),
                            AuthTG.getMessage("joininaccounts2", "MC"),
                            20, 10000000, 0
                    );

                    user.sendLoginAcceptedAsync(
                            AuthTG.getMessage("loginaccept", "TG")
                                    .replace("{PLAYER}", user.playername)
                                    .replace("{IP}", p.getAddress().getAddress().getHostAddress())
                    );
                } else {
                    MuterEvent.mute(p.getName(),
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("authtgactivetext", "MC")));
                    p.sendTitle(
                            ChatColor.translateAlternateColorCodes('&',
                                    AuthTG.getMessage("authtgactives1", "MC")),
                            AuthTG.getMessage("authtgactives2", "MC"),
                            20, 10000000, 0
                    );
                }
            } else {
                FreezerEvent.unfreezeplayer(p.getName());
                MuterEvent.unmute(p.getName());
                p.resetTitle();
                if (AuthTG.kickTimeout != 0) {
                    AuthHandler.removeTimeout(p.getUniqueId());
                }
            }
            return;
        }

        if (user != null) {
            MuterEvent.mute(p.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("loginmessage", "MC")));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    AuthTG.getMessage("loginmessage", "MC")));
            p.sendTitle(
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("logintitles1", "MC")),
                    AuthTG.getMessage("logintitles2", "MC"),
                    20, 10000000, 0
            );
        } else {
            MuterEvent.mute(p.getName(),
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("registermessage", "MC")));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    AuthTG.getMessage("registermessage", "MC")));
            p.sendTitle(
                    ChatColor.translateAlternateColorCodes('&',
                            AuthTG.getMessage("registertitles1", "MC")),
                    AuthTG.getMessage("registertitles2", "MC"),
                    20, 10000000, 0
            );
        }
    }

    private static String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
