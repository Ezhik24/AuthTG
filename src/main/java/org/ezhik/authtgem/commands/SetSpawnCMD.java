package org.ezhik.authtgem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.User;

public class SetSpawnCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            Player player = (Player) commandSender;
            User.setSpawnLocation(player.getLocation());
        } else {
            if(strings[0].equals("none")){
                User.setSpawnLocation(null);
            }
        }
        return true;
    }
}
