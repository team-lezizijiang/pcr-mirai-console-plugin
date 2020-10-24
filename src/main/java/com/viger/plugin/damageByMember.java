package com.viger.plugin;

import java.util.List;

public class damageByMember {
    String name;
    int number;
    long damage;
    long score;
    List<record> damage_list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getDamage() {
        return damage;
    }

    public void setDamage(long damage) {
        this.damage = damage;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public List<record> getDamage_list() {
        return damage_list;
    }

    public void setDamage_list(List<record> damage_list) {
        this.damage_list = damage_list;
    }

}
