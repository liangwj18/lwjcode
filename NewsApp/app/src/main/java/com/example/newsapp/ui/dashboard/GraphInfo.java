package com.example.newsapp.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.io.Serializable;

public class GraphInfo extends SugarRecord implements Serializable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    private static final long serialVersionUID = 2017010191;
    String img, label, info;
    String properties, relations;

    protected GraphInfo(Parcel in) {

    }

    public GraphInfo() {
    }

    public GraphInfo(String img, String label, String info, String properties, String relations) {
        this.img = img;
        this.label = label;
        this.info = info;
        this.properties = properties;
        this.relations = relations;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }
}

