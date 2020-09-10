package com.example.newsapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.ClusterKeywordActivity;
import com.example.newsapp.GraphDetailActivity;
import com.example.newsapp.R;
import com.example.newsapp.ui.home.NewsInfo;
import com.example.newsapp.utils.HttpsTrustManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class ClusterFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";

    private LinearLayoutManager linearLayoutManager;
    private Handler mHandler;
    private ListView listView;

    private   DetailHelper detailHelper;

    // TODO: Rename and change types of parameters
    private String name;

    public ClusterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClusterFragment newInstance(String param1) {
        ClusterFragment fragment = new ClusterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_cluster_channel, container, false);
        TextView textView=root.findViewById(R.id.text_cluster_channel);


        listView = root.findViewById(R.id.cluster_listView);//在视图中找到ListView
        ArrayList<String> data=new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,data);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);

        detailHelper=new DetailHelper();
        new Thread(detailHelper).start();


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final List<String> list = detailHelper.getKeyword_ch_list();
                adapter.clear();
                for (int i = 0; i < list.size(); ++i) {
                    adapter.add(list.get(i));
                }
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                        Intent intent = new Intent(getActivity(), ClusterKeywordActivity.class);
                        intent.putExtra("type",list.get(i));
                        startActivity(intent);

                    }
                });
            }
        }, 1500);


                   /* if(findList.size() == 0)
                    {
                        Toast.makeText(MainActivity.this, "查找的商品不在列表中", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "查找成功", Toast.LENGTH_SHORT).show();
                        findAdapter = new listViewAdapter(MainActivity.this, findList);
                        listView.setAdapter(findAdapter);
                    }*/


        return root;
    }

    private class DetailHelper implements Runnable {

        private ArrayList<String> keyword_ch_list;
        private ArrayList<Integer> type_list;
        private List<NewsInfo> news_list;

        public ArrayList<String> getKeyword_ch_list() {
            return keyword_ch_list;
        }

        private List<NewsInfo> parseJson(JSONArray json) {
            List<NewsInfo> data = new ArrayList<>();
            for (int i = 0; i < json.size(); ++i) {
                JSONObject item = json.getJSONObject(i);
                String content = item.getString("content");
                String time = item.getString("time");
                long tflag = item.getLongValue("tflag");
                String title = item.getString("title");
                String source = item.getString("source");
                source = (source != null) ? source : "未知来源";
                JSONArray originarr = item.getJSONArray("urls");
                String originURL = (originarr.size() > 0) ? originarr.getString(0) : "未知URL";
                String id = item.getString("_id");
                String newsType = item.getString("type");
                NewsInfo info = new NewsInfo(id, title, time, source, tflag, originURL,
                        content, newsType,keyword_ch_list.get(type_list.get(i)));
                info.save();
                data.add(info);
            }
            return data;
        }
        @Override
        public void run() {
            try {

                Scanner scanner = new Scanner(getResources().openRawResource(R.raw.keyword_ch));
                try {
                    String st = scanner.nextLine();
                    String s[] = st.split(" ");
                    keyword_ch_list = new ArrayList<String>();
                    for (int i = 0; i < s.length; ++i)
                        keyword_ch_list.add(s[i]);

                } catch (Exception e) {

                }

                scanner = new Scanner(getResources().openRawResource(R.raw.type));
                try {
                    type_list = new ArrayList<Integer>();
                    while (scanner.hasNext()) {
                        int num = Integer.parseInt(scanner.next());
                        type_list.add(num);
                    }
                } catch (Exception e) {

                }

                StringBuilder builder = new StringBuilder();
                scanner = new Scanner(getResources().openRawResource(R.raw.events));
                try {
                    while (scanner.hasNextLine()) {
                        builder.append(scanner.nextLine());
                    }
                } catch (Exception e) {

                }

                // 解析Json

                JSONArray jsonArray = JSONObject.parseArray(builder.toString());
                news_list = parseJson(jsonArray);


                // 更新UI

            }
            catch (Exception e)
            {
            }
        }
    }
}