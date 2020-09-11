package com.java.huangjialiang.ui.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.java.huangjialiang.R;
import com.java.huangjialiang.ui.home.channel.ChannelFragment;
import com.java.huangjialiang.ui.home.channel.ChannelItem;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    private HomeViewModel homeViewModel;
    private FragmentManager fragmentManager;
    private View root;
    private ImageButton channelButton;
    private MyPagerAdapter adapter;
    private FragmentPagerItems pagers;
    private SmartTabLayout pagerTab;
    private ViewPager viewPager;
    private ChannelFragment channelFragment;
    private MaterialSearchBar searchBar;

    private final String historyFileName = "search_history";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
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
                .add("All", NewsListFragment.class, listBundle)
                .add("News", NewsListFragment.class, newsBundle)
                .add("Paper", NewsListFragment.class, paperBundle)
                .add("新时代", BlankFragment.class)
                .create();
    }

    private void findView(View root) {
        channelButton = root.findViewById(R.id.channel_button);
        pagerTab = root.findViewById(R.id.view_pager_tab);
        viewPager = root.findViewById(R.id.viewpager);
        searchBar = root.findViewById(R.id.news_list_search_bar);
    }

    private void initView() {
        // 设置缓存大小
        viewPager.setOffscreenPageLimit(2);
        // 配置好Tab、viewPager和适配器
        adapter = new MyPagerAdapter(getChildFragmentManager(), pagers);
        viewPager.setAdapter(adapter);
        pagerTab.setViewPager(viewPager);
        // 为频道按钮增加监听，添加频道选择Fragment
        channelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelFragment = (channelFragment != null) ? channelFragment : new ChannelFragment(new RefreshHandler());
                FragmentManager manager = getParentFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
                if (!channelFragment.isAdded()) {
                    transaction.replace(android.R.id.content, channelFragment, "CHANNEL").addToBackStack("CHANNEL").commit();
                } else {
                    transaction.show(channelFragment).commit();
                }
            }
        });
        // 设置搜索框
        searchBar.setHint("请输入关键词");
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(this);
        //restore last queries from disk
        List<String> lastSearches = loadSearchSuggestionFromDisk();
        if (lastSearches != null)
            searchBar.setLastSuggestions(lastSearches);
    }

    private List<String> loadSearchSuggestionFromDisk() {
        try {
            FileInputStream fileInputStream = getContext().openFileInput(historyFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return JSON.parseArray(builder.toString(), String.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<String> history = searchBar.getLastSuggestions();
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput(historyFileName, Context.MODE_PRIVATE);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(JSON.toJSONString(history));
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onSearchStateChanged(boolean enabled) {
        // 取消搜索时 DO NOTHING
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchBar.closeSearch();
        if (!isOnline()) {
            // 没有联网的话
            Toast.makeText(getContext(), "未联网无法启用搜索", Toast.LENGTH_LONG).show();
            return;
        }
        SearchResFragment searchResFragment = SearchResFragment.newInstance(text.toString());
        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.search_in, R.anim.search_out);
        transaction.replace(android.R.id.content, searchResFragment, "SEARCH").addToBackStack("SEARCH").commit();
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.closeSearch();
                break;
        }
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
            adapter.notifyChangeInPosition(1);
            adapter.notifyDataSetChanged();
            pagerTab.setViewPager(viewPager);
        }
    }
}