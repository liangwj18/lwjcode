package com.java.huangjialiang.ui.dashboard.cluster;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.huangjialiang.ClusterKeywordActivity;
import com.java.huangjialiang.R;
import com.orm.SugarRecord;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClusterFragment extends Fragment implements AdapterView.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private TypeAdapter mAdapter;
    private ClusterHelper helper;      //用于HTTPS获取数据
    private boolean initDone;               // 是否已经初始化
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cluster_channel, container, false);
        findView(root);     //初始化PtrFrame和recycleView
        initView();     //初始化界面
        return root;
    }

    //recycleView
    private void findView(View root) {
        recyclerView = root.findViewById(R.id.cluster_recycler_view);
        loadingIndicatorView = root.findViewById(R.id.cluster_avi);
    }

    //返回数据
    private void loadMoreData() {
        Thread thread = new Thread(helper);
        thread.start();
    }

    private void initView() {
        initDone = false;
        // 设置helper
        helper = new ClusterHelper();
        // 初始化RecyclerView
        gridLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new TypeAdapter(this, getContext());
        recyclerView.setAdapter(mAdapter);

        // 初始化数据
        loadingIndicatorView.show();
        loadMoreData();
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        //只有新闻添加了监听
        String type = mAdapter.getPositionItem(position);   //点击的类别
        Intent intent = new Intent(getActivity(), ClusterKeywordActivity.class);
        intent.putExtra("type", type);     //传入id
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

    private class ClusterHelper implements Runnable {
        // 下面是新闻的内容
        private List<String> keyWordList;

        public ClusterHelper() {

        }

        private void getData() {
            keyWordList = new ArrayList<String>();
            List<Integer> typesList = new ArrayList<>();
            List<ClusterItem> clusterItemList = new ArrayList<>();
            // 读入类别关键词
            Scanner scanner = new Scanner(getResources().openRawResource(R.raw.keyword_ch));
            String st = scanner.nextLine();
            String s[] = st.split(" ");
            for (int i = 0; i < s.length; ++i)
                keyWordList.add(s[i]);
            loadingUIDone(keyWordList, 500);
            scanner.close();
            // 解析Json，一把存入数据库
            // 如果数据库已经有了，就不存了
            if (ClusterItem.count(ClusterItem.class, null, null, null, null, "1") > 0) {
                Log.i("ClusterItem", "Already in database : " + ClusterItem.count(ClusterItem.class));
                return;
            } else {
                // 读入类别
                scanner = new Scanner(getResources().openRawResource(R.raw.type));
                while (scanner.hasNext()) {
                    int num = Integer.parseInt(scanner.next());
                    typesList.add(num);
                }
                scanner.close();
                // 读入新闻存入数据库
                StringBuilder builder = new StringBuilder();
                scanner = new Scanner(getResources().openRawResource(R.raw.events));
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine());
                }
                JSONArray jsonArray = JSONObject.parseArray(builder.toString());
                clusterItemList = parseJson(jsonArray, typesList);
                SugarRecord.saveInTx(clusterItemList);
            }
        }

        private void loadingUIDone(final List<String> data, int delay) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateData(data);
                    if (!initDone) {
                        loadingIndicatorView.hide();
                        initDone = true;
                    }
                }
            }, delay);
        }

        @Override
        public void run() {
            if (!initDone)
                getData();
        }

        // 开始解析json
        private List<ClusterItem> parseJson(JSONArray json, List<Integer> typeList) {
            List<ClusterItem> data = new ArrayList<>();
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
                ClusterItem info = new ClusterItem(id, title, time, source, tflag, originURL,
                        content, newsType, keyWordList.get(typeList.get(i)));
                data.add(info);
            }
            return data;
        }
    }
}

