package com.example.newsapp.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.NewsDetailActivity;
import com.example.newsapp.R;
import com.example.newsapp.ui.home.channel.ChannelFragment;
import com.example.newsapp.utils.HttpsTrustManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class NewsListFragment extends Fragment implements AdapterView.OnClickListener {
    private static final String ARG_TYPE = "type";
    private final int MORE_NUM = 8;
    private final int UPDATE_MAX_NUM = 20;
    // TODO 实现下拉加载
    private HomeViewModel homeViewModel;
    private PtrFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private NewsAdapter mAdapter;
    private ListHelper helper;      //用于HTTPS获取数据
    private String type;            //代表当前Fragment对应的分类
    private ChannelFragment channelFragment = null;
    int lastVisiableItem;
    private boolean isLoadingMore = false;      // 表明当前是否正在加载
    private boolean isScrollDown = false;   // 表明是否正在下拉
    private boolean initDone;               // 是否已经初始化
    private View root;
    private int page = 0;

    public static NewsListFragment newInstance(String type) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

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
            homeViewModel =
                    ViewModelProviders.of(this).get(HomeViewModel.class);
            root = inflater.inflate(R.layout.fragment_news_list, container, false);
            findView(root);     //初始化PtrFrame和recycleView
            initView();     //初始化界面
            Log.i("Create", type);
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
        Thread thread = new Thread(helper);
        thread.start();
    }

    //获取最新数据
    private void updateData() {

    }

    private void initView() {
        initDone = false;
        Log.i("TYPE", type);
        // 设置helper
        helper = new ListHelper();
        // 设置分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        recyclerView.addItemDecoration(divider);
        // 初始化RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new NewsAdapter(this, getContext());
        recyclerView.setAdapter(mAdapter);
        // 初始化数据
        loadingIndicatorView.smoothToShow();
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
        NewsInfo info = mAdapter.getPositionItem(position);
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra("id", info.myId);     //传入id
        startActivity(intent);
        //数据库保存，颜色变灰
        mAdapter.itemPressed(position);
        TextView titleTv = view.findViewById(R.id.news_list_title);
        titleTv.setTextColor(getContext().getColorStateList(R.color.grey));
    }

    private class ListHelper implements Runnable {
        // 下面是新闻的内容
        private  final String listUrl = getString(R.string.news_list_url);

        public ListHelper() {

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
                object = JSONObject.parseObject(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return object;
        }

        // 获取更多
        void getMore() {
            page++;
            URL url = null;
            try {
                url = new URL(listUrl + "?type=" + type + "&page=" + page + "&size=" + MORE_NUM);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONArray json = getData(url).getJSONArray("data");
            final List<NewsInfo> data = parseJson(json);
            // 更新UI
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateData(data);
                    if (!initDone) {
                        loadingIndicatorView.hide();
                        initDone = true;
                    }
                }
            }, 500);
            isLoadingMore = false;
        }

        // 获取更新
        void updateMore(){
            URL url = null;
            try {
                url = new URL(listUrl + "?type=" + type + "&page=" + 1 + "&size=" + UPDATE_MAX_NUM);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONArray json = getData(url).getJSONArray("data");
            final List<NewsInfo> data = parseJson(json);
            // 更新UI
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateData(data);
                    if (!initDone) {
                        loadingIndicatorView.hide();
                        initDone = true;
                    }
                }
            }, 500);
            isLoadingMore = false;
        }

        @Override
        public void run() {
            getMore();
        }

        // 开始解析json
        private List<NewsInfo> parseJson(JSONArray json) {
            List<NewsInfo> data = new ArrayList<>();
            for (int i = 0; i < json.size(); ++i) {
                JSONObject item = json.getJSONObject(i);
                String content = item.getString("content");
                String time = item.getString("time");
                String tflag = item.getString("tflag");
                String title = item.getString("title");
                String source = item.getString("source");
                source = (source != null) ? source : "未知来源";
                String originURL = item.getJSONArray("urls").getString(0);
                String id = item.getString("_id");
                String newsType = item.getString("type");
                NewsInfo info = new NewsInfo(id, title, time, source, tflag, originURL, content, newsType);
                data.add(info);
            }
            return data;
        }
    }
}