package org.ezhik.authTG.handlers;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.IPManager;
import org.ezhik.authTG.User;
public final class PremiumAuthService {
 private PremiumAuthService() {}
 public static void detectAndLogin(Player player, User user) {
  if (user == null || IPManager.isAuthorized(player) || Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;
  new BukkitRunnable() {
   private int attempts;
   public void run() {
    if (!player.isOnline() || IPManager.isAuthorized(player) || attempts++ >= 5) { cancel(); return; }
    String premium = PlaceholderAPI.setPlaceholders(player, "%fastloginplus_is_premium%");
    if (!"true".equalsIgnoreCase(premium)) return;
    cancel();
    if (!TwoFactorAuthService.beginSecondFactorOrLogin(player, user)) TwoFactorAuthService.completeLogin(player);
   }
  }.runTaskTimer(AuthTG.getInstance(), 20L, 20L);
 }
}
