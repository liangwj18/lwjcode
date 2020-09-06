package com.example.newsapp.ui.home;

import java.time.LocalDateTime;

public class NewsInfo {
    String content;
    String time;
    String tflag;
    String title;
    String source;
    String originURL;
    String myId;
    String newsType;

    NewsInfo(String id, String title, String time, String source, String tflag, String originURL, String content, String type) {
        this.myId = id;
        this.title = title;
        this.time = time;
        this.source = source;
        this.tflag = tflag;
        this.originURL = originURL;
        this.content = content;
        this.newsType = type;
    }
}
