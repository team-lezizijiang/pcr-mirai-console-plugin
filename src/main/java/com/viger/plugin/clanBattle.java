package com.viger.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClanBattle {

    public static final ClanBattle INSTANCE = new ClanBattle();
    public final String cookie = pcrMain.INSTANCE.cookie;
    String url_first = "https://www.bigfun.cn/api/feweb?target=gzlj-clan-day-report-collect%2Fa";
    List<damageByMember> records;
    int lap_num;
    String boss_name;
    Long current_life;
    Long total_life;
    int ranking;
    long timestamp;

    private ClanBattle() {
        super();
        update();
    }

    public List<damageByMember> getRecords() {
        return records;
    }

    public void setRecords(List<damageByMember> records) {
        this.records = records;
    }

    public int getLap_num() {
        return lap_num;
    }

    public void setLap_num(int lap_num) {
        this.lap_num = lap_num;
    }

    public String getBoss_name() {
        return boss_name;
    }

    public void setBoss_name(String boss_name) {
        this.boss_name = boss_name;
    }

    public Long getCurrent_life() {
        return current_life;
    }

    public void setCurrent_life(Long current_life) {
        this.current_life = current_life;
    }

    public Long getTotal_life() {
        return total_life;
    }

    public void setTotal_life(Long total_life) {
        this.total_life = total_life;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void update() {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL("https://www.bigfun.cn/api/feweb?target=gzlj-clan-day-timeline-report%2Fa&page=1&size=30");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Cookie", cookie);
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
        long timestamp;
        for (JsonElement js : JsonParser.parseString(
                content.toString()).getAsJsonObject()
                .get("data").getAsJsonObject()
                .get("list").getAsJsonArray()) {
            if ((timestamp = js.getAsJsonObject().get("datetime").getAsLong()) > INSTANCE.getTimestamp()) {
                INSTANCE.setTimestamp(timestamp);
                pcrMain.group.sendMessage(new Gson().fromJson(js, record.class).toString());
                getStatus();
                getRecords("");
            }
        }


    }


    public Message query() {
        update();
        MessageChainBuilder content = new MessageChainBuilder();

        content.add("当前的状态为：\n");
        content.add(INSTANCE.getLap_num() + "周目 " + INSTANCE.getBoss_name() + "\n");
        content.add(INSTANCE.getCurrent_life() + "/" + INSTANCE.getTotal_life() + "\n");
        content.add("排名: " + INSTANCE.getRanking());
        return content.asMessageChain();
    }

    public void getStatus() {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(this.url_first);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Cookie", cookie);
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
        String bossName = "";
        long total_life = 0, current_life = 0;
        int lap_num = 0, ranking = 0;
        JsonObject response = (JsonObject) JsonParser.parseString(content.toString());
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

        INSTANCE.setBoss_name(bossName);
        INSTANCE.setCurrent_life(current_life);
        INSTANCE.setLap_num(lap_num);
        INSTANCE.setRanking(ranking);
        INSTANCE.setTotal_life(total_life);
    }


    public void getRecords(String cookies) {
        StringBuilder content = new StringBuilder();
        List<damageByMember> result = new ArrayList<>();

        try {
            URL url = new URL("https://www.bigfun.cn/api/feweb?target=gzlj-clan-day-report%2Fa&date=2020-10-23&size=30");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Cookie", cookies.equals("") ? "sid=5hsmuqp9; DedeUserID=12931312; DedeUserID__ckMd5=c4c255f8f534bf24; SESSDATA=d7daff29%2C1616689534%2C0f1f9*91; bili_jct=a1f310c738dab5858c2e63f47e0855e4; session-api=6305edelp16eojkl09k0al4phq; _csrf=qtzT1ebnxEws8NZ7_Nvl4_gS" : cookies);
            con.setRequestProperty("Referer", "https://www.bigfun.cn/tools/pcrteam/d_report");
            con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7");
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
        damageByMember r;
        for (JsonElement js : JsonParser.parseString(
                content.toString()).getAsJsonObject()
                .get("data").getAsJsonArray()) {
            r = new Gson().fromJson(js, damageByMember.class);
            result.add(r);
        }

        INSTANCE.setRecords(result);
    }
}

class record implements Serializable {
    Long datetime;
    String name;
    String boss_name;
    int lap_num;
    long damage;
    int kill;
    int reimburse;
    long score;

    public Timestamp getDatetime() {
        return new Timestamp(datetime * 1000);
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime.getTime() / 1000;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoss_name() {
        return boss_name;
    }

    public void setBoss_name(String boss_name) {
        this.boss_name = boss_name;
    }

    public int getLap_num() {
        return lap_num;
    }

    public void setLap_num(int lap_num) {
        this.lap_num = lap_num;
    }

    public long getDamage() {
        return damage;
    }

    public void setDamage(long damage) {
        this.damage = damage;
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public int getReimburse() {
        return reimburse;
    }

    public void setReimburse(int reimburse) {
        this.reimburse = reimburse;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return name + "在" + new Timestamp(datetime * 1000).toLocaleString() + "对" + boss_name + "造成了" + damage + "伤害" + " " + (kill == 1 ? "尾刀" : "") + (reimburse == 1 ? "补偿刀" : "");
    }
}

