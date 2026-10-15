package org.ezhik.authTG.commandMC;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.ezhik.authTG.util.MessageHelper;
public final class PremiumCMD implements CommandExecutor {
 public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
  if (!(sender instanceof Player player)) { sender.sendMessage("Only players can use this command."); return true; }
  if (Bukkit.getPluginCommand("flp") == null) {
   MessageHelper.send(player, "<red>Premium-вход недоступен: установите FastLoginPlus на Velocity и lobby."); return true;
  }
  return player.performCommand("flp premium");
 }
}
