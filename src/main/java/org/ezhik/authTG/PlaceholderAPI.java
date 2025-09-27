package org.ezhik.authTG;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "authtg";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ezhik24";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equals("username")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.loader.getUserName(player.getUniqueId()) : AuthTG.config.getString("placeholders.none");
        }
        if (params.equals("activetg")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.config.getString("placeholders.activetg.true") : AuthTG.config.getString("placeholders.activetg.false");
        }
        if (params.equals("lastname")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.loader.getLastName(player.getUniqueId()) : AuthTG.config.getString("placeholders.none");
        }
        if (params.equals("firstname")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.loader.getFirstName(player.getUniqueId()) : AuthTG.config.getString("placeholders.none");
        }
        if (params.equals("twofactor")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.config.getString("placeholders.twofactor.true") : AuthTG.config.getString("placeholders.twofactor.false");
        }
        if (params.equals("status")) {
            if (AuthTG.loader.isAdmin(player.getUniqueId())) {
                return AuthTG.config.getString("placeholders.status.admin");
            }else if (AuthTG.loader.getCommands(player.getUniqueId()) != null) {
                return AuthTG.config.getString("placeholders.status.moderator");
            } else {
                return AuthTG.config.getString("placeholders.status.user");
            }
        }
        return null;
    }
}
