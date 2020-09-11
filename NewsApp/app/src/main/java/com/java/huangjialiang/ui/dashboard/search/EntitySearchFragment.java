package com.java.huangjialiang.ui.dashboard.search;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.huangjialiang.GraphDetailActivity;
import com.java.huangjialiang.R;
import com.java.huangjialiang.ui.dashboard.GraphInfo;
import com.java.huangjialiang.utils.HttpsTrustManager;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class EntitySearchFragment extends Fragment implements AdapterView.OnClickListener,
        MaterialSearchBar.OnSearchActionListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout containerLayout;
    private AVLoadingIndicatorView loadingIndicatorView;
    private MaterialSearchBar searchBar;
    private EntityAdapter mAdapter;
    private SearchHelper helper;      //用于HTTPS获取数据
    private View root;

    private final String historyFileName = "entity_history";

    public EntitySearchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i("SearchFragment", "OnCreateView");
        root = inflater.inflate(R.layout.fragment_entity_search, container, false);
        findView(root);     //初始化PtrFrame和recycleView
        initView();     //初始化界面
        return root;
    }

    private List<String> loadSearchSuggestionFromDisk() {
        try {
            FileInputStream fileInputStream = getContext().openFileInput(historyFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            Log.i("HISTORY", builder.toString());
            return JSON.parseArray(builder.toString(), String.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<String> history = searchBar.getLastSuggestions();
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput(historyFileName, Context.MODE_PRIVATE);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(JSON.toJSONString(history));
            Log.i("HISTORY", JSON.toJSONString(history));
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void findView(View root) {
        recyclerView = root.findViewById(R.id.entity_recycler_view);
        loadingIndicatorView = root.findViewById(R.id.entity_avi);
        searchBar = root.findViewById(R.id.entity_search_bar);
        containerLayout = root.findViewById(R.id.entity_search_container);
    }

    //返回更多数据
    private void initData(String entity) {
        loadingIndicatorView.show();
        containerLayout.setVisibility(View.VISIBLE);
        helper.setEntity(entity);
        Thread thread = new Thread(helper);
        thread.start();
    }


    private void initView() {
        // 设置helper
        helper = new SearchHelper();
        // 关闭加载动画
        loadingIndicatorView.hide();
        // 初始化RecyclerView
        gridLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        // 为列表构建一个监听器，监听是否上拉加载更多
        mAdapter = new EntityAdapter(this, getContext());
        recyclerView.setAdapter(mAdapter);
        // 设置搜索框
        searchBar.setHint("请输入关键词");
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(this);
        //restore last queries from disk
        List<String> lastSearches = loadSearchSuggestionFromDisk();
        Log.i("HISTORY", "Loading");
        if (lastSearches != null)
            searchBar.setLastSuggestions(lastSearches);
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        GraphInfo info = mAdapter.getPositionItem(position);
        // 打开详情页面
        Intent intent = new Intent(getActivity(), GraphDetailActivity.class);
        intent.putExtra("info", info);
        startActivity(intent);
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
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        // 取消搜索时 DO NOTHING
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchBar.closeSearch();
        if (!isOnline()) {
            // 没有联网的话
            Toast.makeText(getContext(), "未联网无法启用搜索", Toast.LENGTH_LONG).show();
            return;
        }
        // 开始搜索
        initData(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                Log.i("BUTTON", "NAVIGATION");
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.closeSearch();
                break;
        }
    }

    private class SearchHelper implements Runnable {
        private final String searchURL = getString(R.string.entity);
        private List<GraphInfo> backend;
        private String entity;

        public SearchHelper() {

        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        private JSONObject getData(URL url) {
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
                url = new URL(searchURL + this.entity);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONArray json = getData(url).getJSONArray("data");
            backend = parseJson(json);
        }


        private void loadingUIDone(int delay) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateData(backend);
                    loadingIndicatorView.hide();
                    containerLayout.setVisibility(View.GONE);
                }
            }, delay);
        }

        @Override
        public void run() {
            getSearchData();
            loadingUIDone(800);
        }

        // 开始解析json
        private List<GraphInfo> parseJson(JSONArray json) {
            List<GraphInfo> data = new ArrayList<>();
            for (int i = 0; i < json.size(); ++i) {
                JSONObject object = json.getJSONObject(i);
                String label = object.getString("label");
                String img = object.getString("img");
                JSONObject abstarctInfo = object.getJSONObject("abstractInfo");
                String info = "";
                String enwiki = abstarctInfo.getString("enwiki");
                String baidu = abstarctInfo.getString("baidu");
                String zhwiki = abstarctInfo.getString("zhwiki");

                info = enwiki;
                if (info.length() < baidu.length()) info = baidu;
                if (info.length() < zhwiki.length()) info = zhwiki;

                JSONObject covid = abstarctInfo.getJSONObject("COVID");
                String properties = covid.getString("properties");
                String relations = covid.getString("relations");
                GraphInfo newItem = new GraphInfo(img, label, info, properties, relations);
                data.add(newItem);
            }
            return data;
        }
    }
}