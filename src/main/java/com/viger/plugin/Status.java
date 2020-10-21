package com.viger.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Status {
    public static final Status INSTANCE = new Status();
    String url_first = "https://www.bigfun.cn/api/feweb?target=gzlj-clan-day-report-collect%2Fa";

    private Status() {
        super();
    }


    public Message query(String Cookie) {
        MessageChainBuilder content = new MessageChainBuilder();
        String bossName = "";
        long total_life = 0, current_life = 0;
        int lap_num = 0, ranking;
        JsonObject response = (JsonObject) getStatus(Cookie);
        try {
            if (response.get("code").getAsInt() != 0) {
                throw new Exception("");
            } else {
                ranking = response.get("data").getAsJsonObject().get("clan_info").getAsJsonObject().get("last_ranking").getAsInt();
                bossName = response.get("data").getAsJsonObject().get("boss_info").getAsJsonObject().get("name").getAsString();
                total_life = response.get("data").getAsJsonObject().get("boss_info").getAsJsonObject().get("total_life").getAsLong();
                current_life = response.get("data").getAsJsonObject().get("boss_info").getAsJsonObject().get("current_life").getAsLong();
                lap_num = response.get("data").getAsJsonObject().get("boss_info").getAsJsonObject().get("lap_num").getAsInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        content.add("当前的状态为：\n");
        content.add(lap_num + "周目 " + bossName + "\n");
        content.add(current_life + "/" + total_life);
        return content.asMessageChain();
    }

    public JsonElement getStatus(String Cookie) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(this.url_first);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Custom-Source", "GitHub@var-mixer");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Cookie", Cookie.equals("") ? "sid=5hsmuqp9; DedeUserID=12931312; DedeUserID__ckMd5=c4c255f8f534bf24; SESSDATA=d7daff29%2C1616689534%2C0f1f9*91; bili_jct=a1f310c738dab5858c2e63f47e0855e4; session-api=6305edelp16eojkl09k0al4phq; _csrf=qtzT1ebnxEws8NZ7_Nvl4_gS" : Cookie);
            con.setRequestProperty("Referer", "https://www.bigfun.cn/tools/pcrteam/d_report");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonParser.parseString(content.toString());
    }
}
