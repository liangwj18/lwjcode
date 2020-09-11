package com.example.newsapp.ui.notifications;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;


public class Indices_Info extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    int activity,citations,diveristy,gindex,hindex,newStar,pubs,risingStar,sociability;

    public Indices_Info(int activity, int citations, int diveristy, int gindex, int hindex, int newStar, int pubs, int risingStar, int sociability) {
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


    protected Indices_Info(Parcel in) {

    }

    public Indices_Info() {

    }

    public static final Creator<Indices_Info> CREATOR = new Creator<Indices_Info>() {
        @Override
        public Indices_Info createFromParcel(Parcel in) {
            return new Indices_Info(in);
        }

        @Override
        public Indices_Info[] newArray(int size) {
            return new Indices_Info[size];
        }
    };

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public int getDiveristy() {
        return diveristy;
    }

    public void setDiveristy(int diveristy) {
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

    public int getNewStar() {
        return newStar;
    }

    public void setNewStar(int newStar) {
        this.newStar = newStar;
    }

    public int getPubs() {
        return pubs;
    }

    public void setPubs(int pubs) {
        this.pubs = pubs;
    }

    public int getRisingStar() {
        return risingStar;
    }

    public void setRisingStar(int risingStar) {
        this.risingStar = risingStar;
    }

    public int getSociability() {
        return sociability;
    }

    public void setSociability(int sociability) {
        this.sociability = sociability;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       // dest.writeString(Title);
        //dest.writeString(number);
        //dest.writeString(author);
    }

    @Override
    public String toString() {
        return "i am Indices_Info";
       // return getId()+"/"+getAuthor()+"/"+getTitle()+"/"+getNumber();
    }
}

