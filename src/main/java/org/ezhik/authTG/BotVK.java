package org.ezhik.authTG;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.ezhik.authTG.callbackQueryVK.CallbackQueryVK;
import org.ezhik.authTG.callbackQueryVK.NoCallbackQuery;
import org.ezhik.authTG.callbackQueryVK.YesCallbackQuery;
import org.ezhik.authTG.commandTG.CommandHandler;
import org.ezhik.authTG.commandVK.*;
import org.ezhik.authTG.handlers.VKCheckHandler;
import org.ezhik.authTG.nextStep.NextStepHandler;
import org.ezhik.authTG.nextStepVK.NextStepVK;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.ezhik.authTG.AuthTG.isVKEnabled;

public class BotVK {
    private final String token;
    private static final String API = "https://api.vk.com/method/";
    private static final String VERSION = "5.199";
    private final int GROUP_ID;

    public static final Map<String, VKCommandHandler> commandHandler = new ConcurrentHashMap<>();
    public static final Map<Integer, NextStepVK> nextStepHandler = new ConcurrentHashMap<>();
    public static final Map<String, CallbackQueryVK> callbackQueryHandler = new ConcurrentHashMap<>();

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

    public BotVK(String token, int groupId) {
        this.token = token;
        this.GROUP_ID = groupId;

        commandHandler.put("/start", new StartCMDHandler());
        commandHandler.put("/link", new StartCMDHandler());
        commandHandler.put("/tfon", new TFOnCMDHandler());
        commandHandler.put("/tfoff", new TFOffCMDHandler());
        commandHandler.put("/resetpassword", new ResetPasswordHandler());
        commandHandler.put("/kickme", new KickMeCMDHandler());
        commandHandler.put("/unlink", new UnLinkCMDHandler());

        callbackQueryHandler.put("ys", new YesCallbackQuery());
        callbackQueryHandler.put("no", new NoCallbackQuery());
    }

    public void initializationBot() {
        if (!isVKEnabled()) {
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
        if (!isVKEnabled() || shutdown.get()) {
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
    public void sendLoginAccept(int peerId, String message, UUID uuid) {
        String keyboardJson = "{"
                + "\"one_time\": false,"
                + "\"buttons\": [["
                + "{"
                + "\"action\": {"
                + "\"type\": \"text\","
                + "\"label\": \"" + AuthTG.getMessage("yesbtn", "VK") + "\","
                + "\"payload\": \"{\\\"answer\\\":\\\"ys\\\",\\\"uuid\\\":\\\"" + uuid + "\\\"}\""
                + "},"
                + "\"color\": \"positive\""
                + "},"
                + "{"
                + "\"action\": {"
                + "\"type\": \"text\","
                + "\"label\": \"" + AuthTG.getMessage("nobtn", "VK") + "\","
                + "\"payload\": \"{\\\"answer\\\":\\\"no\\\",\\\"uuid\\\":\\\"" + uuid + "\\\"}\""
                + "},"
                + "\"color\": \"negative\""
                + "}"
                + "]]"
                + "}";

        RequestBody body = new FormBody.Builder()
                .add("access_token", token)
                .add("v", VERSION)
                .add("peer_id", String.valueOf(peerId))
                .add("message", message)
                .add("random_id", String.valueOf(System.currentTimeMillis() / 1000 + new Random().nextInt(10000)))
                .add("keyboard", keyboardJson)
                .build();

        Request request = new Request.Builder()
                .url(API + "messages.send")
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
        if (!isVKEnabled() || shutdown.get()) {
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

    public void setNextStepHandler(Integer peerid, NextStepVK nextStepHandler) {
        if (!isVKEnabled()) return;
        if (peerid == null || nextStepHandler == null) return;
        this.nextStepHandler.put(peerid, nextStepHandler);
    }

    public void remNextStepHandler(Integer peerid) {
        if (!isVKEnabled()) return;
        if (peerid == null) return;
        this.nextStepHandler.remove(peerid);
    }
}
