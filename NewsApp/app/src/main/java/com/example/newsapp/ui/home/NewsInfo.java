package com.example.newsapp.ui.home;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;


public class NewsInfo extends SugarRecord {
    private String content;
    private String time;
    private long tflag;
    private String title;
    private String source;
    private String originURL;
    @Unique
    private String myId;
    private String newsType;    // json中的"type"
    private String type;        // 对应的分类,all news paper

    public NewsInfo() {

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

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public long getTflag() {
        return tflag;
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getOriginURL() {
        return originURL;
    }

    public String getMyId() {
        return myId;
    }

    public String getNewsType() {
        return newsType;
    }

    public String getType() {
        return type;
    }
}
