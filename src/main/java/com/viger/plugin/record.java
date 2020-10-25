package com.viger.plugin;

import java.io.Serializable;
import java.sql.Timestamp;

public class record implements Serializable {
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
        return (name != null ? name + "在" : "") + new Timestamp(datetime * 1000).toLocaleString() + "对" + boss_name + "造成了" + damage + "伤害" + " " + (kill == 1 ? "尾刀" : "") + (reimburse == 1 ? "补偿刀" : "");
    }
}
