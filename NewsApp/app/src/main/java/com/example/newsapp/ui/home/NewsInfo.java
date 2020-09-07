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
