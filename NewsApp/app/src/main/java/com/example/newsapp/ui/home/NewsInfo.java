package com.example.newsapp.ui.home;

import java.time.LocalDateTime;

public class NewsInfo {
    String id;
    String type;
    String title;
    String time;
    String lang;
    String origin;

    NewsInfo(String id, String type, String title, String time, String lang, String origin){
        this.id = id;
        this.type = type;
        this.title=title;
        this.lang = lang;
        this.origin = origin;
        this.time = time;
    }
}
