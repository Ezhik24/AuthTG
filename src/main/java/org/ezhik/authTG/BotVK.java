package org.ezhik.authTG;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.ezhik.authTG.handlers.VKCheckHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotVK {
    private final String token;
    private static final String API = "https://api.vk.com/method/";
    private static final String VERSION = "5.199";
    private static final int GROUP_ID = 217145799;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    private final ExecutorService vkIoExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "AuthTG-vkIO");
        thread.setDaemon(true);
        return thread;
    });

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public BotVK(String token) {
        this.token = token;
    }

    public void initializationBot() {
        if (!AuthTG.isVKEnabled()) {
            AuthTG.logger.warning("[AuthTG] VK integration is disabled in config.yml (vk.enabled: false)");
            return;
        }

        if (token == null || token.isBlank() || "changeme".equals(token)) {
            AuthTG.logger.warning("[AuthTG] Please, set token in config.yml (VK)");
            return;
        }

        getLongPollServerAsync().whenComplete((lpResponse, throwable) -> {
            if (throwable != null) {
                AuthTG.logger.severe("[AuthTG] VK long poll init failed: " + throwable.getMessage());
                return;
            }

            if (lpResponse == null) {
                AuthTG.logger.severe("[AuthTG] VK long poll init failed: response is null");
                return;
            }

            try {
                JSONObject response = lpResponse.getJSONObject("response");

                String server = response.getString("server");
                String key = response.getString("key");
                String ts = response.getString("ts");

                Bukkit.getScheduler().runTask(AuthTG.getInstance(), () -> {
                    if (shutdown.get()) {
                        return;
                    }

                    AuthTG.logger.info("[AuthTG] VKBot started");
                    new VKCheckHandler(this, server, ts, key)
                            .runTaskTimer(AuthTG.getInstance(), 0L, 20L);
                });
            } catch (Exception e) {
                AuthTG.logger.severe("[AuthTG] Cannot parse VK long poll init response: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<JSONObject> getLongPollServerAsync() {
        return CompletableFuture.supplyAsync(this::getLongPollServerNow, vkIoExecutor);
    }

    private JSONObject getLongPollServerNow() {
        String url = API + "groups.getLongPollServer"
                + "?group_id=" + GROUP_ID
                + "&access_token=" + token
                + "&v=" + VERSION;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IOException("VK response body is null");
            }

            String body = response.body().string();
            return new JSONObject(body);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void sendMessage(int peerId, String message) {
        if (!AuthTG.isVKEnabled() || shutdown.get()) {
            return;
        }

        if (peerId <= 0) {
            return;
        }

        if (message == null) {
            message = "";
        }

        String url = API + "messages.send";
        RequestBody body = new FormBody.Builder()
                .add("peer_id", String.valueOf(peerId))
                .add("message", message)
                .add("random_id", String.valueOf(System.nanoTime()))
                .add("access_token", token)
                .add("v", VERSION)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        CompletableFuture.runAsync(() -> executeSendMessage(request), vkIoExecutor)
                .exceptionally(throwable -> {
                    AuthTG.logger.severe("[AuthTG] VK sendMessage failed: " + throwable.getMessage());
                    return null;
                });
    }

    private void executeSendMessage(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                return;
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            if (json.has("error")) {
                AuthTG.logger.warning("[AuthTG] VK API error on messages.send: " + json.getJSONObject("error"));
            }
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public CompletableFuture<JSONObject> checkEvent(String server, String key, String ts) {
        if (!AuthTG.isVKEnabled() || shutdown.get()) {
            return CompletableFuture.completedFuture(null);
        }

        String url = server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25";

        Request request = new Request.Builder()
                .url(url)
                .build();

        return CompletableFuture.supplyAsync(() -> checkEventNow(request), vkIoExecutor);
    }

    private JSONObject checkEventNow(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }

            return new JSONObject(response.body().string());
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }

    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return;
        }

        vkIoExecutor.shutdownNow();
        client.dispatcher().executorService().shutdownNow();
        client.connectionPool().evictAll();
    }
}