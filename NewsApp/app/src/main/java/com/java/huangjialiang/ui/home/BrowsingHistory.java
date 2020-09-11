package com.java.huangjialiang.ui.home;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class BrowsingHistory extends SugarRecord {
    @Unique
    String myId;
    public BrowsingHistory(){

    }
    public BrowsingHistory(String myId) {
        this.myId = myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getMyId() {
        return myId;
    }
}
