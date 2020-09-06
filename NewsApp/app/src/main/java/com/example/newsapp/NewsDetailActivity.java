package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.utils.HttpsTrustManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class NewsDetailActivity extends AppCompatActivity {
    AVLoadingIndicatorView loadingIndicatorView;
    TextView detailTitle, detailSource, detailTime, detailType, detailLink, detailContent;
    ImageView influence_icon;
    LinearLayout container;
    Handler mHandler;
    DetailHelper detailHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        findView();     //找到所有的TextView
        loadingIndicatorView.smoothToShow();
        container.setVisibility(View.GONE);
        mHandler = new Handler();

        // 下面构建子线程
        String targetID = getIntent().getStringExtra("id");
        detailHelper = new DetailHelper(getString(R.string.news_detail_url) + targetID);
        Log.i("URL",getString(R.string.news_detail_url) + targetID);
        new Thread(detailHelper).start();
    }


    // 内部类，用于网络通信
    private class DetailHelper implements Runnable {
        private String urlString;
        // 下面是新闻的内容
        private String content;
        private String time;
        private String lang;
        private String source;
        private String type;
        private String title;
        private String originURL;

        public DetailHelper(String url) {
            this.urlString = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(urlString);
                HttpsTrustManager.allowAllSSL();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setAllowUserInteraction(false);
                connection.setInstanceFollowRedirects(true);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                // 发起请求
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("HTTPS", "[NewsDetailActivity line 80] NOT OK");
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                connection.disconnect();

                // 解析Json
                JSONObject json = JSONObject.parseObject(builder.toString()).getJSONObject("data");
                parseJson(json);

                // 更新UI
                updateUI();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 开始解析json
        private void parseJson(JSONObject json) {
            content = json.getString("content");
            time = json.getString("time");
            type = json.getString("type");
            title = json.getString("title");
            source = json.getString("source");
            source = (source != null) ? source : "未知来源";
            lang = json.getString("lang");
            originURL = json.getJSONArray("urls").getString(0);
        }

        private void updateUI() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 设置图标
                    int randNum = new Random().nextInt(3);
                    switch (randNum){
                        case 0:
                            influence_icon.setImageDrawable(getDrawable(R.drawable.circle_strong));
                            break;
                        case 1:
                            influence_icon.setImageDrawable(getDrawable(R.drawable.circle_normal));
                            break;
                        case 2:
                            influence_icon.setImageDrawable(getDrawable(R.drawable.circle_weak));
                            break;
                    }
                    detailTitle.setText(title);
                    detailSource.setText(source);
                    detailTime.setText(time);
                    detailType.setText(type);
                    detailContent.setText(content);
                    String html = "<a href=\"" + originURL + "\">原文链接</a>";
                    detailLink.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
                    detailLink.setMovementMethod(LinkMovementMethod.getInstance());
                    loadingIndicatorView.hide();    //隐藏加载
                    container.setVisibility(View.VISIBLE);  //显示内容
                }
            }, 500);
        }
    }

    private void findView() {
        detailTitle = findViewById(R.id.detail_title);
        detailSource = findViewById(R.id.detail_source);
        detailTime = findViewById(R.id.detail_time);
        detailType = findViewById(R.id.detail_type);
        detailLink = findViewById(R.id.detail_link);
        detailContent = findViewById(R.id.detail_content);
        influence_icon = findViewById(R.id.influence_icon);

        container = findViewById(R.id.detail_content_layout);
        loadingIndicatorView = findViewById(R.id.detail_avi);
    }
}
