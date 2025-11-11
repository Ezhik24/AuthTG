package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CodeCMD implements CommandExecutor {
    public static Map<UUID, String> code = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO,AuthTG.getMessage("notplayer", "CE"));
            return false;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("codeusage", "MC")));
            return false;
        }
        Player player = (Player) commandSender;
        if (!strings[0].equals(code.get(player.getUniqueId()))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',AuthTG.getMessage("codeuncorect", "MC")));
            return false;
        }
        if (AuthTG.authNecessarily) {
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
            AuthHandler.removeTimeout(player.getUniqueId());
        }
        if (AuthTG.loader.getActiveTG(player.getUniqueId())) {
            AuthTG.loader.setActiveTG(player.getUniqueId(), false);
            AuthTG.loader.setTwofactor(player.getUniqueId(), false);
            code.remove(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("codeunlink", "MC")));
        } else {
            AuthTG.loader.setActiveTG(player.getUniqueId(), true);
            code.remove(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTG.getMessage("codelink", "MC")));
            if (AuthTG.notRegAndLogin) {
                player.resetTitle();
                MuterEvent.unmute(player.getName());
                FreezerEvent.unfreezeplayer(player.getName());
                AuthTG.loader.setActive(player.getUniqueId(), true);
                AuthTG.loader.setPlayerName(player.getUniqueId(), player.getName());
            }
            User user = User.getUser(player.getUniqueId());
            user.sendMessage(AuthTG.getMessage("codelinkplayer", "TG"));
        }
        return true;
    }
}
