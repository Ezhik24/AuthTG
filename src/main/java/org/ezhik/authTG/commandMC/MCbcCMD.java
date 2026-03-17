package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.util.MessageHelper;

public class MCbcCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String text = String.join(" ", strings);
        String message = AuthTG.getMessage("mcbc", "MC") + text;

        if (commandSender instanceof Player) {
            if (!commandSender.hasPermission("authtg.mcbc")) {
                MessageHelper.send(commandSender, AuthTG.getMessage("mcbcnoperm", "MC"));
                return false;
            }

            broadcast(message);
            MessageHelper.send(commandSender, AuthTG.getMessage("mcbcsuccess", "MC"));
        } else {
            broadcast(message);
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            MessageHelper.send(console, AuthTG.getMessage("mcbcsuccess", "MC"));
        }

        return true;
    }

    private void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MessageHelper.send(player, message);
        }
        MessageHelper.send(Bukkit.getConsoleSender(), message);
    }
}