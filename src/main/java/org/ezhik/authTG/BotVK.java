package org.ezhik.authTG;

import okhttp3.*;
import org.bukkit.Bukkit;
import org.ezhik.authTG.handlers.VKCheckHandler;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.concurrent.*;

public class BotVK {
    private final String TOKEN;
    private final String API = "https://api.vk.com/method/";
    private final String VERSION = "5.199";

    private final OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();

    private final ExecutorService vkIoExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "AuthTG-vkIO");
        thread.setDaemon(true);
        return thread;
    });

    public BotVK(String token) {
        this.TOKEN = token;
    }
    public void initializationBot() {
        if (!AuthTG.isVKEnabled()) {
            AuthTG.logger.warning("[AuthTG] VK integration is disabled in config.yml (vk.enabled: false)");
        }

        if ("changeme".equals(TOKEN)) {
            AuthTG.logger.warning("[AuthTG] Please, set token in config.yml (VK)");
            return;
        }

        String lp = getLongPollServer();

        JSONObject obj = new JSONObject(lp);
        JSONObject response = obj.getJSONObject("response");

        String server = response.getString("server");
        String key = response.getString("key");
        String ts = response.getString("ts");
        AuthTG.logger.info("VKBot started!");

        VKCheckHandler handler = new VKCheckHandler(server,ts,key);
        handler.runTaskTimer(AuthTG.getInstance(), 0,20);
    }

    private String getLongPollServer () {
        try {
            String url = API + "groups.getLongPollServer"
                    + "?group_id=217145799"
                    + "&access_token=" + TOKEN
                    + "&v=" + VERSION;
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(int peerID, String message) {
        if (!AuthTG.isVKEnabled()) return;
        if (peerID <= 0) return;
        if (message == null) message = "";

        String url = API + "messages.send";
        RequestBody body = new FormBody.Builder()
                .add("user_id", String.valueOf(peerID))
                .add("message", message)
                .add("random_id", String.valueOf(System.currentTimeMillis()))
                .add("access_token", TOKEN)
                .add("v", VERSION)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        executeAsync(request);
    }

    public void executeAsync(Request request) {
        if (!AuthTG.isVKEnabled()) return;
        if (request == null) return;
        vkIoExecutor.execute(() -> executeSendMessage(request));
    }
    private void executeSendMessage(Request request) {
        if (!AuthTG.isVKEnabled()) return;
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Response> checkEvent(Request request) {
        if (!AuthTG.isVKEnabled()) return null;
        return executeCheck(request);
    }

    public CompletableFuture<Response> executeCheck(Request request) {
        if (!AuthTG.isVKEnabled()) return null;
        if (request == null) return null;
        return CompletableFuture.supplyAsync(() -> checkEventNow(request));
    }

    private Response checkEventNow(Request request) {
        if (!AuthTG.isVKEnabled()) return null;
        try {
             return client.newCall(request).execute();
        } catch (Exception e) {
            AuthTG.logger.severe("[AuthTG] Exception: " + e);
        }
        return null;
    }


}
