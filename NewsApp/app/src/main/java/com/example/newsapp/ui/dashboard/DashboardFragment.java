package com.example.newsapp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;
import com.example.newsapp.ui.dashboard.cluster.ClusterFragment;
import com.example.newsapp.ui.dashboard.search.EntitySearchFragment;
import com.example.newsapp.ui.home.MyPagerAdapter;
import com.example.newsapp.utils.HttpsTrustManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DashboardFragment extends Fragment {


    private FragmentManager fragmentManager;

    private MyPagerAdapter adapter;
    private FragmentPagerItems pagers;
    private SmartTabLayout pagerTab;
    private ViewPager viewPager;
    private RelativeLayout relativeLayout;

    DetailHelper detailHelper;
    private DashboardViewModel dashboardViewModel;


    private class DetailHelper implements Runnable {
        private String urlString;

        public DetailHelper(String url) {
            this.urlString = url;
        }

        @Override
        public void run() {
            if (CountyInfo.count(CountyInfo.class, null, null,
                    null, null, "1") == 0) {
                loadingData();
                Log.i("CHART", "LOADING");
            } else {
                Log.i("CHART", "NO_LOADING");
            }
            updateUI();
        }

        private void loadingData() {
            try {
                URL url = new URL(urlString);
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
                for (Map.Entry<String, Object> entry : jsonSet.entrySet()) {
                    String address = entry.getKey();
                    String[] address_list = address.split("\\|");
                    String Country = address_list[0];
                    String Province = "";
                    if (address_list.length > 1) Province = address_list[1];
                    String County = "";
                    if (address_list.length > 2) County = address_list[2];
                    System.out.println(address_list[0].length());

                    JSONObject jsonSet2 = (JSONObject) entry.getValue();
                    String Begin_Time = jsonSet2.getString("begin");
                    JSONArray array = jsonSet2.getJSONArray("data");
                    String dayinfo = array.toString();

                    CountyInfo countyInfo = new CountyInfo(Country, Province, County, Begin_Time, dayinfo);
                    System.out.println(Country);
                    countyInfo.save();
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
                    // 开始加载页面
                    initView();
                    // 隐藏加载
                    relativeLayout.setVisibility(View.GONE);
                }
            }, 500);
        }
    }

    private static Bundle getBundle(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        return bundle;
    }

    private void initFragment() {
        Bundle listBundle = getBundle("世界疫情");
        Bundle newsBundle = getBundle("国内疫情");
        Bundle clusterBundle = getBundle("聚类分析");
        pagers = FragmentPagerItems.with(getContext())
                .add("世界疫情", WorldFragment.class, listBundle)
                .add("国内疫情", DomesticFragment.class, newsBundle)
                .add("疫情图谱", EntitySearchFragment.class)
                .add("聚类分析", ClusterFragment.class, clusterBundle)
                .create();
    }

    public static int myParseInt(String st) {
        int ret = -1;
        if (st.equals("null")) return ret;
        else return Integer.parseInt(st);
    }

    public static void MyDrawChart(View root, String datas) {
        List<County_Day_Info> day_infoList = new ArrayList<County_Day_Info>();
        JSONArray array = JSON.parseArray(datas);
        for (int i = 0; i < array.size(); ++i) {
            int sum_days = i + 1;
            String dataArr = array.get(i).toString();
            dataArr = dataArr.substring(1, dataArr.length() - 1);
            String[] datalist = dataArr.split(",");

            int confirmed = myParseInt(datalist[0]);
            int suspected = myParseInt(datalist[1]);
            int cured = myParseInt(datalist[2]);
            int dead = myParseInt(datalist[3]);
            int severe = myParseInt(datalist[4]);
            int risk = myParseInt(datalist[5]);
            int inc24 = myParseInt(datalist[6]);

            County_Day_Info day_info = new County_Day_Info(confirmed, suspected, cured, dead, severe, risk, inc24);
            day_infoList.add(day_info);
        }

        LineChart chart = (LineChart) (root.findViewById(R.id.mLineChar));
        Description description = new Description();//描述信息
        description.setText("疫情发生天数");
        description.setEnabled(true);//是否可用
        chart.setDescription(description);//不然会显示默认的 Description。
        chart.setTouchEnabled(true); // 设置是否可以触摸
        chart.setDragEnabled(true);// 是否可以拖拽
        chart.setScaleEnabled(true);// 是否可以缩放
        chart.setPinchZoom(false);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
        chart.setDoubleTapToZoomEnabled(true);//是否允许双击进行缩放
        chart.setScaleXEnabled(false);//是否允许以X轴缩放
        chart.setDrawGridBackground(false);// 是否显示表格颜色
        chart.setGridBackgroundColor(Color.TRANSPARENT);// 表格的的颜色
        chart.animateY(1000, Easing.Linear);//设置动画
        chart.setExtraBottomOffset(5f);//防止底部数据显示不完整，设置底部偏移量
//x轴配置
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);//是否可用
        xAxis.setDrawLabels(true);//是否显示数值
        xAxis.setDrawAxisLine(true);//是否显示坐标线
        xAxis.setAxisLineColor(Color.BLACK);//设置坐标轴线的颜色
        xAxis.setAxisLineWidth(0.8f);//设置坐标轴线的宽度
        xAxis.setDrawGridLines(false);//是否显示竖直风格线
        xAxis.setTextColor(Color.BLACK);//X轴文字颜色
        xAxis.setTextSize(12f);//X轴文字大小
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴文字显示位置
        xAxis.setSpaceMin(.4f);//左空白区大小
        xAxis.setSpaceMax(.4f);//右空白区大小
