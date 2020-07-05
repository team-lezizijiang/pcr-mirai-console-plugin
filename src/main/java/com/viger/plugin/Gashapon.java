package com.viger.plugin;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import static com.viger.plugin.pcrMain.*;


class Gashapon {
    private String data;

    Gashapon(int num, boolean isUp) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int on = 0, tw = 0, thre = 0; // ����������Ƕ����м���

        //�ޱ���
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
        //�б���
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
            if (q < 7 && isUp) {//�鲻��ĳ���UP
                map1.merge(three_plus[random.nextInt(three_plus.length)], 1, Integer::sum);
            } else if (!isUp) {
                map1.merge(three[random.nextInt(three.length)], 1, Integer::sum);
            } else {
                map1.merge(noUpThree[random.nextInt(noUpThree.length)], 1, Integer::sum);
            }
        }
        for (int i = 0; i < tw; i++) {
            int q = random.nextInt(16);
            if (q < 3 && isUp) {//�鲻��ĳ���UP
                map2.merge(two_plus[random.nextInt(two_plus.length)], 1, Integer::sum);
            } else if (!isUp) {
                map2.merge(two[random.nextInt(two.length)], 1, Integer::sum);
            } else {
                map2.merge(noUpTwo[random.nextInt(noUpTwo.length)], 1, Integer::sum);
            }
        }
        for (int i = 0; i < on; i++) {
            int q = random.nextInt(795);
            if (q < 160 && isUp) {//�鲻��ĳ���UP
                map3.merge(one_plus[random.nextInt(one_plus.length)], 1, Integer::sum);
            } else if (!isUp) {
                map3.merge(one[random.nextInt(one.length)], 1, Integer::sum);
            } else {
                map3.merge(noUpOne[random.nextInt(noUpOne.length)], 1, Integer::sum);
            }
        }
        this.setData(on, tw, thre, map1, map2, map3);
    }

    /**
     * ˢ����ȴʱ��
     *
     * @param QQ :qq
     */
    static void refreshCoolDown(long QQ) {
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
     * ��ȡ��ȴʱ���ǲ��ǵ���
     */
    static boolean isCool(long QQ) {
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
        stringBuilder.append("һ�������");
        if (thre != 0) {
            stringBuilder.append(thre).append("������;");
        }
        if (tw != 0) {
            stringBuilder.append(tw).append("������;");
        }
        if (on != 0) {
            stringBuilder.append(on).append("��һ��");
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        if (thre != 0) {
            stringBuilder.append("\n���ǽ�ɫ�У�");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n���ǽ�ɫ�У�");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\nһ�ǽ�ɫ�У�");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
        }

        this.data = stringBuilder.toString();
    }

    String getData() {
        return data;
    }
}