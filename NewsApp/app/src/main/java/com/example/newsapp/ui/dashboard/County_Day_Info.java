package com.example.newsapp.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;


public class County_Day_Info extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    int Confirmed,Cured,Dead,Suspected;
    int Severe,Risk,inc24;

    public County_Day_Info(int Confirmed,   int Suspected,int Cured,int Dead,
                           int Severe, int Risk, int inc24){
        this.Confirmed= Confirmed;
        this.Cured = Cured;
        this.Dead= Dead;
        this.Suspected = Suspected;
        this.Severe = Severe;
        this.Risk = Risk;
        this.inc24= inc24;
    }

    protected County_Day_Info(Parcel in) {

    }

    public County_Day_Info() {
        inc24=0;
        Risk=0;
        Severe=0;
    }

    public static final Creator<County_Day_Info> CREATOR = new Creator<County_Day_Info>() {
        @Override
        public County_Day_Info createFromParcel(Parcel in) {
            return new County_Day_Info(in);
        }

        @Override
        public County_Day_Info[] newArray(int size) {
            return new County_Day_Info[size];
        }
    };

    public int getConfirmed() {
        return Confirmed;
    }

    public void setConfirmed(int confirmed) {
        Confirmed = confirmed;
    }

    public int getCured() {
        return Cured;
    }

    public void setCured(int cured) {
        Cured = cured;
    }

    public int getDead() {
        return Dead;
    }

    public void setDead(int dead) {
        Dead = dead;
    }

    public int getSuspected() {
        return Suspected;
    }

    public void setSuspected(int suspected) {
        Suspected = suspected;
    }

    public int getSevere() {
        return Severe;
    }

    public void setSevere(int severe) {
        Severe = severe;
    }

    public int getRisk() {
        return Risk;
    }

    public void setRisk(int risk) {
        Risk = risk;
    }

    public int getInc24() {
        return inc24;
    }

    public void setInc24(int inc24) {
        this.inc24 = inc24;
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
        return getConfirmed()+" "+getCured()+" "+getDead();
       // return getId()+"/"+getAuthor()+"/"+getTitle()+"/"+getNumber();
    }
}

