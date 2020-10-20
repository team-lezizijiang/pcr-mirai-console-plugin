package com.viger.plugin;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import static com.viger.plugin.pcrMain.*;


public class Gashapon {

    private String data;

    public Gashapon(int num, boolean isUp) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int on = 0, tw = 0, thre = 0; // 抽出来的三星二星有几个

        //无保底
        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        //有保底
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else {
                tw++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();

        for (int i = 0; i < thre; i++) {
            int q = random.nextInt(25);
            if (q < 7 && isUp && three_plus.size() > 0) {//抽不抽的出来UP
                map1.merge(three_plus.get(random.nextInt(three_plus.size())), 1, Integer::sum);
            } else if (!isUp) {
                map1.merge(three.get(random.nextInt(three.size())), 1, Integer::sum);
            } else {
                map1.merge(noUpThree.get(random.nextInt(noUpThree.size())), 1, Integer::sum);
            }
        }
        for (int i = 0; i < tw; i++) {
            int q = random.nextInt(16);
            if (q < 3 && isUp && two_plus.size() > 0) {//抽不抽的出来UP
                map2.merge(two_plus.get(random.nextInt(two_plus.size())), 1, Integer::sum);
            } else if (!isUp) {
                map2.merge(two.get(random.nextInt(two.size())), 1, Integer::sum);
            } else {
                map2.merge(noUpTwo.get(random.nextInt(noUpTwo.size())), 1, Integer::sum);
            }
        }
        for (int i = 0; i < on; i++) {
            int q = random.nextInt(795);
            if (q < 160 && isUp && one_plus.size() > 0) {//抽不抽的出来UP
                map3.merge(one_plus.get(random.nextInt(one_plus.size())), 1, Integer::sum);
            } else if (!isUp) {
                map3.merge(one.get(random.nextInt(one.size())), 1, Integer::sum);
            } else {
                map3.merge(noUpOne.get(random.nextInt(noUpOne.size())), 1, Integer::sum);
            }
        }
        this.setData(on, tw, thre, map1, map2, map3);
    }

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

    private void setData(int on, int tw, int thre, HashMap<String, Integer> map1, HashMap<String, Integer> map2, HashMap<String, Integer> map3) {
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
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        if (thre != 0) {
            stringBuilder.append("\n三星角色有：");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n二星角色有：");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\n一星角色有：");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
        }

        this.data = stringBuilder.toString();
    }

    public String getData() {
        return data;
    }
}
// todo: 卡池自动更新