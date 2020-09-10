package com.example.newsapp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;
//import com.example.newsapp.ui.dashboard.GraphAdapter;
import com.example.newsapp.ui.dashboard.GraphInfo;
import com.example.newsapp.ui.dashboard.relationInfo;
import com.example.newsapp.utils.HttpsTrustManager;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class GraphDetailActivity extends AppCompatActivity {
    AVLoadingIndicatorView loadingIndicatorView;
    TextView detailTitle, detailDescription, detailTime, detailType, detailLink, detailContent;
    ImageView influence_icon;
    LinearLayout container;
    Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_detail);
        findView();     //找到所有的TextView
      //  loadingIndicatorView.show();
       // container.setVisibility(View.GONE);

        String Label=getIntent().getStringExtra("Label");
        String arg[]=new String[]{Label};
        ListView prolistview=findViewById(R.id.detail_properties),relationview=findViewById(R.id.detail_relations);
        ArrayList<String> datapro=new ArrayList<String>();
     //   ArrayList<relationInfo> datarel = new ArrayList<relationInfo>();

        List<GraphInfo> infolist = GraphInfo.find(GraphInfo.class,"Label = ?",arg);
        List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
        if (infolist.size()>=1)
        {
            GraphInfo info =infolist.get(0);
            String img=info.getImg();

            Picasso.get().load(img).into(influence_icon);

            // 下面构建子线程
            detailTitle.setText(info.getLabel());
            detailDescription.setText(info.getInfo());

            JSONObject proset=JSONObject.parseObject(info.getProperties());

            for (Map.Entry<String, Object> entry : proset.entrySet()) {

                datapro.add(entry.getKey()+"："+entry.getValue().toString());
            }
            JSONArray relarray=JSONObject.parseArray(info.getRelations());
            for (int i=0;i<relarray.size();++i)
            {
                JSONObject obj=relarray.getJSONObject(i);
                Map<String,Object> map=new HashMap<String, Object>();
                map.put("relation_label",obj.getString("relation"));
              //  map.put("url",obj.getString("url"));
                map.put("relation_name",obj.getString("label"));

                boolean f=obj.getBoolean("forward");
                int imgg=R.mipmap.forwand;
                if (!f) imgg=R.mipmap.backward;

                map.put("ward_icon",imgg);
                data.add(map);
               // datarel.add(new relationInfo(,,,
                 //       obj.getBoolean("forward")));
            }
        }
       // getIntent().getStringExtra("id");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datapro);//新建并配置ArrayAapeter
        prolistview.setAdapter(adapter);
        SimpleAdapter reladapter=new SimpleAdapter(this,data,R.layout.fragment_graph_list_item,
                new String[]{"relation_label","relation_name","ward_icon"},new int[]{R.id.relation_label,R.id.relation_name,R.id.ward_icon});
        relationview.setAdapter(reladapter);


/*
        detailInfo.setText(info.getInfo());
        detailRelation.setText(time);
        detailType.setText(type);
        detailContent.setText(content);
        String html = "<a href=\"" + originURL + "\">原文链接</a>";
        detailLink.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        detailLink.setMovementMethod(LinkMovementMethod.getInstance());
        loadingIndicatorView.hide();    //隐藏加载
        container.setVisibility(View.VISIBLE);  //显示内容
        GraphInfo.deleteAll(GraphInfo.class);
    }



        private void updateUI() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 设置图标
                    int randNum = new Random().nextInt(3);
                    switch (randNum){
                        case 0:
                            influence_icon.setImageDrawable(getDrawable(R.drawable.circle_strong));
                            break;
                        case 1:
                            influence_icon.setImageDrawable(getDrawable(R.drawable.circle_normal));
                            break;
                        case 2:
                            influence_icon.setImageDrawable(getDrawable(R.drawable.circle_weak));
                            break;
                    }
                    detailTitle.setText(title);
                    detailSource.setText(source);
                    detailTime.setText(time);
                    detailType.setText(type);
                    detailContent.setText(content);
                    String html = "<a href=\"" + originURL + "\">原文链接</a>";
                    detailLink.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
                    detailLink.setMovementMethod(LinkMovementMethod.getInstance());
                    loadingIndicatorView.hide();    //隐藏加载
                    container.setVisibility(View.VISIBLE);  //显示内容
                }
            }, 500);
        }*/
    }

    private void findView() {
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailTime = findViewById(R.id.detail_time);
        detailType = findViewById(R.id.detail_type);
        detailLink = findViewById(R.id.detail_link);
        detailContent = findViewById(R.id.detail_content);
        influence_icon = findViewById(R.id.influence_icon);

      //  container = findViewById(R.id.detail_content_layout);
     //   loadingIndicatorView = findViewById(R.id.detail_avi);
    }
}
