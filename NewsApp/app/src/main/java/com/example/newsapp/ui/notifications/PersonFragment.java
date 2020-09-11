package com.example.newsapp.ui.notifications;

import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.PersonDetailActivity;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.example.newsapp.newsDetailActivity;
import com.example.newsapp.R;
import com.example.newsapp.ui.notifications.NotificationsViewModel;

import com.example.newsapp.ui.notifications.PersonInfo;
import com.example.newsapp.ui.home.channel.ChannelFragment;
import com.example.newsapp.utils.HttpsTrustManager;
import com.orm.SugarRecord;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class PersonFragment extends Fragment implements AdapterView.OnClickListener {
    private static final String ARG_TYPE = "type";
    private final int MORE_NUM = 6;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private PersonAdapter mAdapter;
    private PersonListHelper helper;      //用于HTTPS获取数据
    private String type;            //代表当前Fragment对应的分类
    int lastVisiableItem;
    private boolean isLoadingMore = false;      // 表明当前是否正在加载
    private boolean isScrollDown = false;   // 表明是否正在下拉
    private boolean initDone = false;               // 是否已经初始化
    private View root;
    private int offlinePage = 0;
    // TODO 添加微博分享功能
    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "3702273900";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE = "";
    private IWBAPI mWBAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_persons_list, container, false);
            findView(root);     //初始化PtrFrame和recycleView
            initView();     //初始化界面
        }
        return root;
    }

    private void initWeiboSDK() {
        // 初始化微博分享
        AuthInfo authInfo = new AuthInfo(getContext(), APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(getContext());
        mWBAPI.registerApp(getContext(), authInfo);
        mWBAPI.setLoggerEnable(true);
    }

    //初始化PtrFrame和recycleView
    private void findView(View root) {
        recyclerView = root.findViewById(R.id.person_list_recycler_view);
        loadingIndicatorView = root.findViewById(R.id.person_list_avi);
    }

    //返回更多数据
    private void loadMoreData() {
        Thread thread = new Thread(helper);
        thread.start();
    }

    private void initView() {
        // 初始化微博分享
        initWeiboSDK();
        // 设置helper
        helper = new PersonListHelper();
        // 初始化RecyclerView
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new PersonAdapter(this, getContext(), mWBAPI);
        recyclerView.setAdapter(mAdapter);
        // 初始化数据
        loadingIndicatorView.show();
        loadMoreData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int Persontate) {
                super.onScrollStateChanged(recyclerView, Persontate);
                if (Persontate == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem == mAdapter.getItemCount() - 1 && !isLoadingMore && isScrollDown) {
                    isLoadingMore = true;
                    mAdapter.changeState(PersonAdapter.LoadingType.LOADING_MORE);
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
        PersonInfo info = mAdapter.getPositionItem(position);
        Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
        intent.putExtra("id", info.getMyID());     //传入id
        String name = info.getName_zh();
        name = (name == null || name.length() == 0) ? info.getName() : name;
        intent.putExtra("name", name);     //传入name
        startActivity(intent);
    }

    private class PersonListHelper implements Runnable {
        List<PersonInfo> backup;

        public PersonListHelper() {

        }

        // 获取更多
        private void getMore() {
            if (!initDone)
                backup = PersonInfo.find(PersonInfo.class, "isPassedaway = ?", type);
            Log.i("PersonFragment", "type = " + type + ", backup number = " + backup.size());
            int start = offlinePage * MORE_NUM;
            int loadNum = Math.min(MORE_NUM, backup.size() - start);
            if (loadNum > 0) {
                final List<PersonInfo> data = backup.subList(start, start + loadNum);
                offlinePage++;
                loadingUIDone(data, 1000);
            } else {
                // 没有数据了
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.changeState(PersonAdapter.LoadingType.NO_MORE);
                    }
                });
                loadingUIDone(null, 1000);
            }
        }

        private void loadingUIDone(final List<PersonInfo> data, int delay) {
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
            getMore();
        }
    }
}