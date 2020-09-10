package com.example.newsapp.ui.home;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class NewsInfo extends SugarRecord {
    String content;
    String time;
    long tflag;
    String title;
    String source;
    String originURL;
    @Unique
    String myId;
    String newsType;
    String type;        // 对应的分类

    public NewsInfo() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTflag() {
        return tflag;
    }

    public void setTflag(long tflag) {
        this.tflag = tflag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOriginURL() {
        return originURL;
    }

    public void setOriginURL(String originURL) {
        this.originURL = originURL;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NewsInfo(String id, String title, String time, String source, long tflag, String originURL, String content, String newsType, String type) {
        this.myId = id;
        this.title = title;
        this.time = time;
        this.source = source;
        this.tflag = tflag;
        this.originURL = originURL;
        this.content = content;
        this.newsType = newsType;
        this.type = type;
    }
}
