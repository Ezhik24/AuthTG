package org.ezhik.authTG.commandMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ezhik.authTG.AuthTG;
import org.ezhik.authTG.migrates.AuthMeMigrate;
import org.ezhik.authTG.util.AsyncBridge;
import org.ezhik.authTG.util.MessageHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class AuthTGCMD implements CommandExecutor {
    private static final AtomicBoolean MIGRATION_RUNNING = new AtomicBoolean();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player && !commandSender.hasPermission("authtg.authtg")) {
            MessageHelper.send(commandSender, AuthTG.getMessage("authtgnoperm", "MC"));
            return false;
        }
        if (strings.length < 1) {
            MessageHelper.send(commandSender, AuthTG.getMessage("authtgusage", "MC"));
            return false;
        }

        switch (strings[0].toLowerCase()) {
            case "reload":
                AuthTG.getInstance().reloadPluginRuntime();
                MessageHelper.send(commandSender, AuthTG.getMessage("authtgnobotmysql", "MC"));
                MessageHelper.send(commandSender, AuthTG.getMessage("authtgsuccess", "MC"));
                MessageHelper.send(commandSender, AuthTG.isTelegramEnabled()
                        ? "<green>Telegram integration: ENABLED"
                        : "<yellow>Telegram integration: DISABLED");
                return true;
            case "migrate":
                if (strings.length < 2 || !strings[1].equalsIgnoreCase("authme")) {
                    MessageHelper.send(commandSender, "<yellow>Использование: /authtg migrate authme");
                    return false;
                }
                if (!MIGRATION_RUNNING.compareAndSet(false, true)) {
                    MessageHelper.send(commandSender, "<yellow>Миграция AuthMe уже выполняется.");
                    return true;
                }
                MessageHelper.send(commandSender, "<yellow>Начинаю импорт пользователей AuthMe...");
                AsyncBridge.supplyAsync(
                        AuthMeMigrate::migrate,
                        result -> {
                            MIGRATION_RUNNING.set(false);
                            MessageHelper.send(commandSender,
                                    "<green>Миграция AuthMe завершена. Импортировано: " + result.imported()
                                            + ", пропущено: " + result.skipped()
                                            + ", ошибок: " + result.failed() + ".");
                        },
                        error -> {
                            MIGRATION_RUNNING.set(false);
                            AuthTG.logger.severe("[AuthTG] AuthMe migration error: " + error.getMessage());
                            MessageHelper.send(commandSender, "<red>Ошибка миграции AuthMe: " + error.getMessage());
                        }
                );
                return true;
            default:
                MessageHelper.send(commandSender, AuthTG.getMessage("authtgusage", "MC"));
                return false;
        }
    }
}
