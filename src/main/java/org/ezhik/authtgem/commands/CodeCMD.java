package org.ezhik.authtgem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ezhik.authtgem.AuthTGEM;
import org.ezhik.authtgem.User;
import org.ezhik.authtgem.events.FreezerEvent;
import org.ezhik.authtgem.events.MuterEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CodeCMD implements CommandExecutor {
    public static Map<UUID,String> code = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("code_wrong_command")));
            return false;
        }
        Player player = (Player) commandSender;
        File file = new File("plugins/Minetelegram/users/" + player.getUniqueId() + ".yml");
        YamlConfiguration userconf = YamlConfiguration.loadConfiguration(file);
        if (!strings[0].equals(code.get(player.getUniqueId()))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("code_invalid")));
            return false;
        }
        if (AuthTGEM.bot.authNecessarily) {
            FreezerEvent.unfreezeplayer(player.getName());
            MuterEvent.unmute(player.getName());
            player.resetTitle();
        }
        if (userconf.getBoolean("active")) {
            userconf.set("active", false);
            userconf.set("twofactor", false);
            code.remove(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("code_account_deactivated")));
            try {
                userconf.save(file);
            } catch (IOException e) {
                System.out.println("Error saving config file: " + e);
            }
        } else {
            userconf.set("active", true);
            code.remove(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AuthTGEM.messageMC.get("code_account_activated")));
            if (AuthTGEM.bot.notRegAndLogin) {
                player.resetTitle();
                MuterEvent.unmute(player.getName());
                FreezerEvent.unfreezeplayer(player.getName());
                userconf.set("playername", player.getName());
            }
            try {
                userconf.save(file);
            } catch (IOException e) {
                System.out.println("Error saving config file: " + e);
            }
            User user = User.getUser(player.getUniqueId());
            user.sendMessage(AuthTGEM.messageTG.get("code_account_activated"));
        }
        return true;
    }
}