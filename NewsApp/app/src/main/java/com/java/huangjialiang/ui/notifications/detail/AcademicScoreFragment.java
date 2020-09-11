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
import com.java.huangjialiang.ui.notifications.IndicesInfo;
import com.java.huangjialiang.ui.notifications.PersonInfo;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class AcademicScoreFragment extends Fragment {

    private static final String ARG_PARAM = "id";

    private String id;
    private AcademicScoreHelper helper;
    private View root;
    private Handler mHandler;
    private AcademicScoreAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AVLoadingIndicatorView loadingIndicatorView;
    private RelativeLayout relativeLayout;  // 动画的容器

    public AcademicScoreFragment() {
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
        helper = new AcademicScoreHelper(id);
        // 设置handler
        mHandler = new Handler(Looper.getMainLooper());
        // 设置recyclerview和adapter
        relativeLayout.setVisibility(View.VISIBLE);
        loadingIndicatorView.show();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AcademicScoreAdapter(getContext());
        recyclerView.setAdapter(adapter);
        // 加载内容
        loadData();
    }

    // 内部类，用于网络通信
    private class AcademicScoreHelper implements Runnable {
        private String targetID;
        // 下面是人物的内容
        private IndicesInfo indices;
        private List<String> tags, tagsScore;

        public AcademicScoreHelper(String targetID) {
            this.targetID = targetID;
        }

        @Override
        public void run() {
            // 查看数据库
            List<PersonInfo> targetList = PersonInfo.find(PersonInfo.class, "my_id = ?", targetID);
            if (targetList.size() != 0) {
                PersonInfo target = targetList.get(0);
                indices = target.getIndices();
                tags = parseTags(target.getTags());
                tagsScore = parseTagsScore(target.getTags_score());
            }
            // 把数据提出来
            fillIndicesList();
            // 更新UI
            updateUI();
        }

        private void fillIndicesList() {
            tags.add("学术活跃度");
            tags.add("引用数");
            tags.add("多样性");
            tags.add("g指数");
            tags.add("h指数");
            tags.add("newStar");
            tags.add("risingStar");
            tags.add("发表数");
            tags.add("社会性");
            tagsScore.add(Float.toString(indices.getActivity()));
            tagsScore.add(Integer.toString(indices.getCitations()));
            tagsScore.add(Float.toString(indices.getDiveristy()));
            tagsScore.add(Integer.toString(indices.getGindex()));
            tagsScore.add(Integer.toString(indices.getHindex()));
            tagsScore.add(Float.toString(indices.getNewStar()));
            tagsScore.add(Float.toString(indices.getRisingStar()));
            tagsScore.add(Integer.toString(indices.getPubs()));
            tagsScore.add(Float.toString(indices.getSociability()));
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
        private List<String> parseTagsScore(String tagsScore) {
            List<String> res = new ArrayList<>();
            JSONArray array = JSONObject.parseArray(tagsScore);
            for (int i = 0; i < array.size(); ++i) {
                res.add(array.getString(i));
            }
            return res;
        }

        private void updateUI() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.updateData(tags, tagsScore);
                    loadingIndicatorView.hide();    //隐藏加载
                    relativeLayout.setVisibility(View.GONE);
                }
            }, 500);
        }
    }
}
