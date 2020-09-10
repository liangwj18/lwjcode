package com.example.newsapp.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;


public class relationInfo extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    String relation_label,url,relation_name;
    boolean forward;

    protected relationInfo(Parcel in) {

    }

    public relationInfo(String relation_label, String url, String relation_name, boolean forward) {
        this.relation_label = relation_label;
        this.url = url;
        this.relation_name = relation_name;
        this.forward = forward;
    }

    public String getRelation_label() {
        return relation_label;
    }

    public void setRelation_label(String relation_label) {
        this.relation_label = relation_label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRelation_name() {
        return relation_name;
    }

    public void setRelation_name(String relation_name) {
        this.relation_name = relation_name;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }


    public relationInfo() {

    }

    public static final Creator<relationInfo> CREATOR = new Creator<relationInfo>() {
        @Override
        public relationInfo createFromParcel(Parcel in) {
            return new relationInfo(in);
        }

        @Override
        public relationInfo[] newArray(int size) {
            return new relationInfo[size];
        }
    };

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
        return "i am relationInfo";
       // return getId()+"/"+getAuthor()+"/"+getTitle()+"/"+getNumber();
    }
}

