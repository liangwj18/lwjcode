package com.example.newsapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class HomeFragment extends Fragment {

    final int INIT_NUM = 8;
    final int UPDATE_NUM = 5;

    private HomeViewModel homeViewModel;
    private PtrFrameLayout mPtrFrame;
    private MaterialHeader materialHeader;
    private RecyclerView recyclerView;
    private JSONArray jsonArray;
    private NewsAdapter mAdapter;
    int lastVisiableItem;
    int loadedNumber = 0;
    boolean isLoading = false;      // 表明当前是否正在加载
    boolean isScrollDown = false;   //表明是否正在下拉

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        findView(root);     //初始化PtrFrame和recycleView
        // TODO 写死到了主进程中，应该考虑让子线程去读取
        String jsonString = JsonReader.getJson(getActivity(), "lists.json");
        jsonArray = JSONObject.parseObject(jsonString).getJSONArray("datas");
        initView();     //初始化界面
        return root;
    }

    //初始化PtrFrame和recycleView
    private void findView(View root) {
        mPtrFrame = root.findViewById(R.id.ptrFrameLayout);
        recyclerView = root.findViewById(R.id.recyclerView);
    }

    //上拉加载更多时返回数据
    private void getData() {
        ArrayList<NewsInfo> data = null;
        if (loadedNumber < jsonArray.size()) {
            int number = Math.min(jsonArray.size() - loadedNumber, UPDATE_NUM);
            data = new ArrayList<>(number);
            for (int i = 0; i < number; ++i) {
                data.add(JsonReader.getNewsInfo(jsonArray.getJSONObject(loadedNumber)));
                loadedNumber++;
            }
        }
        mAdapter.updateData(data);
        isLoading = false;
    }


    private void initView() {
        // 首先加载一些数据(10条)
        ArrayList<NewsInfo> news = new ArrayList<>(INIT_NUM);
        for (int i = 0; i < INIT_NUM; ++i) {
            JSONObject item = jsonArray.getJSONObject(i);
            news.add(JsonReader.getNewsInfo(item));
            loadedNumber++;
        }
        // 初始化RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new NewsAdapter(news);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem == mAdapter.getItemCount() - 1 && !isLoading && isScrollDown) {
                    isLoading = true;
                    if (loadedNumber < jsonArray.size()) {
                        mAdapter.changeState(NewsAdapter.LoadingType.LOADING_MORE);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                            }
                        }, 1000);
                    } else {
                        mAdapter.changeState(NewsAdapter.LoadingType.NO_MORE);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiableItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.i("DY", Integer.toString(dy));
                if (dy > 0)
                    isScrollDown = true;
                else
                    isScrollDown = false;
            }
        });
    }
}