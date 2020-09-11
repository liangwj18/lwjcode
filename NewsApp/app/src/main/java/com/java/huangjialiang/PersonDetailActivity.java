package com.java.huangjialiang;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.java.huangjialiang.ui.notifications.detail.AcademicScoreFragment;
import com.java.huangjialiang.ui.notifications.detail.BasicInfoFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

public class PersonDetailActivity extends AppCompatActivity {
    TextView personNameTv;
    SmartTabLayout tabLayout;
    ViewPager viewPager;
    String id, name;

    // TODO 微博分享功能
    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "3702273900";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE = "";
    IWBAPI mWBAPI;

    private void initWeiboSDK() {
        // 初始化微博分享
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
        mWBAPI.setLoggerEnable(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        findView();     //找到所有的TextView
        initView();
    }

    private void findView() {
        personNameTv = findViewById(R.id.person_detail_title);
        tabLayout = findViewById(R.id.person_detail_pager_tab);
        viewPager = findViewById(R.id.person_detail_view_pager);
    }

    private void initView() {
        // 先获得数据
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        // 设置标题
        personNameTv.setText(name);
        // 初始化两个fragment，并传入info
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        FragmentPagerItems pages = FragmentPagerItems.with(this)
                .add("基本信息", BasicInfoFragment.class, bundle)
                .add("学术与标签", AcademicScoreFragment.class, bundle)
                .create();
        // 初始化tab
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);
    }
}