package com.viger.plugin;

import com.google.gson.JsonParser;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class Rank {
    public static final Rank INSTANCE = new Rank();
    String url_first = "https://service-kjcbcnmw-1254119946.gz.apigw.tencentcs.com/name/0";
    String headers = "{\"Custom-Source\":\"GitHub@var-mixer\",\"Content-Type\": \"application/json\",\"Referer\": \"https://kengxxiao.github.io/Kyouka/\"}";
    LinkedHashMap<String, String> clanName;

    private Rank() {
        super();
        clanName = new LinkedHashMap<>();
    }

    public void update() {
        if (!clanName.isEmpty()) {
            for (String clanname :
                    clanName.keySet()) {
                clanName.replace(clanname, getRank(clanname));
            }
        }
    }

    public void add(String toAdd) {
        clanName.put(toAdd, "");
        update();
    }

    public Message query(String toQuery) {
        MessageChainBuilder content = new MessageChainBuilder();
        content.add("公会" + toQuery + "当前的排名为: ");
        content.add(clanName.containsKey(toQuery) ? clanName.get(toQuery) : getRank(toQuery));
        content.add("\n");
        return content.asMessageChain();
    }

    public String getRank(String toGet) {
        String data = "{\"history\":\"0\",\"clanName\":\"" + toGet + "\"}";
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(this.url_first);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Custom-Source", "GitHub@var-mixer");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Referer", "https://kengxxiao.github.io/Kyouka/");
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            outStreamWriter.write(data);
            outStreamWriter.flush();
            outStreamWriter.close();

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
        return JsonParser.parseString(content.toString()).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("rank").getAsString();
    }
}
