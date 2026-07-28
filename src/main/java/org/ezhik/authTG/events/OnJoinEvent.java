package org.ezhik.authTG.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;
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
                event.joinMessage(null);
                Handler.kick(
                        p.getName(),
                        MessageHelper.legacySection(AuthTG.getMessage("ban", "MC"))
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
                event.joinMessage(null);
                Handler.kick(
                        p.getName(),
                        MessageHelper.legacySection(AuthTG.getMessage("ban", "MC"))
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
        if (!AuthTG.velocity) {
            if (AuthTG.world.equals("none")) {
                FreezerEvent.freezeplayer(p, p.getLocation());
            } else {
                Location playerloc = p.getLocation();
                Location loc = new Location(
                        Bukkit.getWorld(AuthTG.world),
                        AuthTG.locationX,
                        AuthTG.locationY,
                        AuthTG.locationZ,
                        AuthTG.locationYaw,
                        AuthTG.locationPitch
                );
                FreezerEvent.beforeFreeze.put(p.getName(), playerloc);
                Handler.teleport(p.getName(), loc);
                FreezerEvent.freezeplayer(p, loc);
            }
        } else {
            if (AuthTG.velocityAuthorizationWorld.equals("none")) {
                FreezerEvent.freezeplayer(p, p.getLocation());
            } else {
                Location loc = new Location(
                        Bukkit.getWorld(AuthTG.velocityAuthorizationWorld),
                        AuthTG.velocityAuthorizationX,
                        AuthTG.velocityAuthorizationY,
                        AuthTG.velocityAuthorizationZ,
                        AuthTG.velocityAuthorizationYaw,
                        AuthTG.velocityAuthorizationPitch
                );
                Handler.teleport(p.getName(), loc);
                FreezerEvent.freezeplayer(p, loc);
            }
        }

        if (AuthTG.forbiddenNicknames.contains(p.getName())) {
            Handler.kick(p.getName(),
                    MessageHelper.legacySection(AuthTG.getMessage("forbiddennickname", "MC")));
            return;
        }

        if (p.getName().length() < AuthTG.minLenghtNickname || p.getName().length() > AuthTG.maxLenghtNickname) {
            Handler.kick(
                    p.getName(),
                    MessageHelper.legacySection(AuthTG.getMessage("nicknamelenght", "MC")
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
            if (AuthTG.openimmediately) {
                Captcha.beginChallenge(p);
                Captcha.openFor(p);
                MessageHelper.send(p, mc("captchaopened",
                        "<green>Открываем капчу..."));
                return;
            }

            Captcha.beginChallenge(p);

            String text = mc("captchawaittext",
                    "<green>Пройдите капчу, чтобы продолжить вход. Используйте <yellow>/captcha<green>.");
            MuterEvent.mute(p.getName(), MessageHelper.legacySection(text));
            MessageHelper.send(p, text);
            MessageHelper.showTitle(
                    p,
                    mc("captchawaittitle", "<red><bold>Пройдите капчу"),
                    mc("captchawaitsubtitle", "<gray>Напишите /captcha, чтобы открыть меню")
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
            if (AuthTG.velocity) {
                if (!AuthTG.velocityAfterAuthorizationWorld.equals("none")) {
                    Location loc = new Location(
                            Bukkit.getWorld(AuthTG.velocityAfterAuthorizationWorld),
                            AuthTG.velocityAfterAuthorizationX,
                            AuthTG.velocityAfterAuthorizationY,
                            AuthTG.velocityAfterAuthorizationZ,
                            AuthTG.velocityAfterAuthorizationYaw,
                            AuthTG.velocityAfterAuthorizationPitch
                    );
                    Handler.teleport(p.getName(), loc);
                }
            }
            FreezerEvent.unfreezeplayer(p.getName());
            if (AuthTG.kickTimeout != 0) {
                AuthHandler.removeTimeout(p.getUniqueId());
            }
            return;
        }

        if (AuthTG.notRegAndLogin && AuthTG.authNecessarily) {
            if (AuthTG.isTelegramEnabled() && AuthTG.authNecessarilyPrefer.equals("TG")) {
                if (AuthTG.velocity) AuthTG.sendVelocityAuthPacket(p, "auth_start");
                if (user != null && user.activetg) {
                    String joinText = AuthTG.getMessage("joininaccounttext", "MC");
                    MuterEvent.mute(p.getName(), MessageHelper.legacySection(joinText));
                    MessageHelper.showTitle(
                            p,
                            AuthTG.getMessage("joininaccounts1", "MC"),
                            AuthTG.getMessage("joininaccounts2", "MC")
                    );

                    user.sendLoginAcceptedAsync(
                            AuthTG.getMessage("loginaccept", "TG")
                                    .replace("{PLAYER}", user.playername)
                                    .replace("{IP}", p.getAddress().getAddress().getHostAddress())
                    );
                } else {
                    String activeText = AuthTG.getMessage("authtgactivetext", "MC");
                    MuterEvent.mute(p.getName(), MessageHelper.legacySection(activeText));
                    MessageHelper.showTitle(
                            p,
                            AuthTG.getMessage("authtgactives1", "MC"),
                            AuthTG.getMessage("authtgactives2", "MC")
                    );
                }
            } else if (AuthTG.isVKEnabled() && AuthTG.authNecessarilyPrefer.equals("VK")) {
                if (AuthTG.velocity) AuthTG.sendVelocityAuthPacket(p, "auth_start");
                if (user != null && user.activevk) {
                    String joinText = AuthTG.getMessage("joininaccounttextvk", "MC");
                    MuterEvent.mute(p.getName(), MessageHelper.legacySection(joinText));
                    MessageHelper.showTitle(
                            p,
                            AuthTG.getMessage("joininaccounts1vk", "MC"),
                            AuthTG.getMessage("joininaccounts2vk", "MC")
                    );

                    AuthTG.vk.sendLoginAccept(user.peerid, AuthTG.getMessage("vkloginaccepted", "VK").replace("{PLAYER}", user.playername), p.getUniqueId());
                } else {
                    String activeText = AuthTG.getMessage("authtgactivetext", "MC");
                    MuterEvent.mute(p.getName(), MessageHelper.legacySection(activeText));
                    MessageHelper.showTitle(
                            p,
                            AuthTG.getMessage("authtgactives1", "MC"),
                            AuthTG.getMessage("authtgactives2", "MC")
                    );
                }
            } else {
                if (AuthTG.velocity) {
                    if (!AuthTG.velocityAfterAuthorizationWorld.equals("none")) {
                        Location loc = new Location(
                                Bukkit.getWorld(AuthTG.velocityAfterAuthorizationWorld),
                                AuthTG.velocityAfterAuthorizationX,
                                AuthTG.velocityAfterAuthorizationY,
                                AuthTG.velocityAfterAuthorizationZ,
                                AuthTG.velocityAfterAuthorizationYaw,
                                AuthTG.velocityAfterAuthorizationPitch
                        );
                        Handler.teleport(p.getName(), loc);
                    }
                }
                FreezerEvent.unfreezeplayer(p.getName());
                MuterEvent.unmute(p.getName());
                p.resetTitle();
                if (AuthTG.kickTimeout != 0) {
                    AuthHandler.removeTimeout(p.getUniqueId());
                }
            }
            return;
        }

        if (AuthTG.velocity) AuthTG.sendVelocityAuthPacket(p, "auth_start");
        if (user != null) {
            String loginText = AuthTG.getMessage("loginmessage", "MC");
            MuterEvent.mute(p.getName(), MessageHelper.legacySection(loginText));
            MessageHelper.send(p, loginText);
            MessageHelper.showTitle(
                    p,
                    AuthTG.getMessage("logintitles1", "MC"),
                    AuthTG.getMessage("logintitles2", "MC")
            );
        } else {
            String registerText = AuthTG.getMessage("registermessage", "MC");
            MuterEvent.mute(p.getName(), MessageHelper.legacySection(registerText));
            MessageHelper.send(p, registerText);
            MessageHelper.showTitle(
                    p,
                    AuthTG.getMessage("registertitles1", "MC"),
                    AuthTG.getMessage("registertitles2", "MC")
            );
        }
    }

    private static String mc(String key, String fallback) {
        String value = AuthTG.getMessage(key, "MC");
        return value == null || value.isBlank() ? fallback : value;
    }

}
