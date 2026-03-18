package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;

import java.util.logging.Level;

public class RegisterCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (AuthTG.notRegAndLogin) {
            MessageHelper.send(commandSender, AuthTG.getMessage("registeroff", "MC"));
            return false;
        }

        if (strings.length < 2) {
            MessageHelper.send(commandSender, AuthTG.getMessage("registerusage", "MC"));
            return false;
        }

        Player player = (Player) commandSender;

        if (AuthTG.loader.isActive(player.getUniqueId())) {
            MessageHelper.send(player, AuthTG.getMessage("alreadyreg", "MC"));
            return false;
        }

        if (strings[0].length() < AuthTG.minLenghtPassword || strings[0].length() > AuthTG.maxLenghtPassword) {
            MessageHelper.send(player, AuthTG.getMessage("registerlenght", "MC")
                    .replace("{MIN}", String.valueOf(AuthTG.minLenghtPassword))
                    .replace("{MAX}", String.valueOf(AuthTG.maxLenghtPassword)));
            return false;
        }

        if (!strings[0].equals(strings[1])) {
            MessageHelper.send(player, AuthTG.getMessage("registernomatch", "MC"));
            return false;
        }

        if (AuthTG.loader.getIpsRegistration(player.getAddress().getAddress().toString()) >= AuthTG.ipregmax) {
            MessageHelper.send(player, AuthTG.getMessage("registeripregmax", "MC"));
            return false;
        }

        AuthTG.loader.setPlayerName(player.getUniqueId(), player.getName());
        AuthTG.loader.setPasswordHash(player.getUniqueId(), strings[0]);
        AuthTG.loader.setActive(player.getUniqueId(), true);
        AuthTG.loader.setIpRegistration(player.getUniqueId(), player.getAddress().getAddress().toString());

        if (AuthTG.authNecessarily && AuthTG.isTelegramEnabled()) {
            String activeText = AuthTG.getMessage("authtgactivetext", "MC");
            MessageHelper.send(player, activeText);
            MuterEvent.mute(player.getName(), MessageHelper.legacySection(activeText));
            MessageHelper.showTitle(
                    player,
                    AuthTG.getMessage("authtgactives1", "MC"),
                    AuthTG.getMessage("authtgactives2", "MC")
            );
        } else {
            MessageHelper.send(player, AuthTG.getMessage("registersuccess", "MC"));
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
            AuthHandler.removeTimeout(player.getUniqueId());
        }

        return true;
    }
}
