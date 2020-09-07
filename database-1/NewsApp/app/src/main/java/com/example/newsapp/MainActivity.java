package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.ui.home.JsonReader;

public class MainActivity extends AppCompatActivity {
    Button newsListBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String jsonString = JsonReader.getJson(this, "lists.json");
        JSONArray jsonArray = JSONObject.parseObject(jsonString).getJSONArray("datas");

        NewsInfo.deleteAll(NewsInfo.class);
        for (int i = 0; i < jsonArray.size(); ++i) {
            JSONObject item = jsonArray.getJSONObject(i);
            NewsInfo news=JsonReader.getNewsInfo(item);
            System.out.println(news);
            news.save();

        }

        setContentView(R.layout.activity_main);
        newsListBtn = findViewById(R.id.newsListBtn);
        newsListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewsList.class);
                startActivity(intent);
            }
        });
    }
}