package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.IPManager;
import org.ezhik.authTG.User;
import org.ezhik.authTG.handlers.TwoFactorAuthService;
import org.ezhik.authTG.util.AsyncBridge;
import org.ezhik.authTG.util.MessageHelper;

import java.util.UUID;
import java.util.logging.Level;

public class LoginCMD implements CommandExecutor {

    private record LoginCheckResult(
            boolean passwordValid,
            boolean hasIpRegistration
    ) {
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            AuthTG.logger.log(Level.INFO, AuthTG.getMessage("notplayer", "CE"));
            return false;
        }

        if (AuthTG.notRegAndLogin) {
            MessageHelper.send(commandSender, AuthTG.getMessage("loginoff", "MC"));
            return false;
        }

        if (IPManager.isAuthorized(player)) {
            MessageHelper.send(player, AuthTG.getMessage("alreadylogin", "MC"));
            return false;
        }

        if (strings.length != 1) {
            MessageHelper.send(player, AuthTG.getMessage("loginnousage", "MC"));
            return false;
        }

        final UUID uuid = player.getUniqueId();
        final String password = strings[0];
        final String ip = player.getAddress() != null && player.getAddress().getAddress() != null
                ? player.getAddress().getAddress().toString()
                : "";

        AsyncBridge.supplyAsync(
                () -> new LoginCheckResult(
                        AuthTG.loader.passwordValid(uuid, password),
                        AuthTG.loader.containsIpRegistration(uuid)
                ),
                result -> {
                    Player online = player.isOnline() ? player : null;
                    if (online == null) {
                        return;
                    }

                    if (!result.passwordValid()) {
                        MessageHelper.send(online, AuthTG.getMessage("loginpassnovalid", "MC"));
                        return;
                    }

                    AsyncBridge.runAsync(() -> {
                        if (!result.hasIpRegistration()) {
                            AuthTG.loader.setIpRegistration(uuid, ip);
                        }

                        AsyncBridge.runSync(() -> {
                            Player current = player.isOnline() ? player : null;
                            if (current == null) {
                                return;
                            }


                            User user = User.getUser(uuid);
                            if (user == null) {
                                MessageHelper.send(current, AuthTG.getMessage("loginpassnovalid", "MC"));
                                return;
                            }

                            if (TwoFactorAuthService.beginSecondFactorOrLogin(current, user)) {
                                return;
                            }

                            TwoFactorAuthService.completeLogin(current);
                        });
                    });
                },
                throwable -> {
                    AuthTG.logger.severe("[AuthTG] Login async error: " + throwable.getMessage());
                    if (player.isOnline()) {
                        MessageHelper.send(player, "<red>Произошла ошибка при авторизации.");
                    }
                }
        );

        return true;
    }
}
