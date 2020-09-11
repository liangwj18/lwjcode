package com.java.huangjialiang;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.java.huangjialiang.ui.dashboard.GraphInfo;
import com.java.huangjialiang.ui.dashboard.detail.PropertyFragment;
import com.java.huangjialiang.ui.dashboard.detail.RelationFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class GraphDetailActivity extends AppCompatActivity {
    TextView graphTitleTv;
    SmartTabLayout tabLayout;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_detail);
        findView();     //找到所有的TextView
        initView();
        // 1.有两个信息，一个是标题，一个是内容
        // 2.之后的properties是键值对
        // 3.最后的关系内容，是一个relation_label + relation_name
    }

    private void findView() {
        graphTitleTv = findViewById(R.id.graph_detail_title);
        tabLayout = findViewById(R.id.graph_pager_tab);
        viewPager = findViewById(R.id.graph_view_pager);
    }

    private void initView(){
        // 先获得数据
        GraphInfo info = (GraphInfo) getIntent().getSerializableExtra("info");
        // 设置标题
        graphTitleTv.setText(info.getLabel());
        // 初始化两个fragment，并传入info
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", info);
        FragmentPagerItems pages = FragmentPagerItems.with(this)
                .add("PROPERTY", PropertyFragment.class, bundle)
                .add("RELATION", RelationFragment.class, bundle)
                .create();
        // 初始化tab
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);
    }
}
