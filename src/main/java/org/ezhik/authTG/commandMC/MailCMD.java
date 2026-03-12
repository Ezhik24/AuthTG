package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.mail.MailCodeSession;
import org.ezhik.authTG.mail.MailCodeStore;
import org.ezhik.authTG.mail.MailDeliveryService;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.logging.Level;

public class MailCMD implements CommandExecutor {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        Player player = (Player) commandSender;

        if (!MailDeliveryService.isEnabled()) {
            player.sendMessage(color(AuthTG.getMessage("maildisabled", "MC")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(color(AuthTG.getMessage("mailusage", "MC")));
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "link":
                return handleLink(player, args);
            case "verify":
                return handleVerify(player, args);
            case "unlink":
                return handleUnlink(player);
            case "status":
                return handleStatus(player);
            default:
                player.sendMessage(color(AuthTG.getMessage("mailusage", "MC")));
                return true;
        }
    }

    private boolean handleLink(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(color(AuthTG.getMessage("maillinkusage", "MC")));
            return true;
        }

        if (!AuthTG.loader.isActive(player.getUniqueId())) {
            player.sendMessage(color(AuthTG.getMessage("mailnotactive", "MC")));
            return true;
        }

        if (!MailDeliveryService.hasValidProvider()) {
            player.sendMessage(color(AuthTG.getMessage("mailtransportinvalid", "MC")));
            return true;
        }

        String email = args[1].trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            player.sendMessage(color(AuthTG.getMessage("mailinvalidemail", "MC")));
            return true;
        }

        UUID uuid = player.getUniqueId();
        String code = MailCodeStore.generateCode(MailDeliveryService.getCodeLength());

        if (MailDeliveryService.isLocalMode()) {
            MailCodeStore.create(uuid, email, code, MailDeliveryService.getCodeExpireSeconds());
            player.sendMessage(color(AuthTG.getMessage("maillinksent", "MC")));
            player.sendMessage(color(AuthTG.getMessage("maillocalcode", "MC").replace("{CODE}", code)));
            return true;
        }

        String playerName = player.getName();
        String ip = getPlayerIp(player);

        player.sendMessage(color(AuthTG.getMessage("mailsending", "MC")));

        Bukkit.getScheduler().runTaskAsynchronously(AuthTG.getInstance(), () -> {
            boolean sent = MailDeliveryService.sendLinkCode(playerName, uuid, ip, email, code);

            Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
                Player online = Bukkit.getPlayer(uuid);
                if (online == null) {
                    return;
                }

                if (sent) {
                    MailCodeStore.create(uuid, email, code, MailDeliveryService.getCodeExpireSeconds());
                    online.sendMessage(color(AuthTG.getMessage("maillinksent", "MC")));
                } else {
                    online.sendMessage(color(AuthTG.getMessage("mailsenderror", "MC")));
                }
            });
        });

        return true;
    }

    private boolean handleVerify(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(color(AuthTG.getMessage("mailverifyusage", "MC")));
            return true;
        }

        UUID uuid = player.getUniqueId();
        MailCodeSession session = MailCodeStore.get(uuid);

        if (session == null) {
            player.sendMessage(color(AuthTG.getMessage("mailverifyexpired", "MC")));
            return true;
        }

        String inputCode = args[1].trim();

        if (!MailCodeStore.verify(uuid, inputCode)) {
            player.sendMessage(color(AuthTG.getMessage("mailverifywrong", "MC")));
            return true;
        }

        AuthTG.loader.setEmail(uuid, session.getEmail());
        AuthTG.loader.setVerifiedEmail(uuid, true);

        player.sendMessage(color(
                AuthTG.getMessage("mailverifysuccess", "MC")
                        .replace("{EMAIL}", session.getEmail())
        ));
        return true;
    }

    private boolean handleUnlink(Player player) {
        UUID uuid = player.getUniqueId();
        String email = AuthTG.loader.getEmail(uuid);
        boolean verified = AuthTG.loader.isVerifiedEmail(uuid);

        MailCodeStore.remove(uuid);

        if ((email == null || email.isBlank()) && !verified) {
            player.sendMessage(color(AuthTG.getMessage("mailunlinkempty", "MC")));
            return true;
        }

        AuthTG.loader.setEmail(uuid, "");
        AuthTG.loader.setVerifiedEmail(uuid, false);

        player.sendMessage(color(AuthTG.getMessage("mailunlinksuccess", "MC")));
        return true;
    }

    private boolean handleStatus(Player player) {
        UUID uuid = player.getUniqueId();

        MailCodeSession pending = MailCodeStore.get(uuid);
        if (pending != null) {
            player.sendMessage(color(
                    AuthTG.getMessage("mailstatuspending", "MC")
                            .replace("{EMAIL}", pending.getEmail())
            ));
        }

        String email = AuthTG.loader.getEmail(uuid);
        boolean verified = AuthTG.loader.isVerifiedEmail(uuid);

        if (email == null || email.isBlank()) {
            if (pending == null) {
                player.sendMessage(color(AuthTG.getMessage("mailstatusnone", "MC")));
            }
            return true;
        }

        if (verified) {
            player.sendMessage(color(
                    AuthTG.getMessage("mailstatusverified", "MC")
                            .replace("{EMAIL}", email)
            ));
        } else {
            player.sendMessage(color(
                    AuthTG.getMessage("mailstatusnotverified", "MC")
                            .replace("{EMAIL}", email)
            ));
        }

        return true;
    }

    private String getPlayerIp(Player player) {
        if (player.getAddress() != null && player.getAddress().getAddress() != null) {
            return player.getAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}