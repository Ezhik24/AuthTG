package org.ezhik.authTG.util;

import org.bukkit.Bukkit;
import org.ezhik.authTG.AuthTG;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AsyncBridge {

    private AsyncBridge() {
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(AuthTG.getInstance(), runnable);
    }

    public static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }

        Bukkit.getScheduler().runTask(AuthTG.getInstance(), runnable);
    }

    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();

        runAsync(() -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    public static <T> void supplyAsync(
            Supplier<T> supplier,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError
    ) {
        runAsync(() -> {
            try {
                T result = supplier.get();
                runSync(() -> onSuccess.accept(result));
            } catch (Throwable t) {
                runSync(() -> onError.accept(t));
            }
        });
    }
}