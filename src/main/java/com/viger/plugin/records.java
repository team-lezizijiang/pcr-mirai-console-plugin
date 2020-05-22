package com.viger.plugin;

import java.util.Date;

class records{
    private long damage;
    private boolean isFinal;
    private int date;

    public records(long damage, boolean isFinal, int date){
        this.damage = damage;
        this.date = date;
        this.isFinal = isFinal;
    }

    public int getDate() {
        return date;
    }


    public boolean isFinal() {
        return isFinal;
    }


    public long getDamage() {
        return damage;
    }
}