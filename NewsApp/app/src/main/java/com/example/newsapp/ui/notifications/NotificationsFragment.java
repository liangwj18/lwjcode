package com.example.newsapp.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;
import com.example.newsapp.ui.home.MyPagerAdapter;
import com.example.newsapp.utils.HttpsTrustManager;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NotificationsFragment extends Fragment {

    private FragmentPagerItemAdapter adapter;
    private FragmentPagerItems pagers;
    private SmartTabLayout pagerTab;
    private ViewPager viewPager;
    private RelativeLayout loadingLayout;
    private DetailHelper detailHelper;

    private NotificationsViewModel notificationsViewModel;

    private static Bundle getBundle(String st) {
        Bundle bundle = new Bundle();
        bundle.putString("type", st);
        return bundle;
    }

    private void findView(View root) {
        pagerTab = root.findViewById(R.id.person_page_tab);
        viewPager = root.findViewById(R.id.person_view_pager);
        loadingLayout = root.findViewById(R.id.person_loading_layout);
    }

    private void initView() {
        // 设置缓存大小
        viewPager.setOffscreenPageLimit(2);
        // 设置好pagers
        Bundle listBundle = getBundle("false");
        Bundle newsBundle = getBundle("true");
        pagers = FragmentPagerItems.with(getContext())
                .add("高关注学者", PersonFragment.class, listBundle)
                .add("追忆学者", PersonFragment.class, newsBundle)
                .create();
        // 配置好Tab、viewPager和适配器
        adapter = new FragmentPagerItemAdapter(getChildFragmentManager(), pagers);
        viewPager.setAdapter(adapter);
        pagerTab.setViewPager(viewPager);
    }

    private void loadData(){
        // 显示加载页面，开始数据加载
        loadingLayout.setVisibility(View.VISIBLE);
        detailHelper = new NotificationsFragment.DetailHelper(getString(R.string.expert_data));
        new Thread(detailHelper).start();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        findView(root);
        loadData();
        return root;
    }

    private class DetailHelper implements Runnable {
        private String urlString;

        public DetailHelper(String url) {
            this.urlString = url;
        }

        @Override
        public void run() {
            if (PersonInfo.count(PersonInfo.class, null, null,
                    null, null, "1") == 0) {
                // 如果数据库有数据就加载
                loadingData();
                Log.i("CHART", "LOADING");
            } else {
                Log.i("CHART", "NO_LOADING");
            }
            updateUI();
        }

        private IndicesInfo parseIndices(JSONObject obj) {
            // 解析indices
            float activity = obj.getFloat("activity");
            int citations = obj.getIntValue("citations");
            float diversity = obj.getFloat("diversity");
            int gindex = obj.getIntValue("gindex");
            int hindex = obj.getIntValue("hindex");
            float newStar = obj.getFloat("newStar");
            int pubs = obj.getIntValue("pubs");
            float risingStar = obj.getFloat("risingStar");
            float sociability = obj.getFloat("sociability");

            IndicesInfo indicesInfo = new IndicesInfo(activity, citations, diversity, gindex, hindex, newStar, pubs, risingStar, sociability);
            return indicesInfo;
        }

        private ProfileInfo parseProfile(JSONObject obj) {
            // 解析profile
            String addres = obj.getString("address");
            String affiliation = obj.getString("affiliation");
            String affiliation_zh = obj.getString("affiliation_zh");
            String bio = obj.getString("bio");
            String edu = obj.getString("edu");
            String email = obj.getString("email");
            String email_cr = obj.getString("email_cr");

            System.out.println(obj.toString());
            String emails_u = "";
            if (obj.getJSONArray("emails_u") != null)
                emails_u = obj.getJSONArray("emails_u").toJSONString();
            System.out.println(emails_u);
            String fax = obj.getString("fax");
            String homepage = obj.getString("homepage");
            String note = obj.getString("note");
            String phone = obj.getString("phone");
            String position = obj.getString("position");
            String work = obj.getString("work");
            ProfileInfo profileInfo = new ProfileInfo(addres, affiliation, affiliation_zh,
                    bio, edu, email, email_cr, emails_u, fax, homepage, note, phone, position, work);
            return profileInfo;
        }

        private void loadingData() {
            try {
                URL url = new URL(urlString);
                System.out.println(urlString);
                HttpsTrustManager.allowAllSSL();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setAllowUserInteraction(false);
                connection.setInstanceFollowRedirects(true);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                // 发起请求
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("HTTPS", "[NewsDetailActivity line 80] NOT OK");
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();
                connection.disconnect();
                // 读取JSON
                JSONObject jsonSet = JSONObject.parseObject(builder.toString());
                JSONArray array = jsonSet.getJSONArray("data");
                for (int i = 0; i < array.size(); ++i) {
                    JSONObject obj = array.getJSONObject(i);
                    String Avatar = obj.getString("avatar");
                    boolean bind = obj.getBoolean("bind");
                    String myID = obj.getString("id");

                    IndicesInfo indicesInfo = parseIndices(obj.getJSONObject("indices"));
                    String name = obj.getString("name");
                    String name_zh = obj.getString("name_zh");

                    int num_followed = obj.getInteger("num_followed");
                    int num_viewed = obj.getInteger("num_viewed");
                    ProfileInfo profileInfo = parseProfile(obj.getJSONObject("profile"));
                    int score = obj.getInteger("score");
                    String sourcetype = obj.getString("sourcetype");
                    String tags = "";
                    if (obj.getJSONArray("tags") != null)
                        tags = obj.getJSONArray("tags").toJSONString();
                    String tags_score = "";
                    if (obj.getJSONArray("tags_score") != null)
                        tags_score = obj.getJSONArray("tags_score").toJSONString();
                    int index = obj.getInteger("index");
                    int tab = obj.getInteger("tab");
                    boolean is_passedaway = obj.getBoolean("is_passedaway");
                    String isPassedaway = "false";
                    if (is_passedaway) isPassedaway = "true";
                    indicesInfo.save();
                    profileInfo.save();
                    PersonInfo personInfo = new PersonInfo(Avatar, bind, myID, indicesInfo, name, name_zh,
                            num_followed, num_viewed, profileInfo, score, sourcetype, tags,
                            tags_score, index, tab, isPassedaway);
                    personInfo.save();
                }
                updateUI();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void updateUI() {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 隐藏加载
                    initView();
                    loadingLayout.setVisibility(View.GONE);
                }
            }, 500);
        }
    }

}