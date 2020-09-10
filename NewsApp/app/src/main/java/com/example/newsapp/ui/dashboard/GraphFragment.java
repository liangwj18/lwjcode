package com.example.newsapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.GraphDetailActivity;
import com.example.newsapp.MainActivity;
import com.example.newsapp.NewsDetailActivity;
import com.example.newsapp.R;
import com.example.newsapp.ui.home.NewsAdapter;
import com.example.newsapp.ui.home.NewsInfo;
import com.example.newsapp.utils.HttpsTrustManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class GraphFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";

    private LinearLayoutManager linearLayoutManager;
    private Handler mHandler;
    private ListView listView;

    private   DetailHelper detailHelper;

    // TODO: Rename and change types of parameters
    private String name;

    public GraphFragment() {
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
    public static GraphFragment newInstance(String param1) {
        GraphFragment fragment = new GraphFragment();
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
    public GraphInfo parseGraph(JSONObject object)
    {
        String label=object.getString("label");
        String img=object.getString("img");
        JSONObject abstarctInfo=object.getJSONObject("abstractInfo");
        String info="";
        String enwiki=abstarctInfo.getString("enwiki");
        String baidu=abstarctInfo.getString("baidu");
        String zhwiki=abstarctInfo.getString("zhwiki");

        info=enwiki;
        if (info.length()<baidu.length()) info=baidu;
        if (info.length()<zhwiki.length()) info=zhwiki;

        JSONObject covid=abstarctInfo.getJSONObject("COVID");
        String properties=covid.getString("properties");
        String relations=covid.getString("relations");
        return new GraphInfo(img,label,info,properties,relations);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_graph_channel, container, false);
        TextView textView=root.findViewById(R.id.text_graph_channel);
        textView.setText("疫情图谱搜索");

        listView = root.findViewById(R.id.graph_listView);//在视图中找到ListView
        ArrayList<String> data=new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,data);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);




        SearchView searchView=root.findViewById(R.id.graph_searchview);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            //输入完成后，提交时触发的方法，一般情况是点击输入法中的搜索按钮才会触发，表示现在正式提交了
            public boolean onQueryTextSubmit(String query)
            {
                if(TextUtils.isEmpty(query))
                {
                    Toast.makeText(getActivity(), "请输入查找内容！", Toast.LENGTH_SHORT).show();
                    adapter.clear();

                    listView.setAdapter(adapter);

                }
                else
                {
                    String urlString="https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity="+query;
                    detailHelper=new DetailHelper(urlString);
                    new Thread(detailHelper).start();


                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final ArrayList<GraphInfo> list = detailHelper.getList();
                            adapter.clear();
                            for (int i = 0; i < list.size(); ++i) {
                                adapter.add(list.get(i).getLabel());
                            }
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    GraphInfo.deleteAll(GraphInfo.class);
                                    GraphInfo info = list.get(i);
                                    info.save();
                                    Intent intent = new Intent(getActivity(), GraphDetailActivity.class);
                                    intent.putExtra("Label",info.label);
                                    startActivity(intent);

                                }
                            });
                        }
                    }, 1000);


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
                }
                return true;
            }
            //在输入时触发的方法，当字符真正显示到searchView中才触发，像是拼音，在输入法组词的时候不会触发
            public boolean onQueryTextChange(String newText)
            {

                return true;
            }
        });

        return root;
    }

    private class DetailHelper implements Runnable {
        private String urlString;
        // 下面是新闻的内容
        private String content;
        private String time;
        private String lang;
        private String source;
        private String type;
        private String title;
        private String originURL;
        private ArrayList<GraphInfo> list;
        public DetailHelper(String url) {
            this.urlString = url;
        }

        public ArrayList<GraphInfo> getList()
        {
            return list;
        }

        @Override
        public void run() {
            try {
                list = new ArrayList<GraphInfo>();
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

                // 解析Json

                JSONObject jsonSet = JSONObject.parseObject(builder.toString());
                JSONArray jsonArray= jsonSet.getJSONArray("data");
                for (int i=0;i<jsonArray.size();++i)
                {

                    GraphInfo graphInfo=parseGraph(jsonArray.getJSONObject(i));
                    list.add(graphInfo);
                }

                // 更新UI

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }}
    }
}