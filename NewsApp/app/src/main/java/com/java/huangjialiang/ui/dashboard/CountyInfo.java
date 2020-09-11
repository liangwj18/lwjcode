package com.java.huangjialiang.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class CountyInfo extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    String Country,Province,County;
    String Begin_Time;

    //List  <County_Day_Info> day_info;
    String day_info;

    public CountyInfo(String Country, String Province, String County, String Begin_Time,  String day_info){
        this.Country= Country;
        this.Province = Province;
        this.County= County;
        this.Begin_Time = Begin_Time;
        this.day_info =day_info;
    }

    protected CountyInfo(Parcel in) {

    }

    public CountyInfo() {
    }

    public static final Creator<CountyInfo> CREATOR = new Creator<CountyInfo>() {
        @Override
        public CountyInfo createFromParcel(Parcel in) {
            return new CountyInfo(in);
        }

        @Override
        public CountyInfo[] newArray(int size) {
            return new CountyInfo[size];
        }
    };

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCounty() {
        return County;
    }

    public void setCounty(String county) {
        County = county;
    }

    public String getBegin_Time() {
        return Begin_Time;
    }

    public void setBegin_Time(String begin_Time) {
        Begin_Time = begin_Time;
    }


    public String getDay_info() {
        return day_info;
    }

    public void setDay_info(String day_info) {
        this.day_info = day_info;
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
       // return "i am countyinfo";
        return getCountry()+"|"+getProvince()+"|"+getCounty();
    }
}

