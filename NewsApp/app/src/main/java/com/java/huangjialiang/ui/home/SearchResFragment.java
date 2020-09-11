package com.java.huangjialiang.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.huangjialiang.NewsDetailActivity;
import com.java.huangjialiang.R;
import com.java.huangjialiang.utils.HttpsTrustManager;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchResFragment extends Fragment implements AdapterView.OnClickListener {
    // 搜索结果不缓存
    private final int SEARCH_NUM = 1000;
    private final int MORE_NUM = 10;
    private final static String ARG_TYPE = "keyword";
    private String keyWord;
    // 下拉刷新和上拉加载更多
    private ImageButton imageButton;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private TextView keyWordTv;
    private NewsAdapter mAdapter;
    private SearchHelper helper;      //用于HTTPS获取数据
    int lastVisiableItem;
    private boolean isLoadingMore = false;      // 表明当前是否正在加载
    private boolean isScrollDown = false;   // 表明是否正在下拉
    private boolean initDone;               // 是否已经初始化
    private View root;
    private int onlinePage = 0;

    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "3702273900";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE = "";
    private IWBAPI mWBAPI;


    public static SearchResFragment newInstance(String keyWord) {
        SearchResFragment fragment = new SearchResFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, keyWord);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyWord = getArguments().getString(ARG_TYPE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search_res, container, false);
        findView(root);     //初始化PtrFrame和recycleView
        initView();     //初始化界面
        return root;
    }


    //查找view
    private void findView(View root) {
        recyclerView = root.findViewById(R.id.search_recycler_view);
        loadingIndicatorView = root.findViewById(R.id.search_avi);
        keyWordTv = root.findViewById(R.id.search_key_word);
        imageButton = root.findViewById(R.id.search_close_btn);
    }

    //返回更多数据
    private void loadMoreData() {
        Thread thread = new Thread(helper);
        thread.start();
    }

    // fragment回退
    private void back(){
        FragmentManager parentManager = getParentFragmentManager();
        FragmentTransaction transaction = parentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.search_in, R.anim.search_out);
        transaction.remove(parentManager.findFragmentByTag("SEARCH")).commit();
    }

    private void initWeiboSDK() {
        // 初始化微博分享
        AuthInfo authInfo = new AuthInfo(getContext(), APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(getContext());
        mWBAPI.registerApp(getContext(), authInfo);
        mWBAPI.setLoggerEnable(true);
    }

    private void initView() {
        initDone = false;
        initWeiboSDK();
        // 设置helper
        helper = new SearchHelper();
        // 设置textview
        keyWordTv.setText(keyWord);
        // 初始化RecyclerView
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new NewsAdapter(this, getContext(), mWBAPI);
        recyclerView.setAdapter(mAdapter);
        // 给按钮添加监听
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

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
        NewsInfo info = mAdapter.getPositionItem(position);
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra("id", info.getMyId());     //传入id
        startActivity(intent);
        //数据库保存，颜色变灰
        mAdapter.itemPressed(position);
        TextView titleTv = view.findViewById(R.id.news_list_title);
        titleTv.setTextColor(getContext().getColorStateList(R.color.grey));
    }

    // 判断是否联网
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                return true;
            }
        }
        return false;
    }

    private class SearchHelper implements Runnable {
        // 下面是新闻的内容
        private final String searchURL = getString(R.string.news_list_url);
        private List<NewsInfo> backend;

        public SearchHelper() {

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

        private void getSearchData() {
            URL url = null;
            try {
                url = new URL(searchURL + "?type=all" + "&page=1" + "&size=" + SEARCH_NUM);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONArray json = getData(url).getJSONArray("data");
            List<NewsInfo> allData = parseJson(json);
            backend = new ArrayList<>();
            for (NewsInfo item : allData) {
                if (item.getTitle().contains(keyWord)) {
                    backend.add(item);
                }
            }
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
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.changeState(NewsAdapter.LoadingType.NO_MORE);
                    }
                });
                loadingUIDone(backend.subList(start, start + num), 1000);
            }
        }

        private void loadingUIDone(final List<NewsInfo> data, int delay) {
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
                getSearchData();
            }
            getMore();
        }

        // 开始解析json
        private List<NewsInfo> parseJson(JSONArray json) {
            List<NewsInfo> data = new ArrayList<>();
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
                String newsType = item.getString("type");
                NewsInfo info = new NewsInfo(id, title, time, source, tflag, originURL,
                        content, newsType, "all");
                data.add(info);
            }
            return data;
        }
    }
}