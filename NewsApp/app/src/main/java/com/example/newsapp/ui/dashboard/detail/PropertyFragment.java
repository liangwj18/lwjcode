package com.example.newsapp.ui.dashboard.detail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;
import com.example.newsapp.ui.dashboard.GraphInfo;
import com.squareup.picasso.Picasso;

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
            Log.i("Property", mInfo.getLabel() + "\n" + mInfo.getProperties());
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

    private void initView() {
        // TODO 开新线程初始化，尤其是图片
        // 加载属性
        List<String> key = new ArrayList<>();
        List<String> property = new ArrayList<>();
        JSONObject proset = JSONObject.parseObject(mInfo.getProperties());
        for (Map.Entry<String, Object> entry : proset.entrySet()) {
            key.add(entry.getKey());
            property.add(entry.getValue().toString());
        }
        // 初始化recyclerview的adapter
        adapter = new PropertyAdapter(mInfo, getContext());
        recyclerView.setAdapter(adapter);
        adapter.updateData(key, property);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}