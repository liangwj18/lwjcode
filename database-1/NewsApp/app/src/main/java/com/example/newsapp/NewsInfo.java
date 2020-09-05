package com.example.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.sql.Time;


import java.time.LocalDateTime;



public class NewsInfo extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    String myID;
    String type;
    String title;
    String time;
    String lang;
    String origin;

    public NewsInfo(String id, String type, String title, String time, String lang, String origin){
        this.myID= id;
        this.type = type;
        this.title= title;
        this.lang = lang;
        this.origin = origin;
        this.time = time;
    }

    protected NewsInfo(Parcel in) {
       
    }

    public NewsInfo() {
    }

    public static final Creator<NewsInfo> CREATOR = new Creator<NewsInfo>() {
        @Override
        public NewsInfo createFromParcel(Parcel in) {
            return new NewsInfo(in);
        }

        @Override
        public NewsInfo[] newArray(int size) {
            return new NewsInfo[size];
        }
    };

    public String getid() {
        return myID;
    }

    public void setid(String id) {
        this.myID = id;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getorigin() {
        return origin;
    }

    public void setorigin(String origin) {
        this.origin = origin;
    }
    public String gettime() {
        return time;
    }

    public void settime(String time) {
        this.time = time;
    }

    public String getlang() {
        return lang;
    }

    public void setlang(String lang) {
        this.lang = lang;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String Title) {
        this.title = Title;
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
        return myID;
       // return getId()+"/"+getAuthor()+"/"+getTitle()+"/"+getNumber();
    }
}

