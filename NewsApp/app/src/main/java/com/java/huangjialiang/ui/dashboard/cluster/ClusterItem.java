package com.java.huangjialiang.ui.dashboard.cluster;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;


public class ClusterItem extends SugarRecord {
    private String content;
    private String time;
    private long tflag;
    private String title;
    private String source;
    private String originURL;
    @Unique
    private String myId;
    private String newsType;    // json中的"type"
    private String type;        // 对应的聚类分类

    public ClusterItem() {

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

    public ClusterItem(String id, String title, String time, String source, long tflag, String originURL, String content, String newsType, String type) {
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
