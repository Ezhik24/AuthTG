package org.ezhik.authTG.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Handler extends BukkitRunnable {
    private static final Map<String, String> kickplayers = new ConcurrentHashMap<>();
    private static final Map<String, String> minecrfatmsg = new ConcurrentHashMap<>();
    private static final Map<String, String> dispatcCommand = new ConcurrentHashMap<>();
    private static final Map<String, Location> locationMap = new ConcurrentHashMap<>();

    @Override
    public void run() {

        // KICK
        if (!kickplayers.isEmpty()) {
            for (Map.Entry<String, String> e : kickplayers.entrySet()) {
                String name = e.getKey();
                String reason = e.getValue();

                Player player = Bukkit.getPlayer(name);
                if (player != null) {
                    player.kickPlayer(reason);
                }

                // удаляем именно эту пару, чтобы не снести новый reason, если его поменяли между чтением и удалением
                kickplayers.remove(name, reason);
            }
        }

        // CHAT
        if (!minecrfatmsg.isEmpty()) {
            for (Map.Entry<String, String> e : minecrfatmsg.entrySet()) {
                String name = e.getKey();
                String message = e.getValue();

                Player player = Bukkit.getPlayer(name);
                if (player != null) {
                    // отправка через chat (если message начинается с / — выполнится как команда игрока)
                    player.chat(message);
                }

                minecrfatmsg.remove(name, message);
            }
        }

        // DISPATCH COMMAND (console)
        if (!dispatcCommand.isEmpty()) {
            for (Map.Entry<String, String> e : dispatcCommand.entrySet()) {
                String name = e.getKey();
                String command = e.getValue();

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                dispatcCommand.remove(name, command);
            }
        }

        // TELEPORT
        if (!locationMap.isEmpty()) {
            for (Map.Entry<String, Location> e : locationMap.entrySet()) {
                String name = e.getKey();
                Location location = e.getValue();

                Player player = Bukkit.getPlayer(name);
                if (player != null && location != null) {
                    player.teleport(location);
                }

                locationMap.remove(name, location);
            }
        }
    }

    public static void kick(String name, String reason) {
        kickplayers.put(name, reason);
    }

    public static void sendMCmessage(String name, String message) {
        minecrfatmsg.put(name, message);
    }

    public static void dispatchCommand(String name, String command) {
        dispatcCommand.put(name, command);
    }

    public static void teleport(String name, Location location) {
        locationMap.put(name, location);
    }
}
