package org.ezhik.authTG.otherAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.ezhik.authTG.AuthTG;
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
        return AuthTG.getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equals("username")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.loader.getUserName(player.getUniqueId()) : AuthTG.getPlaceholderMessage("none", "none");
        }
        if (params.equals("activetg")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.getPlaceholderMessage("activetg", "true") : AuthTG.getPlaceholderMessage("activetg", "false");
        }
        if (params.equals("lastname")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.loader.getLastName(player.getUniqueId()) : AuthTG.getPlaceholderMessage("none", "none");
        }
        if (params.equals("firstname")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.loader.getFirstName(player.getUniqueId()) : AuthTG.getPlaceholderMessage("none", "none");
        }
        if (params.equals("twofactor")) {
            return AuthTG.loader.getActiveTG(player.getUniqueId()) ? AuthTG.getPlaceholderMessage("twofactor", "true") : AuthTG.getPlaceholderMessage("twofactor", "false");
        }
        if (params.equals("status")) {
            if (AuthTG.loader.isAdmin(player.getUniqueId())) {
                return AuthTG.getPlaceholderMessage("status", "admin");
            }else if (AuthTG.loader.getCommands(player.getUniqueId()) != null) {
                return AuthTG.getPlaceholderMessage("status", "moderator");
            } else {
                return AuthTG.getPlaceholderMessage("status", "user");
            }
        }
        return null;
    }
}
