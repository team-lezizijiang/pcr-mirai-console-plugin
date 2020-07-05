package com.viger.plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.message.data.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class NewsFeeder {
    private final String timestamp; // 最后一篇已读文章的时间戳
    private final String url = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?host_uid=353840826";
    private JsonObject last; // 上一次更新内容


    public NewsFeeder() {
        last = null;
        timestamp = "";
    }


    /**
     * 检查是否有未读文章
     *
     * @return 是否有未读文章
     */
    boolean unread() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-agent", "Mozilla/4.0");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        JsonObject response = JsonParser.parseString(content.toString()).getAsJsonObject();
        if (last.get("cards").getAsJsonArray().get(0).getAsJsonObject().get("card").getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong()
                == response.get("cards").getAsJsonArray().get(0).getAsJsonObject().get("card").getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong()) {
            return false;
        } else {
            last = response;
            return true;
        }
    }

    /**
     * 获取最新的文章们,并更新last
     *
     * @return 文章消息化
     */
    LinkedList<Message> fetch() {
        JsonArray feeds = last.get("cards").getAsJsonArray();
        LinkedList<Message> result = new LinkedList<>();
        for (JsonElement feed : feeds
        ) {
            if (feed.getAsJsonObject().get("desc").getAsJsonObject().get("timestamp").getAsString().equals(timestamp)) {
                break;
            } else {
                result.add(convert(feed.getAsJsonObject().get("card").getAsJsonObject()));
            }
        }
        return result;
    }

    /**
     * @param feed json格式的文章
     * @return message格式的文章
     */
    Message convert(JsonObject feed) {

    }
}
