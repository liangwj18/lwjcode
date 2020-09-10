package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.ui.dashboard.GraphInfo;
import com.example.newsapp.ui.home.HomeViewModel;
import com.example.newsapp.ui.home.NewsAdapter;
import com.example.newsapp.ui.home.NewsInfo;
import com.example.newsapp.ui.home.NewsListFragment;
import com.example.newsapp.ui.home.channel.ChannelFragment;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

//import com.example.newsapp.ui.dashboard.GraphAdapter;

public class ClusterKeywordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ARG_TYPE = "type";
    private final int MORE_NUM = 10;
    private final int UPDATE_NUM = 5;
    private final int MAX_CACHE_NUM = 1000;     //最多缓存1k条数据
    // 下拉刷新和上拉加载更多
    private final String UP = "MORE";
    private final String DOWN = "UPDATE";
    // TODO 实现下拉加载
    private HomeViewModel homeViewModel;
    private PtrFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private NewsAdapter mAdapter;

    private String type;            //代表当前Fragment对应的分类
    private ChannelFragment channelFragment = null;
    int lastVisiableItem;
    private boolean isLoadingMore = false;      // 表明当前是否正在加载
    private boolean isScrollDown = false;   // 表明是否正在下拉
    private boolean initDone;               // 是否已经初始化
    private View root;
    private int onlinePage = 0;
    private int offlinePage = 0;
    private List<NewsInfo> infolist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_list);
        findView();     //找到所有的TextView
        loadingIndicatorView.hide();
       // container.setVisibility(View.GONE);

        String Type=getIntent().getStringExtra("type");
        String arg[]=new String[]{Type};
         infolist = NewsInfo.find(NewsInfo.class,"Type = ?",arg);
      //   root=findViewById(R.layout.fragment_news_list);
        initView();     //初始化界面

    }


    private void initView() {
        initDone = false;
        // 设置helper

        // 设置分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.list_divider));
        recyclerView.addItemDecoration(divider);
        // 初始化RecyclerView
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new NewsAdapter(this, this,null);
        recyclerView.setAdapter(mAdapter);
        // 给下拉刷新加载header
        final MaterialHeader header = new MaterialHeader(this);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 15, 0, 15);
        int[] colors = getResources().getIntArray(R.array.refresh_color);
        header.setColorSchemeColors(colors);
      //  mPtrFrame.setHeaderView(header);
      //  mPtrFrame.addPtrUIHandler(header);
        // 给下拉刷新加载handler
     /*   mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    return true;
                else
                    return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

            }
        });*/
        // 初始化数据
    //    loadingIndicatorView.show();
        mAdapter.resetData(infolist);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
              /*  if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem == mAdapter.getItemCount() - 1 && !isLoadingMore && isScrollDown) {
                 //   isLoadingMore = true;
                  //  mAdapter.changeState(NewsAdapter.LoadingType.LOADING_MORE);
                   // loadMoreData();
                }
                int firstPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0) {
                    mPtrFrame.setEnabled(false);
                } else {
                    mPtrFrame.setEnabled(true);
                }*/
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            //    mPtrFrame.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            //    lastVisiableItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            //    if (dy > 0)
             //       isScrollDown = true;
             //   else
             //       isScrollDown = false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        //只有新闻添加了监听
        NewsInfo info = mAdapter.getPositionItem(position);
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("id", info.getMyId());     //传入id
        startActivity(intent);
        //数据库保存，颜色变灰
    }

    private void findView() {


          //  mPtrFrame = findViewById(R.id.ptrFrameLayout);
            recyclerView = findViewById(R.id.recyclerView);
            loadingIndicatorView = findViewById(R.id.list_avi);



    }
}
