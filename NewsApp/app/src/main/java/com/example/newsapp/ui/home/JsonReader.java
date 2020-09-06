package com.example.newsapp.ui.home;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonReader {
    public static String getJson(Context context, String filename) {
        StringBuilder builder = new StringBuilder();
        try {
            AssetManager manager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    manager.open(filename)));
            String line;
            while ((line = bf.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

//    public static NewsInfo getNewsInfo(JSONObject item) {
//        JSONArray geoInfo = item.getJSONArray("geoInfo");
//        String origin = geoInfo.size() > 0 ? geoInfo.getJSONObject(0).getString("originText") : "未知";
//        return new NewsInfo(item.getString("_id"), item.getString("type"), item.getString("title"),
//                item.getString("time"), item.getString("lang"), origin);
//    }
}
