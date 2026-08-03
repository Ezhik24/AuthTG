package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;
import org.jetbrains.annotations.NotNull;

public class IPCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("authtg.ip")) {
            MessageHelper.send(commandSender, AuthTG.getMessage("ipnoperm", "MC"));
            return false;
        }
        if (strings.length != 1) {
            MessageHelper.send(commandSender, AuthTG.getMessage("ipusage", "MC"));
            return false;
        }
        Player player = Bukkit.getPlayer(strings[0]);
        if (player == null) {
            MessageHelper.send(commandSender, AuthTG.getMessage("ipnotfound", "MC"));
            return false;
        }
        MessageHelper.send(commandSender, AuthTG.getMessage("ipsuccess", "MC").replace("{PLAYER}", player.getName()).replace("{IP}", player.getAddress().getAddress().toString()));
        return true;
    }
}