//左y轴配置
        YAxis lyAxis = chart.getAxisLeft();
        lyAxis.setEnabled(true);//是否可用
        lyAxis.setDrawLabels(true);//是否显示数值
        lyAxis.setDrawAxisLine(false);//是否显示坐标线
        lyAxis.setDrawGridLines(true);//是否显示水平网格线
        lyAxis.setDrawZeroLine(true);////是否绘制零线
        lyAxis.setZeroLineColor(Color.BLACK);
        lyAxis.setZeroLineWidth(0.8f);
        lyAxis.enableGridDashedLine(10f, 10f, 0f);//网格虚线
        lyAxis.setGridColor(Color.BLACK);//网格线颜色
        lyAxis.setGridLineWidth(0.8f);//网格线宽度
        lyAxis.setAxisLineColor(Color.BLACK);//坐标线颜色
        lyAxis.setTextColor(Color.BLACK);//左侧文字颜色
        lyAxis.setTextSize(12f);//左侧文字大小
        lyAxis.setAxisMinimum(-0.0f);
//右y轴配置
        YAxis ryAxis = chart.getAxisRight();
        ryAxis.setEnabled(false);//是否可用
//标签配置
        Legend legend = chart.getLegend();
        legend.setEnabled(true);//是否可用

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        List<Entry> entriesConfirmed = new ArrayList<Entry>();
        List<Entry> entriesCured = new ArrayList<Entry>();
        List<Entry> entriesDead = new ArrayList<Entry>();
        for (int i = 0; i < day_infoList.size(); ++i) {
            entriesConfirmed.add(new Entry(i + 1, day_infoList.get(i).getConfirmed()));
            entriesCured.add(new Entry(i + 1, day_infoList.get(i).getCured()));
            entriesDead.add(new Entry(i + 1, day_infoList.get(i).getDead()));
        }

        float radius = 1f;

        LineDataSet d1 = new LineDataSet(entriesConfirmed, "Confirmed");
        d1.setLineWidth(2.5f);
        d1.setColor(Color.parseColor("#3498db"));
        d1.setDrawCircles(false);
        d1.setCubicIntensity(0.2f);
        d1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d1.setDrawFilled(true);
        d1.setFillColor(Color.parseColor("#3498db"));
        dataSets.add(d1);

        LineDataSet d2 = new LineDataSet(entriesCured, "Cured");
        d2.setLineWidth(2.5f);
        d2.setDrawCircles(false);
        d2.setColor(Color.parseColor("#2ecc71"));
        d2.setCircleColor(Color.parseColor("#2ecc71"));
        d2.setCubicIntensity(0.2f);
        d2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d2.setDrawFilled(true);
        d2.setFillColor(Color.parseColor("#2ecc71"));
        dataSets.add(d2);

        LineDataSet d3 = new LineDataSet(entriesDead, "Dead");
        d3.setLineWidth(2.5f);
        d3.setDrawCircles(false);
        d3.setColor(Color.parseColor("#e74c3c"));
        d3.setCircleColor(Color.parseColor("#e74c3c"));
        d3.setCubicIntensity(0.2f);
        d3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d3.setDrawFilled(true);
        d3.setFillColor(Color.parseColor("#e74c3c"));
        d3.setFillAlpha(200);
        dataSets.add(d3);

        LineData linedata = new LineData(dataSets);
        chart.setData(linedata);
        chart.invalidate();
    }


    private void findView(View root) {
        pagerTab = root.findViewById(R.id.view_pager_tab);
        viewPager = root.findViewById(R.id.viewpager);
        relativeLayout = root.findViewById(R.id.chart_loading_layout);
    }

    private void initView() {
        // 设置缓存大小
        viewPager.setOffscreenPageLimit(3);
        // 配置好Tab、viewPager和适配器
        adapter = new MyPagerAdapter(getChildFragmentManager(), pagers);
        viewPager.setAdapter(adapter);
        pagerTab.setViewPager(viewPager);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        fragmentManager = getChildFragmentManager();
        findView(root);
        initFragment();
        // 显示加载页面
        relativeLayout.setVisibility(View.VISIBLE);
        detailHelper = new DetailHelper(getString(R.string.epidemic_data));
        new Thread(detailHelper).start();

        return root;
    }
}