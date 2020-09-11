package com.example.newsapp.ui.notifications;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Unique;


public class PersonInfo extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    @Unique String myID;


    String Avatar,name,name_zh;
    boolean bind;
    Indices_Info indices;
    int num_followed,num_viewed;



    Profile_Info profile_info;
    int score;
    String sourcetype;
    String tags,tags_score;
    int myindex,tab;
    @Column(name="isPassedaway")
    String isPassedaway;

    public PersonInfo(String avatar,  boolean bind,String myID, Indices_Info indices,String name, String name_zh,  int num_followed, int num_viewed, Profile_Info profile_info, int score, String sourcetype, String tags, String tags_score, int myindex, int tab, String isPassedaway) {
        Avatar = avatar;
        this.myID = myID;
        this.name = name;
        this.name_zh = name_zh;
        this.bind = bind;
        this.indices = indices;
        this.num_followed = num_followed;
        this.num_viewed = num_viewed;
        this.profile_info = profile_info;
        this.score = score;
        this.sourcetype = sourcetype;
        this.tags = tags;
        this.tags_score = tags_score;
        this.myindex = myindex;
        this.tab = tab;
        this.isPassedaway = isPassedaway;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_zh() {
        return name_zh;
    }

    public void setName_zh(String name_zh) {
        this.name_zh = name_zh;
    }

    public String getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public int getMyindex() {
        return myindex;
    }

    public void setMyindex(int myindex) {
        this.myindex = myindex;
    }

    public String getIsPassedaway() {
        return isPassedaway;
    }

    public void setIsPassedaway(String isPassedaway) {
        this.isPassedaway = isPassedaway;
    }

    public Indices_Info getIndices() {
        return indices;
    }

    public void setIndices(Indices_Info indices) {
        this.indices = indices;
    }

    public int getNum_followed() {
        return num_followed;
    }

    public void setNum_followed(int num_followed) {
        this.num_followed = num_followed;
    }

    public int getNum_viewed() {
        return num_viewed;
    }

    public void setNum_viewed(int num_viewed) {
        this.num_viewed = num_viewed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMyIndex() {
        return myindex;
    }

    public void setMyIndex(int index) {
        this.myindex = index;
    }

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags_score() {
        return tags_score;
    }

    public void setTags_score(String tags_score) {
        this.tags_score = tags_score;
    }

    public Profile_Info getProfile_info() {
        return profile_info;
    }

    public void setProfile_info(Profile_Info profile_info) {
        this.profile_info = profile_info;
    }

    protected PersonInfo(Parcel in) {

    }

    public PersonInfo() {
    }

    public static final Creator<PersonInfo> CREATOR = new Creator<PersonInfo>() {
        @Override
        public PersonInfo createFromParcel(Parcel in) {
            return new PersonInfo(in);
        }

        @Override
        public PersonInfo[] newArray(int size) {
            return new PersonInfo[size];
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
        return "i am Personinfo";
       // return getId()+"/"+getAuthor()+"/"+getTitle()+"/"+getNumber();
    }
}

