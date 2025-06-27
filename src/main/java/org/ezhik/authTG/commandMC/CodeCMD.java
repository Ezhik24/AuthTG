package org.ezhik.authTG.commandMC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CodeCMD implements CommandExecutor {
    public static Map<UUID, String> code = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            System.out.println("[AuthTG] This command can only be used by players!");
            return false;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lКОманда введена неверно, введите: /code <код>"));
            return false;
        }
        Player player = (Player) commandSender;
        if (!strings[0].equals(code.get(player.getUniqueId()))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[&c&lAuthTG&f&l] &c&lКод неверный"));
            return false;
        }
        if (AuthTG.loader.getActiveTG(player.getUniqueId())) {
            AuthTG.loader.setActiveTG(player.getUniqueId(), false);
            AuthTG.loader.setTwofactor(player.getUniqueId(), false);
            code.remove(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] &c&lВы успешно отвязали аккаунт"));
        } else {
            AuthTG.loader.setActiveTG(player.getUniqueId(), true);
            code.remove(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[&c&lAuthTG&f&l] Ваш аккаунт успешно привязан"));
            User user = User.getUser(player.getUniqueId());
            user.sendMessage("Вы успешно привязали аккаунт");
        }
        return true;
    }
}
