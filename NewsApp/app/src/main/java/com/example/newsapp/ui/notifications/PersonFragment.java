package com.example.newsapp.ui.notifications;

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
    private static final String ARG_PASS = "is_passedaway";
    private final int MORE_NUM = 10;
    private final int UPDATE_NUM = 5;
    private final int MAX_CACHE_NUM = 1000;     //最多缓存1k条数据
    // 下拉刷新和上拉加载更多
    private final String UP = "MORE";
    private final String DOWN = "UPDATE";
    // TODO 实现下拉加载
    private NotificationsViewModel notificationsViewModel;
    private PtrFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private PersonAdapter mAdapter;
    private ListHelper helper;      //用于HTTPS获取数据
    private String type;            //代表当前Fragment对应的分类
    private ChannelFragment channelFragment = null;
    int lastVisiableItem;
    private boolean is_passedaway;
    private boolean isLoadingMore = false;      // 表明当前是否正在加载
    private boolean isScrollDown = false;   // 表明是否正在下拉
    private boolean initDone;               // 是否已经初始化
    private View root;
    private int onlinePage = 0;
    private int offlinePage = 0;
    private IWBAPI mWBAPI;
    private static final String APP_KY = "3702273900";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE = "";


    public static PersonFragment newInstance(String type) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    private void initWeiboSDK() {
        // 初始化微博分享
        AuthInfo authInfo = new AuthInfo(getContext(), APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(getContext());
        mWBAPI.registerApp(getContext(), authInfo);
        mWBAPI.setLoggerEnable(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            is_passedaway=getArguments().getBoolean(ARG_PASS);
            type = getArguments().getString(ARG_TYPE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            notificationsViewModel =
                    ViewModelProviders.of(this).get(NotificationsViewModel.class);
            root = inflater.inflate(R.layout.fragment_persons_list, container, false);
            findView(root);     //初始化PtrFrame和recycleView
            initView();     //初始化界面

        }
        return root;
    }


    //初始化PtrFrame和recycleView
    private void findView(View root) {
        mPtrFrame = root.findViewById(R.id.ptrFrameLayout);
        recyclerView = root.findViewById(R.id.recyclerView);
        loadingIndicatorView = root.findViewById(R.id.list_avi);
    }

    //返回更多数据
    private void loadMoreData() {
        helper.setType(UP);
        Thread thread = new Thread(helper);
        thread.start();
    }

    //获取最新数据
    private void updateData() {
        helper.setType(DOWN);
        Thread thread = new Thread(helper);
        thread.start();
    }

    private void initView() {
        initDone = false;

        // 设置helper
        initWeiboSDK();
        helper = new ListHelper();
        // 设置分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        recyclerView.addItemDecoration(divider);
        // 初始化RecyclerView
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new PersonAdapter(this, getContext(),mWBAPI);
        recyclerView.setAdapter(mAdapter);
        // 给下拉刷新加载header
        final MaterialHeader header = new MaterialHeader(getContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 15, 0, 15);
        int[] colors = getResources().getIntArray(R.array.refresh_color);
        header.setColorSchemeColors(colors);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        // 给下拉刷新加载handler
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    return true;
                else
                    return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
            }
        });
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
                int firstPos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0) {
                    mPtrFrame.setEnabled(false);
                } else {
                    mPtrFrame.setEnabled(true);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPtrFrame.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
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
        startActivity(intent);
        //数据库保存，颜色变灰

    }

    // 判断是否联网
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private class ListHelper implements Runnable {
        // 下面是新闻的内容
     //   private final String listUrl = getString(R.string.Person_list_url);
        private String loadingType; // 为MORE或者UPDATE，表示刷新还是获得更多

        public ListHelper() {

        }

        public void setType(String type) {
            this.loadingType = type;
        }


        public JSONObject getData(URL url) {
            JSONObject object = null;
            try {
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
                    Log.e("HTTPS", "[PersonDetailActivity line 80] NOT OK");
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
                object = JSONObject.parseObject(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return object;
        }

    /*    private List<PersonInfo> getCurrentPageData() {
            URL url = null;
            try {
                url = new URL(listUrl + "?type=" + type + "&page=" + onlinePage + "&size=" + MORE_NUM);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONArray json = getData(url).getJSONArray("data");
            return parseJson(json);
        }*/

        // 获取更多
        private void getMore() {
           /* if (isOnline()) {
                // 如果有网络
                onlinePage++;
                List<PersonInfo> data = getCurrentPageData();
                if (PersonInfo.count(PersonInfo.class) >= MAX_CACHE_NUM) {
                    // 如果超过缓存数目，那么清空表，重新添加数据
                    SugarRecord.deleteAll(PersonInfo.class);
                }
                // 储存到本地
                SugarRecord.saveInTx(data);
                final List<PersonInfo> newData = data;
                // 更新UI
                loadingUIDone(data, 500);
            } else {*/
                // 没有网络，尝试从数据库加载
        //    List<PersonInfo> data = PersonInfo.listAll(PersonInfo.class);
                List<PersonInfo> data = PersonInfo.find(PersonInfo.class,
                        "isPassedaway = ?",

                        type);
            //System.out.println(data.size());
             //   Log.i("TOTAL_CACHE", type + PersonInfo.count(PersonInfo.class, "type = ?", new String[]{type.toLowerCase()}));
            //    Log.i("CACHE", type + data.size());
                offlinePage++;
                // 数据库加载后排序

                // 更新UI
                if (data.size() == 0) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.changeState(PersonAdapter.LoadingType.NO_MORE);
                        }
                    });
                }
                loadingUIDone(data, 1000);
            //}

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

        // 获取更新
        private boolean updateMore() {
            return true;
            /*
            if (!isOnline()) {
                // 没有网络
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "您未联网", Toast.LENGTH_LONG).show();
                    }
                }, 500);
                mPtrFrame.refreshComplete();
                return false;
            }
            URL url = null;
            try {
                url = new URL(listUrl + "?type=" + type + "&page=" + 1 + "&size=" + UPDATE_NUM);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONArray json = getData(url).getJSONArray("data");
            final List<PersonInfo> data = parseJson(json);
            // 和目前Adapter的数据比对并添加
            boolean updated = mAdapter.checkUpdateData(data.get(0));
            Log.i("Update", Boolean.toString(updated));
            if (updated) {
                // 重新获取更新后的数据
                onlinePage = 0;
                final List<PersonInfo> newData = getCurrentPageData();
                // 重新设置数据
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "成功刷新", Toast.LENGTH_LONG).show();
                        mAdapter.resetData(newData);
                    }
                }, 500);
                mPtrFrame.refreshComplete();    //结束刷新
                return true;
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "已是最新", Toast.LENGTH_LONG).show();
                    }
                }, 500);
                mPtrFrame.refreshComplete();    //结束刷新
                return false;
            }*/

        }

        @Override
        public void run() {
            if (loadingType == "MORE")
                getMore();
            else if (loadingType == "UPDATE")
                updateMore();
        }

        // 开始解析json
     /*   private List<PersonInfo> parseJson(JSONArray json) {
            List<PersonInfo> data = new ArrayList<>();
            for (int i = 0; i < json.size(); ++i) {
                JSONObject item = json.getJSONObject(i);
                String content = item.getString("content");
                String time = item.getString("time");
                long tflag = item.getLongValue("tflag");
                String title = item.getString("title");
                String source = item.getString("source");
                source = (source != null) ? source : "未知来源";
                JSONArray originarr = item.getJSONArray("urls");
                String originURL = (originarr.size() > 0) ? originarr.getString(0) : "未知URL";
                String id = item.getString("_id");
                String PersonType = item.getString("type");
                PersonInfo info = new PersonInfo(id, title, time, source, tflag, originURL,
                        content, PersonType, type);
                data.add(info);
            }
            return data;
        }*/
    }
}