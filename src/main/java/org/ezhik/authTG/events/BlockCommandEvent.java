package org.ezhik.authTG.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.captcha.Captcha;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BlockCommandEvent implements Listener {

    @EventHandler
    public void onCommmand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (Captcha.isPending(player.getUniqueId())) {
            String command = event.getMessage().split(" ")[0].toLowerCase(Locale.ROOT);

            if (!command.equals("/captcha")) {
                event.setCancelled(true);
                player.sendMessage(color(mc("captchawaittext",
                        "&aПройдите капчу, чтобы продолжить вход. Используйте &e/captcha&a.")));
            }
            return;
        }

        if (MuterEvent.isMute(player)) {
            Set<String> allowedCommands = new LinkedHashSet<>(List.of(
                    "/login", "/register", "/reg", "/l", "/code", "/2fa"
            ));

            for (String cmd : AuthTG.commandsPreAuthorization) {
                if (cmd == null) {
                    continue;
                }
                String normalized = cmd.trim().toLowerCase(Locale.ROOT);
                if (!normalized.isBlank()) {
                    allowedCommands.add(normalized);
                }
            }

            String command = event.getMessage().split(" ")[0].toLowerCase(Locale.ROOT);
            if (!allowedCommands.contains(command)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        AuthTG.getMessage("joinblock", "MC")));
            }
        }

        if (MuterEvent.isMuteChat(player)) {
            String[] args = event.getMessage().split(" ");
            String command = args[0].toLowerCase(Locale.ROOT);

            if (!AuthTG.mutecommands.contains(command)) {
                return;
            }

            List<Object> list = MuterEvent.getMuteChat(player.getName());
            if (list == null || list.isEmpty()) {
                return;
            }

            if (!"0".equals(String.valueOf(list.get(0))) && LocalDateTime.now().isAfter((LocalDateTime) list.get(0))) {
                MuterEvent.unmuteChat(player.getName());
                AuthTG.loader.deleteMute(player.getUniqueId());
                return;
            }

            String message;
            if ("0".equals(String.valueOf(list.get(0)))) {
                message = ChatColor.translateAlternateColorCodes('&',
                                AuthTG.getMessage("mute", "MC"))
                        .replace("{TIMEMUTE}", "навсегда")
                        .replace("{REASON}", AuthTG.loader.getMuteReason(player.getUniqueId()))
                        .replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(player.getUniqueId()))
                        .replace("{ADMIN}", AuthTG.loader.getMuteAdmin(player.getUniqueId()));
            } else {
                message = ChatColor.translateAlternateColorCodes('&',
                                AuthTG.getMessage("mute", "MC"))
                        .replace("{TIMEMUTE}", AuthTG.loader.getMuteTime(player.getUniqueId()))
                        .replace("{REASON}", AuthTG.loader.getMuteReason(player.getUniqueId()))
                        .replace("{TIME}", AuthTG.loader.getMuteTimeAdmin(player.getUniqueId()))
                        .replace("{ADMIN}", AuthTG.loader.getMuteAdmin(player.getUniqueId()));
            }

            player.sendMessage(message);
            event.setCancelled(true);
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
