package com.java.huangjialiang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.huangjialiang.ui.dashboard.cluster.ClusterAdapter;
import com.java.huangjialiang.ui.dashboard.cluster.ClusterItem;
import com.java.huangjialiang.ui.home.NewsAdapter;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

public class ClusterKeywordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ARG_TYPE = "type";
    private final int MORE_NUM = 6;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private TextView titleTv;
    private ImageButton backBtn;
    private ClusterAdapter mAdapter;
    private ClusterHelper helper;

    private String type;            //代表当前Fragment对应的分类
    private boolean isLoadingMore = false;      // 表明当前是否正在加载
    private boolean isScrollDown = false;   // 表明是否正在下拉
    private int lastVisiableItem;
    private boolean initDone;               // 是否已经初始化
    private int onlinePage = 0;

    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "3702273900";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE = "";
    private IWBAPI mWBAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_detail);
        findView();     //找到所有的TextView
        initView();     //初始化界面

    }


    private void findView() {
        recyclerView = findViewById(R.id.cluster_detail_recycler_view);
        loadingIndicatorView = findViewById(R.id.cluster_detail_avi);
        titleTv = findViewById(R.id.cluster_detail_title);
        backBtn = findViewById(R.id.cluster_detail_close_btn);
    }

    private void initWeiboSDK() {
        // 初始化微博分享
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
        mWBAPI.setLoggerEnable(true);
    }

    //返回更多数据
    private void loadMoreData() {
        Thread thread = new Thread(helper);
        thread.start();
    }

    // 设置退出按钮
    private void setButton() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void initView() {
        initDone = false;
        type = getIntent().getStringExtra("type");
        // 设置标题
        titleTv.setText(type);
        // 配置按钮
        setButton();
        // 设置微博信息
        initWeiboSDK();
        // 设置helper
        helper = new ClusterHelper();
        // 初始化RecyclerView
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new ClusterAdapter(this, this, mWBAPI);
        recyclerView.setAdapter(mAdapter);
        // 初始化数据
        loadingIndicatorView.show();
        loadMoreData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem == mAdapter.getItemCount() - 1 && !isLoadingMore && isScrollDown) {
                    isLoadingMore = true;
                    mAdapter.changeState(NewsAdapter.LoadingType.LOADING_MORE);
                    loadMoreData();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiableItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (dy > 0)
                    isScrollDown = true;
                else
                    isScrollDown = false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        //只有新闻添加了监听
        ClusterItem info = mAdapter.getPositionItem(position);
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("id", info.getMyId());     //传入id
        startActivity(intent);
        //数据库保存，颜色变灰
        mAdapter.itemPressed(position);
        TextView titleTv = view.findViewById(R.id.news_list_title);
        titleTv.setTextColor(this.getColorStateList(R.color.grey));
    }

    private class ClusterHelper implements Runnable {
        // 下面是新闻的内容
        private List<ClusterItem> backend;

        public ClusterHelper() {
        }

        private void getInitData() {
            backend = ClusterItem.find(ClusterItem.class, "type = ?",
                    ClusterKeywordActivity.this.type);
        }


        // 获取更多
        private void getMore() {
            int start = onlinePage * MORE_NUM;
            int num = Math.min(backend.size() - start, MORE_NUM);
            if (num > 0) {
                onlinePage++;
                // 更新UI
                loadingUIDone(backend.subList(start, start + num), 500);
            } else {
                // 划到底了
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.changeState(NewsAdapter.LoadingType.NO_MORE);
                    }
                });
                loadingUIDone(backend.subList(start, start + num), 1000);
            }
        }

        // 更新UI界面
        private void loadingUIDone(final List<ClusterItem> data, int delay) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateData(data);
                    if (!initDone) {
                        loadingIndicatorView.hide();
                        initDone = true;
                    }
                    isLoadingMore = false;
                    recyclerView.smoothScrollBy(0, -50);    // 划一些上去
                }
            }, delay);
        }

        @Override
        public void run() {
            if (!initDone) {
                getInitData();
            }
            getMore();
        }
    }
}
