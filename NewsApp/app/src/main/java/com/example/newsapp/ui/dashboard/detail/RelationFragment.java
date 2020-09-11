package com.example.newsapp.ui.dashboard.detail;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;
import com.example.newsapp.ui.dashboard.GraphInfo;
import com.example.newsapp.ui.dashboard.RelationInfo;

import java.util.ArrayList;
import java.util.List;


public class RelationFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_NAME = "info";

    private GraphInfo mInfo;
    private RecyclerView recyclerView;
    private View root;
    private RelationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public RelationFragment() {
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
        root = inflater.inflate(R.layout.fragment_relation, container, false);
        findView(root);
        initView();
        return root;
    }

    private void findView(View root) {
        recyclerView = root.findViewById(R.id.relation_recycler_view);
    }

    private void loadData(){
        // 开启新线程初始化数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 解析数据
                final List<RelationInfo> relationInfoList = new ArrayList<>();
                JSONArray relarray = JSONObject.parseArray(mInfo.getRelations());
                for (int i = 0; i < relarray.size(); ++i) {
                    JSONObject obj = relarray.getJSONObject(i);
                    RelationInfo item = new RelationInfo(obj.getString("label"), obj.getString("url"),
                            obj.getString("relation"), obj.getBoolean("forward"));
                    relationInfoList.add(item);
                }
                // 通知UI
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(relationInfoList);
                    }
                });
            }
        }).start();
    }

    private void initView() {
        // 设置适配器
        adapter = new RelationAdapter(this, getContext());
        recyclerView.setAdapter(adapter);
        // 设置布局管理
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 初始化数据
        loadData();
    }

    @Override
    public void onClick(View view) {
        // TODO 点击跳转网页
    }
}