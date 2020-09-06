package com.example.newsapp.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.newsapp.R;
import com.example.newsapp.ui.home.channel.ChannelFragment;
import com.example.newsapp.ui.home.channel.ChannelItem;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentManager fragmentManager;
    private ImageButton channelButton;

    private MyPagerAdapter adapter;
    private FragmentPagerItems pagers;
    private SmartTabLayout pagerTab;
    private ViewPager viewPager;
    private ChannelFragment channelFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentManager = getChildFragmentManager();
        findView(root);
        initFragment();
        initView();
        return root;
    }

    private static Bundle getBundle(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        return bundle;
    }

    private void initFragment() {
        Bundle listBundle = getBundle("all");
        Bundle newsBundle = getBundle("news");
        Bundle paperBundle = getBundle("paper");
        pagers = FragmentPagerItems.with(getContext())
                .add("ALL", NewsListFragment.class, listBundle)
                .add("News", NewsListFragment.class, newsBundle)
                .add("Paper", NewsListFragment.class, paperBundle)
                .add("新时代", BlankFragment.class)
                .create();
    }

    private void findView(View root) {
        channelButton = root.findViewById(R.id.channel_button);
        pagerTab = root.findViewById(R.id.view_pager_tab);
        viewPager = root.findViewById(R.id.viewpager);
    }

    private void initView() {
        // 设置缓存大小
        viewPager.setOffscreenPageLimit(4);
        // 配置好Tab、viewPager和适配器
        adapter = new MyPagerAdapter(getChildFragmentManager(), pagers);
        viewPager.setAdapter(adapter);
        pagerTab.setViewPager(viewPager);
        // 为频道按钮增加监听，添加频道选择Fragment
        channelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CLICK", "LIST_F");
                channelFragment = (channelFragment != null) ? channelFragment : new ChannelFragment(new RefreshHandler());
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                if (!channelFragment.isAdded()) {
                    transaction.replace(android.R.id.content, channelFragment, "CHANNEL").commit();
                } else {
                    transaction.show(channelFragment).commit();
                }
            }
        });
    }

    private class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /* 增加或删除tab */
            adapter.removeAllViews(viewPager);
            List<ChannelItem> selected = (List<ChannelItem>) (List) msg.obj;
            for (ChannelItem item : selected) {
                switch (item.getName()) {
                    case "News":
                        adapter.addItems(FragmentPagerItem.of(item.getName(), NewsListFragment.class, getBundle("news")));
                        break;
                    case "Paper":
                        adapter.addItems(FragmentPagerItem.of(item.getName(), NewsListFragment.class, getBundle("paper")));
                        break;
                    default:
                        adapter.addItems(FragmentPagerItem.of(item.getName(), BlankFragment.class));
                }
            }
            adapter.notifyDataSetChanged();
            pagerTab.setViewPager(viewPager);
        }
    }
}