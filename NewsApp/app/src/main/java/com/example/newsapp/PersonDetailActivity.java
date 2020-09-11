package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.ui.home.NewsInfo;
import com.example.newsapp.utils.HttpsTrustManager;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class PersonDetailActivity extends AppCompatActivity implements WbShareCallback {
    private AVLoadingIndicatorView loadingIndicatorView;
    private TextView detailTitle, detailSource, detailTime, detailType, detailLink, detailContent;
    private ImageView influence_icon;
    private ImageButton shareBtn, backBtn;
    private LinearLayout container;
    private Handler mHandler;
    private DetailHelper detailHelper;
    private IWBAPI mWBAPI;

    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "3702273900";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        findView();     //找到所有的TextView
        initBtn();      //给按钮增加监听
        loadingIndicatorView.show();
        container.setVisibility(View.GONE);
        mHandler = new Handler(Looper.getMainLooper());

        // 初始化微博分享
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
        mWBAPI.setLoggerEnable(true);

        // 下面构建子线程，开始读取数据
        String targetID = getIntent().getStringExtra("id");
        detailHelper = new DetailHelper(targetID);
        Log.i("URL", getString(R.string.news_detail_url) + targetID);
        new Thread(detailHelper).start();
    }

    private void initBtn() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doWeiboShare();
            }
        });
    }

    private String getShareContent() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Title] : " + detailTitle.getText()+"\n\n");
        builder.append("[Type] : " + detailType.getText()+"\n");
        builder.append("[Time] : " + detailTime.getText()+"\n");
        builder.append("[Source] : " + detailSource.getText()+"\n\n");
        builder.append("[content] : " + detailContent.getText());
        builder.append("\n\n来自NewsAPP客户端自动生成");
        return builder.toString();
    }

    private void doWeiboShare() {
        Log.i("Weibo", "Start to share");
        WeiboMultiMessage message = new WeiboMultiMessage();

        TextObject textObject = new TextObject();
        String text = getShareContent();

        // 分享文字
        textObject.text = text;
        message.textObject = textObject;
        mWBAPI.shareMessage(message, true);
    }

    // 分享完后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWBAPI.doResultIntent(data, this);
    }


    // 内部类，用于网络通信
    private class DetailHelper implements Runnable {
        private String urlString;
        private String targetID;
        // 下面是新闻的内容
        private String content;
        private String time;
        private String source;
        private String type;
        private String title;
        private String originURL;

        public DetailHelper(String targetID) {
            this.targetID = targetID;
            this.urlString = getString(R.string.news_detail_url) + targetID;
        }

        @Override
        public void run() {
            // 首先查看数据库
            List<NewsInfo> targetList = NewsInfo.find(NewsInfo.class, "my_id = ?", targetID);
            if (targetList.size() != 0) {
                NewsInfo target = targetList.get(0);
                content = target.getContent();
                time = target.getTime();
                type = target.getNewsType();
                title = target.getTitle();
                source = target.getSource();
                originURL = target.getOriginURL();
                Log.i("DETAIL", "Load from database");
            } else {
                // 数据库中没有再网络加载
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
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("DETAIL", "Load from internet");
            }
            // 更新UI
            updateUI();
        }

        // 开始解析json
        private void parseJson(JSONObject json) {
            content = json.getString("content");
            time = json.getString("time");
            type = json.getString("type");
            title = json.getString("title");
            source = json.getString("source");
            source = (source != null) ? source : "未知来源";
            originURL = json.getJSONArray("urls").getString(0);
        }

        private void updateUI() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 设置图标
                    int randNum = new Random().nextInt(3);
                    switch (randNum) {
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
                    Spannable s = (Spannable) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
                    for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
                        s.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                                tp.setColor(getColor(R.color.yellow));
                            }
                        }, s.getSpanStart(u), s.getSpanEnd(u), 0);
                    }
                    detailLink.setText(s);
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
        shareBtn = findViewById(R.id.detail_share_btn);
        backBtn = findViewById(R.id.detail_back_btn);

        container = findViewById(R.id.detail_content_layout);
        loadingIndicatorView = findViewById(R.id.detail_avi);
    }

    // 􏲚􏲛WbShareCallback􏲜􏲝􏰫􏲞􏲉􏰞􏰟􏲙􏱧􏱄
    @Override
    public void onComplete() {
        Toast.makeText(PersonDetailActivity.this, "􏰞􏰟􏰔􏲆分享成功", Toast.LENGTH_LONG);
    }

    @Override
    public void onError(UiError error) {
        Toast.makeText(PersonDetailActivity.this, "􏰞􏰟􏲟􏲠分享失败:" + error.errorMessage, Toast.LENGTH_SHORT);
    }

    @Override
    public void onCancel() {
        Toast.makeText(PersonDetailActivity.this, "􏰞􏰟􏲉􏲊分享取消", Toast.LENGTH_SHORT);
    }
}
