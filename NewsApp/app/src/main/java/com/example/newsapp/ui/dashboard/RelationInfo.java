package com.example.newsapp.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;


public class RelationInfo{

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    private String label, url, relation;
    private boolean forward;

    public RelationInfo(String label, String url, String relation, boolean forward) {
        this.label = label;
        this.url = url;
        this.relation = relation;
        this.forward = forward;
    }

    public RelationInfo() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }
}

