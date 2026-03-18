package org.ezhik.authTG.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageHelper {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    private static final Pattern MINI_MESSAGE_TAG_PATTERN = Pattern.compile("<[^<>]+>");
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private MessageHelper() {
    }

    public static void send(CommandSender sender, String text) {
        if (sender == null || text == null || text.isBlank()) {
            return;
        }

        sender.sendMessage(component(text));
    }

    public static void send(Player player, String text) {
        if (player == null || text == null || text.isBlank()) {
            return;
        }

        player.sendMessage(component(text));
    }

    public static void showTitle(Player player, String titleText, String subtitleText) {
        if (player == null) {
            return;
        }

        player.showTitle(Title.title(
                component(titleText == null ? "" : titleText),
                component(subtitleText == null ? "" : subtitleText),
                Title.Times.times(
                        Duration.ZERO,
                        Duration.ofDays(3650),
                        Duration.ZERO
                )
        ));
    }

    public static String legacySection(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        return LEGACY_SECTION.serialize(component(text));
    }

    public static Component component(String text) {
        if (text == null || text.isBlank()) {
            return Component.empty();
        }

        String normalized = normalizeHex(text);

        if (looksLikeMiniMessage(normalized)) {
            return MINI_MESSAGE.deserialize(normalized);
        }

        return LEGACY_AMPERSAND.deserialize(normalized);
    }

    private static boolean looksLikeMiniMessage(String text) {
        return MINI_MESSAGE_TAG_PATTERN.matcher(text).find();
    }

    private static String normalizeHex(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder legacy = new StringBuilder("&x");
            for (char c : hex.toCharArray()) {
                legacy.append('&').append(c);
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(legacy.toString()));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}