package org.ezhik.authTG.commandMC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.events.FreezerEvent;
import org.ezhik.authTG.events.MuterEvent;
import org.ezhik.authTG.handlers.AuthHandler;
import org.ezhik.authTG.handlers.Handler;
import org.ezhik.authTG.util.AsyncBridge;
import org.ezhik.authTG.util.MessageHelper;

import java.util.logging.Level;

public class RegisterCMD implements CommandExecutor {

    private record RegisterCheckResult(
            boolean alreadyRegistered,
            int ipRegistrations
    ) {
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
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

        String password = strings[0];
        String repeatPassword = strings[1];

        if (password.length() < AuthTG.minLenghtPassword || password.length() > AuthTG.maxLenghtPassword) {
            MessageHelper.send(player, AuthTG.getMessage("registerlenght", "MC")
                    .replace("{MIN}", String.valueOf(AuthTG.minLenghtPassword))
                    .replace("{MAX}", String.valueOf(AuthTG.maxLenghtPassword)));
            return false;
        }

        if (!password.equals(repeatPassword)) {
            MessageHelper.send(player, AuthTG.getMessage("registernomatch", "MC"));
            return false;
        }

        final var uuid = player.getUniqueId();
        final var playerName = player.getName();
        final var ip = player.getAddress() != null && player.getAddress().getAddress() != null
                ? player.getAddress().getAddress().toString()
                : "";

        AsyncBridge.supplyAsync(
                () -> new RegisterCheckResult(
                        AuthTG.loader.isActive(uuid),
                        AuthTG.loader.getIpsRegistration(ip)
                ),
                result -> {
                    Player online = player.isOnline() ? player : null;
                    if (online == null) {
                        return;
                    }

                    if (result.alreadyRegistered()) {
                        MessageHelper.send(online, AuthTG.getMessage("alreadyreg", "MC"));
                        return;
                    }

                    if (result.ipRegistrations() >= AuthTG.ipregmax) {
                        MessageHelper.send(online, AuthTG.getMessage("registeripregmax", "MC"));
                        return;
                    }

                    AsyncBridge.runAsync(() -> {
                        AuthTG.loader.setPlayerName(uuid, playerName);
                        AuthTG.loader.setPasswordHash(uuid, password);
                        AuthTG.loader.setActive(uuid, true);
                        AuthTG.loader.setIpRegistration(uuid, ip);

                        AsyncBridge.runSync(() -> {
                            Player current = player.isOnline() ? player : null;
                            if (current == null) {
                                return;
                            }

                            if (AuthTG.authNecessarily ) {
                                if (AuthTG.isTelegramEnabled() && AuthTG.authNecessarilyPrefer.equals("TG")) {
                                    String activeText = AuthTG.getMessage("authtgactivetext", "MC");
                                    MessageHelper.send(current, activeText);
                                    MuterEvent.mute(current.getName(), MessageHelper.legacySection(activeText));
                                    MessageHelper.showTitle(
                                            current,
                                            AuthTG.getMessage("authtgactives1", "MC"),
                                            AuthTG.getMessage("authtgactives2", "MC")
                                    );
                                } else if (AuthTG.isVKEnabled() && AuthTG.authNecessarilyPrefer.equals("VK")) {
                                    String activeText = AuthTG.getMessage("authtgactivetext", "MC");
                                    MuterEvent.mute(current.getName(), MessageHelper.legacySection(activeText));
                                    MessageHelper.showTitle(
                                            current,
                                            AuthTG.getMessage("authtgactives1", "MC"),
                                            AuthTG.getMessage("authtgactives2", "MC")
                                    );
                                }
                            } else {
                                if (AuthTG.velocity) {
                                    AuthTG.sendVelocityAuthPacket(player, "auth_success");
                                    if (!AuthTG.velocityAfterAuthorizationWorld.equals("none")) {
                                        Location loc = new Location(
                                                Bukkit.getWorld(AuthTG.velocityAfterAuthorizationWorld),
                                                AuthTG.velocityAfterAuthorizationX,
                                                AuthTG.velocityAfterAuthorizationY,
                                                AuthTG.velocityAfterAuthorizationZ,
                                                AuthTG.velocityAfterAuthorizationYaw,
                                                AuthTG.velocityAfterAuthorizationPitch
                                        );
                                        Handler.teleport(player.getName(), loc);
                                    }
                                }
                                MessageHelper.send(current, AuthTG.getMessage("registersuccess", "MC"));
                                FreezerEvent.unfreezeplayer(current.getName());
                                MuterEvent.unmute(current.getName());
                                current.resetTitle();
                                AuthHandler.removeTimeout(current.getUniqueId());
                            }
                        });
                    });
                },
                throwable -> {
                    AuthTG.logger.severe("[AuthTG] Register async error: " + throwable.getMessage());
                    if (player.isOnline()) {
                        MessageHelper.send(player, "<red>Произошла ошибка при регистрации.");
                    }
                }
        );

        return true;
    }
}