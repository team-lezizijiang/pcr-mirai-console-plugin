package com.viger.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.viger.plugin.pcrMain.*;


public class Gashapon {

    public static final Gashapon INSTANCE = new Gashapon();
    public String loc = "BL"; //指定服务器。 JP BL TW ALL 可选
    private String version;

    /**
     * 刷新冷却时间
     *
     * @param QQ :qq
     */
    public static void refreshCoolDown(long QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTime1 = localDateTime.plusSeconds(200);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = localDateTime1.format(dateTimeFormatter);
        if (coolDown == null) {
            coolDown = new HashMap<>();
        }
        coolDown.put(String.valueOf(QQ), time);
    }

    /**
     * 获取冷却时间是不是到了
     */
    public static boolean isCool(long QQ) {
        if (coolDown == null) {
            coolDown = new HashMap<>();
            return true;
        } else {
            if (coolDown.get(String.valueOf(QQ)) != null) {
                return coolDown.get(String.valueOf(QQ)).compareTo(new SimpleDateFormat("HH:mm").format(new Date())) < 0;
            } else {
                return true;
            }
        }
    }

    public String gacha(int num) {
        String data;
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int on = 0, tw = 0, thre = 0; // 抽出来的三星二星有几个

        //无保底
        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - s3_prob) {
                thre++;
            } else if (j > 1000 - s3_prob - s2_prob) {
                tw++;
            } else {
                on++;
            }
        }
        //有保底
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - s3_prob) {
                thre++;
            } else {
                tw++;
            }
        }
        HashMap<Integer, Integer> map1 = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();
        HashMap<Integer, Integer> map3 = new HashMap<>();

        for (int i = 0; i < thre; i++) {
            int q = random.nextInt(s3_prob);
            if (q < up_prob && up.size() > 0) {//抽不抽的出来UP
                map1.merge(up.get(random.nextInt(up.size())), 1, Integer::sum);
            } else {
                map1.merge(star3.get(random.nextInt(star3.size())), 1, Integer::sum);
            }
        }
        for (int i = 0; i < tw; i++) {
            map2.merge(star2.get(random.nextInt(star2.size())), 1, Integer::sum);
        }
        for (int i = 0; i < on; i++) {
            map3.merge(star1.get(random.nextInt(star1.size())), 1, Integer::sum);
        }
        data = setData(on, tw, thre, map1, map2, map3);
        return data;
    }

    private String setData(int on, int tw, int thre, HashMap<Integer, Integer> map1, HashMap<Integer, Integer> map2, HashMap<Integer, Integer> map3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("一共抽出了");
        if (thre != 0) {
            stringBuilder.append(thre).append("个三星;");
        }
        if (tw != 0) {
            stringBuilder.append(tw).append("个二星;");
        }
        if (on != 0) {
            stringBuilder.append(on).append("个一星");
        }
        Set<Integer> set1 = map1.keySet();
        Set<Integer> set2 = map2.keySet();
        Set<Integer> set3 = map3.keySet();
        if (thre != 0) {
            stringBuilder.append("\n三星角色有：");
            for (Integer s : set1) {
                stringBuilder.append(chara.get(s).get(1)).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n二星角色有：");
            for (Integer s : set2) {
                stringBuilder.append(chara.get(s).get(1)).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\n一星角色有：");
            for (Integer s : set3) {
                stringBuilder.append(chara.get(s).get(1)).append("*").append(map3.get(s)).append(",");
            }
        }

        return stringBuilder.toString();
    }


    /**
     * 检查卡池版本号并唤起数据更新
     */
    public void update() {
        if (getGachaVer().equals(version)) {
        } else {
            version = getGachaVer();
            getGachaData();
            getChara();
            pcrMain.INSTANCE.getLogger().info("new gacha detected, updating");
        }
    }

    /**
     * 获取新的角色名称列表
     */
    private void getChara() {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL("https://api.redive.lolikon.icu/gacha/unitdata.py");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

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

        chara.clear();
        String re = content.toString();
        Pattern pattern = Pattern.compile("(\\d*):\\[(.*?)],", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(re);
        List<String> nicks;
        while (matcher.find()) {
            nicks = Arrays.asList(matcher.group(2).split(","));
            for (int i = 0; i < nicks.size(); i++) {
                nicks.set(i, nicks.get(i).replace("'", ""));
            }
            chara.put(Integer.valueOf(matcher.group(1)), nicks);
        }
        System.out.println(chara);
    }

    /**
     * 更新指定卡池的信息
     *
     * @param
     */
    private void getGachaData() {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL("https://api.redive.lolikon.icu/gacha/default_gacha.json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

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

        JsonObject pool = JsonParser.parseString(content.toString()).getAsJsonObject().get(loc).getAsJsonObject();
        up_prob = pool.get("up_prob").getAsInt();
        s3_prob = pool.get("s3_prob").getAsInt();
        s2_prob = pool.get("s2_prob").getAsInt();
        up.clear();
        for (JsonElement i : pool.get("up").getAsJsonArray()) {
            up.add(i.getAsInt());
        }
        star3.clear();
        for (JsonElement i : pool.get("star3").getAsJsonArray()) {
            star3.add(i.getAsInt());
        }
        star2.clear();
        for (JsonElement i : pool.get("star2").getAsJsonArray()) {
            star2.add(i.getAsInt());
        }
        star1.clear();
        for (JsonElement i : pool.get("star1").getAsJsonArray()) {
            star1.add(i.getAsInt());
        }
    }

    /**
     * 获取最新的卡池版本号
     *
     * @return 最新的卡池版本号
     */
    private String getGachaVer() {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL("https://api.redive.lolikon.icu/gacha/gacha_ver.json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

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
        return JsonParser.parseString(content.toString()).getAsJsonObject().get("ver").getAsString();
    }
}
// todo: 图片拼接