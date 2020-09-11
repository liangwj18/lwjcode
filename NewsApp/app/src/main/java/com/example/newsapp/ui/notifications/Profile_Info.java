package com.example.newsapp.ui.notifications;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;


public class Profile_Info extends SugarRecord implements Parcelable {

    /*
    @Column这个注解意思是说你想强制按照你的规定的名字来创建表中对应的字段名字，所以这里的skuId在Goods表中的字段名就不是默认的sku_id了，而是你自己给的sku_ID
    @Ignore这个注解强调这个属性在表中不要创建对应的字段

     */
    String address,affiliation,affiliation_zh,bio,edu,email,email_cr;
    String emails_u,fax,homepage,note,phone,position,work;

    public Profile_Info(String address, String affiliation, String affiliation_zh, String bio, String edu, String email, String email_cr, String emails_u, String fax, String homepage, String note, String phone, String position, String work) {
        this.address = address;
        this.affiliation = affiliation;
        this.affiliation_zh = affiliation_zh;
        this.bio = bio;
        this.edu = edu;
        this.email = email;
        this.email_cr = email_cr;
        this.emails_u = emails_u;
        this.fax = fax;
        this.homepage = homepage;
        this.note = note;
        this.phone = phone;
        this.position = position;
        this.work = work;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getAffiliation_zh() {
        return affiliation_zh;
    }

    public void setAffiliation_zh(String affiliation_zh) {
        this.affiliation_zh = affiliation_zh;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEdu() {
        return edu;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail_cr() {
        return email_cr;
    }

    public void setEmail_cr(String email_cr) {
        this.email_cr = email_cr;
    }

    public String getEmails_u() {
        return emails_u;
    }

    public void setEmails_u(String emails_u) {
        this.emails_u = emails_u;
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
    protected Profile_Info(Parcel in) {
    }

    public Profile_Info() {

    }

    public static final Creator<Profile_Info> CREATOR = new Creator<Profile_Info>() {
        @Override
        public Profile_Info createFromParcel(Parcel in) {
            return new Profile_Info(in);
        }

        @Override
        public Profile_Info[] newArray(int size) {
            return new Profile_Info[size];
        }
    };

    @Override
    public String toString() {
        return "i am Profile_Info";
       // return getId()+"/"+getAuthor()+"/"+getTitle()+"/"+getNumber();
    }
}

