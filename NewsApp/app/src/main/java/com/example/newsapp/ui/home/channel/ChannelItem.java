package com.example.newsapp.ui.home.channel;

public class ChannelItem {
    private String name;        //频道名称
    private int spanSize;       //在recyclerview中占位
    private int layoutId;       //对应是什么类型

    public ChannelItem() {
    }

    public ChannelItem(String name, int spanSize, int layoutId) {
        this.name = name;
        this.spanSize = spanSize;
        this.layoutId = layoutId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
