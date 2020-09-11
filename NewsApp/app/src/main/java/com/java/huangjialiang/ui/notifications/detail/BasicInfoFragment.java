package com.java.huangjialiang.ui.notifications.detail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.huangjialiang.R;
import com.java.huangjialiang.ui.notifications.PersonInfo;
import com.java.huangjialiang.ui.notifications.ProfileInfo;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class BasicInfoFragment extends Fragment {

    private static final String ARG_PARAM = "id";

    private String id;
    private BasicInfoHelper helper;
    private View root;
    private Handler mHandler;
    private BasicInfoAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private RelativeLayout relativeLayout;  // 动画的容器

    public BasicInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_basic_info, container, false);
        findView(root);
        initView();
        return root;
    }

    private void findView(View root) {
        recyclerView = root.findViewById(R.id.basic_info_recycler_view);
        loadingIndicatorView = root.findViewById(R.id.basic_info_avi);
        relativeLayout = root.findViewById(R.id.basic_info_avi_container);
    }

    private void loadData() {
        new Thread(helper).start();
    }

    private void initView() {
        // 设置helper
        helper = new BasicInfoHelper(id);
        // 设置handler
        mHandler = new Handler(Looper.getMainLooper());
        // 设置recyclerview和adapter
        relativeLayout.setVisibility(View.VISIBLE);
        loadingIndicatorView.show();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BasicInfoAdapter(getContext());
        recyclerView.setAdapter(adapter);
        // 加载内容
        loadData();
    }

    // 内部类，用于网络通信
    private class BasicInfoHelper implements Runnable {
        private String targetID;
        // 下面是人物的内容
        private String Avatar;
        private String name, nameZH;
        private ProfileInfo profileInfo;
        List<String> profileKey, profileValue;

        public BasicInfoHelper(String targetID) {
            this.targetID = targetID;
        }

        @Override
        public void run() {
            // 查看数据库
            List<PersonInfo> targetList = PersonInfo.find(PersonInfo.class, "my_id = ?", targetID);
            if (targetList.size() != 0) {
                PersonInfo target = targetList.get(0);
                Avatar = target.getAvatar();
                name = target.getName();
                nameZH = target.getName_zh();
                profileInfo = target.getProfile_info();
            }
            // 更新UI
            // 把数据提出来
            fillProfileList();
            updateUI();
        }


        private void fillProfileList() {
            profileKey = new ArrayList<>();
            profileValue = new ArrayList<>();
            String value = profileInfo.getAffiliation();
            if (value != null && value.length() > 0) {
                profileKey.add("所属");
                profileValue.add(value);
            }
            value = profileInfo.getAffiliation_zh();
            if (value != null && value.length() > 0) {
                profileKey.add("所属(zh)");
                profileValue.add(value);
            }
            value = profileInfo.getPosition();
            if (value != null && value.length() > 0) {
                profileKey.add("职位");
                profileValue.add(value);
            }
            value = profileInfo.getBio();
            if (value != null && value.length() > 0) {
                profileKey.add("生平");
                profileValue.add(value);
            }
            value = profileInfo.getEdu();
            if (value != null && value.length() > 0) {
                profileKey.add("教育经历");
                profileValue.add(value);
            }
            value = profileInfo.getWork();
            if (value != null && value.length() > 0) {
                profileKey.add("工作经历");
                profileValue.add(value);
            }
        }

        // 开始解析json
        private List<String> parseTags(String tags) {
            List<String> res = new ArrayList<>();
            JSONArray array = JSONObject.parseArray(tags);
            for (int i = 0; i < array.size(); ++i) {
                res.add(array.getString(i));
            }
            return res;
        }

        // 开始解析json
        private List<Integer> parseTagsScore(String tagsScore) {
            List<Integer> res = new ArrayList<>();
            JSONArray array = JSONObject.parseArray(tagsScore);
            for (int i = 0; i < array.size(); ++i) {
                res.add(array.getInteger(i));
            }
            return res;
        }

        private void updateUI() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.updateData(name, nameZH, Avatar, profileKey, profileValue);
                    loadingIndicatorView.hide();    //隐藏加载
                    relativeLayout.setVisibility(View.GONE);
                }
            }, 500);
        }
    }
}