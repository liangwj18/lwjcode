package com.java.huangjialiang.ui.notifications;

import android.os.Parcel;

import com.orm.SugarRecord;


public class IndicesInfo extends SugarRecord {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    float activity, diveristy, newStar, risingStar, sociability;
    int citations, gindex, hindex, pubs;

    public IndicesInfo(float activity, int citations, float diveristy, int gindex, int hindex,
                       float newStar, int pubs, float risingStar, float sociability) {
        this.activity = activity;
        this.citations = citations;
        this.diveristy = diveristy;
        this.gindex = gindex;
        this.hindex = hindex;
        this.newStar = newStar;
        this.pubs = pubs;
        this.risingStar = risingStar;
        this.sociability = sociability;
    }


    protected IndicesInfo(Parcel in) {

    }

    public IndicesInfo() {

    }

    public float getActivity() {
        return activity;
    }

    public void setActivity(float activity) {
        this.activity = activity;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public float getDiveristy() {
        return diveristy;
    }

    public void setDiveristy(float diveristy) {
        this.diveristy = diveristy;
    }

    public int getGindex() {
        return gindex;
    }

    public void setGindex(int gindex) {
        this.gindex = gindex;
    }

    public int getHindex() {
        return hindex;
    }

    public void setHindex(int hindex) {
        this.hindex = hindex;
    }

    public float getNewStar() {
        return newStar;
    }

    public void setNewStar(float newStar) {
        this.newStar = newStar;
    }

    public int getPubs() {
        return pubs;
    }

    public void setPubs(int pubs) {
        this.pubs = pubs;
    }

    public float getRisingStar() {
        return risingStar;
    }

    public void setRisingStar(float risingStar) {
        this.risingStar = risingStar;
    }

    public float getSociability() {
        return sociability;
    }

    public void setSociability(float sociability) {
        this.sociability = sociability;
    }

}

