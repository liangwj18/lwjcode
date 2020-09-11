package com.java.huangjialiang.ui.dashboard.detail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.java.huangjialiang.R;
import com.java.huangjialiang.ui.dashboard.GraphInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PropertyFragment extends Fragment {
    private static final String ARG_NAME = "info";
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    View root;
    GraphInfo mInfo;

    public PropertyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInfo = (GraphInfo) getArguments().getSerializable(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_property, container, false);
        findView(root);
        initView();
        return root;
    }

    private void findView(View root) {
        recyclerView = root.findViewById(R.id.property_recycler_view);
    }

    private void loadData(){
        // 开启新线程初始化数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 解析数据
                final List<String> key = new ArrayList<>();
                final List<String> property = new ArrayList<>();
                JSONObject proset = JSONObject.parseObject(mInfo.getProperties());
                for (Map.Entry<String, Object> entry : proset.entrySet()) {
                    key.add(entry.getKey());
                    property.add(entry.getValue().toString());
                }
                // 通知UI
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(key, property);
                    }
                });
            }
        }).start();
    }

    private void initView() {
        // 初始化recyclerview的adapter
        adapter = new PropertyAdapter(mInfo, getContext());
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 加载属性数据
        loadData();
    }
}