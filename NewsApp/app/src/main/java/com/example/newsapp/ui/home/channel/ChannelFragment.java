package com.example.newsapp.ui.home.channel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.newsapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChannelFragment extends Fragment {
    private ImageButton closeBtn;
    private RecyclerView recyclerView;
    ChannelAdapter mAdapter;
    private String select[] = {"News", "Paper", "新时代"};
    private String waitingList[] = {"汽车", "时尚", "国际", "电影", "财经", "游戏", "科技", "房产", "政务", "图片", "独家"};
    List<ChannelItem> list;
    Handler mHandler;

    public ChannelFragment() {
        // Required empty public constructor
    }

    public ChannelFragment(Handler handler) {
        mHandler = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_channel, container, false);
        findView(root);
        addCloseBtnListener();
        initView();
        return root;
    }

    private void findView(View root) {
        recyclerView = root.findViewById(R.id.channel_recycler_view);
        closeBtn = root.findViewById(R.id.channel_close_btn);
    }

    private void initView() {
        // 设置动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setMoveDuration(300);     //设置动画时间
        animator.setRemoveDuration(0);
        recyclerView.setItemAnimator(animator);
        // 初始化数据
        list = new ArrayList<>();
        for (String name : select) {
            list.add(new ChannelItem(name, 1, R.layout.channel_item));
        }
        ChannelItem divider = new ChannelItem();
        divider.setLayoutId(R.layout.channel_divider);
        divider.setSpanSize(4);
        list.add(divider);
        for (String name : waitingList) {
            list.add(new ChannelItem(name, 1, R.layout.channel_item));
        }
        // 新建适配器
        mAdapter = new ChannelAdapter(getContext(), list);
        mAdapter.setFixSize(1);
        mAdapter.setSelectedSize(select.length);
        recyclerView.setAdapter(mAdapter);
        // 设置网格布局器
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemSpanSize(position);
            }
        });
        recyclerView.setLayoutManager(manager);
        // 设置itemTouchHelper
        ChannelDragCallback callback = new ChannelDragCallback(mAdapter, 2, getContext().getColor(R.color.grey));
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    // 退出按钮
    public void addCloseBtnListener() {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager parentManager = getParentFragmentManager();
                FragmentTransaction transaction = parentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                transaction.hide(parentManager.findFragmentByTag("CHANNEL")).commit();
                Message msg = new Message();
                msg.obj = list.subList(0, mAdapter.getSelectedSize());
                mHandler.sendMessage(msg);
            }
        });
    }
}