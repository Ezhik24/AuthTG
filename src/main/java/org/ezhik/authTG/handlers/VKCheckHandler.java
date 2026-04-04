package org.ezhik.authTG.handlers;

import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.scheduler.BukkitRunnable;
import org.ezhik.authTG.AuthTG;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class VKCheckHandler extends BukkitRunnable {
    private final String server;
    private String ts;
    private final String key;

    public VKCheckHandler(String server, String ts, String key) {
        this.server = server;
        this.ts = ts;
        this.key = key;
    }

    @Override
    public void run() {
        String url = server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25";
        Request request = new Request.Builder().url(url).build();
        AuthTG.vk.checkEvent(request).thenAccept(responseLp -> {

            JSONObject json = null;
            try {
                json = new JSONObject(responseLp.body().string());
            } catch (IOException e) {
                AuthTG.logger.severe("[AuthTG] Exception " + e);
            }
            ts = json.getString("ts");
            JSONArray updates = json.getJSONArray("updates");

            for (int i = 0; i < updates.length(); i++) {
                JSONObject event = updates.getJSONObject(i);
                String type = event.getString("type");
                if (type.equals("message_new")) {
                    JSONObject object = event.getJSONObject("object");
                    JSONObject jsonObject = object.getJSONObject("message");
                    int userId = jsonObject.getInt("peer_id");
                    String text = jsonObject.getString("text");


                    AuthTG.vk.sendMessage(userId, text);
                    break;
                }
            }
        });
    }
}
