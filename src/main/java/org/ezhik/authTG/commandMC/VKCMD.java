package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.util.MessageHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class VKCMD implements CommandExecutor {
    public static Map<UUID, String> code = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        Player player = (Player) commandSender;

        if (!AuthTG.isVKEnabled()) {
            MessageHelper.send(player, "<red>VK integration is disabled in config.yml");
            return false;
        }

        if (strings.length == 0) {
            MessageHelper.send(commandSender, AuthTG.getMessage("codeusagevk", "MC"));
            return false;
        }

        if (!strings[0].equals(code.get(player.getUniqueId()))) {
            MessageHelper.send(player, AuthTG.getMessage("codeuncorect", "MC"));
            return false;
        }

        if (AuthTG.authNecessarily) {
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();

            if (AuthTG.kickTimeout != 0) {
                AuthHandler.removeTimeout(player.getUniqueId());
            }
        }

        if (AuthTG.loader.isActiveVK(player.getUniqueId())) {
            AuthTG.loader.setActiveVK(player.getUniqueId(), false);
            AuthTG.loader.setTwofactor(player.getUniqueId(), false);
            code.remove(player.getUniqueId());
            MessageHelper.send(player, AuthTG.getMessage("codeunlink", "MC"));
        } else {
            AuthTG.loader.setActiveVK(player.getUniqueId(), true);
            code.remove(player.getUniqueId());
            MessageHelper.send(player, AuthTG.getMessage("codelink", "MC"));

            if (AuthTG.notRegAndLogin) {
                player.resetTitle();
                MuterEvent.unmute(player.getName());
                FreezerEvent.unfreezeplayer(player.getName());
                AuthTG.loader.setActive(player.getUniqueId(), true);
                AuthTG.loader.setIpRegistration(player.getUniqueId(), player.getAddress().getAddress().toString());
                AuthTG.loader.setPlayerName(player.getUniqueId(), player.getName());
            }

            User user = User.getUser(player.getUniqueId());
            if (user != null) {
                AuthTG.vk.sendMessage(user.peerid, AuthTG.getMessage("codelinkplayer", "VK"));
            }
        }
        return true;
    }
}
