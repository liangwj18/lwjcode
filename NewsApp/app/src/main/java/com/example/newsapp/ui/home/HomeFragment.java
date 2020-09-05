package com.example.newsapp.ui.home;

import android.os.Bundle;
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
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentManager fragmentManager;
    private ImageButton channelButton;

    private FragmentPagerItemAdapter adapter;
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

    private void initFragment() {
        Bundle bundleNews = new Bundle();
        bundleNews.putString("name", "NEWS");
        Bundle bundlePaper = new Bundle();
        bundlePaper.putString("name", "PAPERS");
        Bundle bundleNewTime = new Bundle();
        bundleNewTime.putString("name", "新时代");
        pagers = FragmentPagerItems.with(getContext())
                .add("ALL", NewsListFragment.class)
                .add("News", BlankFragment.class, bundleNews)
                .add("Paper", BlankFragment.class, bundlePaper)
                .add("NewTime", BlankFragment.class, bundleNewTime)
                .create();
    }

    private void findView(View root) {
        channelButton = root.findViewById(R.id.channel_button);
        pagerTab = root.findViewById(R.id.view_pager_tab);
        viewPager = root.findViewById(R.id.viewpager);
    }

    private void initView() {
        // 配置好Tab、viewPager和适配器
        adapter = new FragmentPagerItemAdapter(getChildFragmentManager(),pagers);
        viewPager.setAdapter(adapter);
        pagerTab.setViewPager(viewPager);
        // 为频道按钮增加监听，添加频道选择Fragment
        channelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CLICK", "LIST_F");
                channelFragment = (channelFragment != null) ? channelFragment : new ChannelFragment();
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
}