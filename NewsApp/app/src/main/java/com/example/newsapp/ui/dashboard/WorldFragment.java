package com.example.newsapp.ui.dashboard;

import android.graphics.Color;

import com.example.newsapp.ui.dashboard.DashboardFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.R;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class WorldFragment extends Fragment {

    private static final String ARG_PARAM1 = "name";

    private String name;
    Spinner spinner;
    View root;

    public WorldFragment() {
        // Required empty public constructor
    }

    public static WorldFragment newInstance(String param1) {
        WorldFragment fragment = new WorldFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_world_channel, container, false);

        spinner = root.findViewById(R.id.spinner);
        List<CountyInfo> list = CountyInfo.listAll(CountyInfo.class);
        LinkedHashSet<String> listS = new LinkedHashSet<String>();
        for (int i = 0; i < list.size(); ++i) {
            listS.add(list.get(i).getCountry());
        }
        this.root = root;
        List<String> country_list = new ArrayList<String>(listS);
        String[] arrs = country_list.toArray(new String[country_list.size()]);
        Arrays.sort(arrs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrs);

        spinner.setAdapter(adapter);

        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Country = spinner.getSelectedItem().toString();
                //清除数据
                String args[] = new String[2];
                args[0] = Country;
                args[1] = "";

                List<CountyInfo> listRet = CountyInfo.find(CountyInfo.class, " Country = ? and Province= ?", args);
                if (listRet.size() == 0) return;
                String datas = listRet.get(0).getDay_info();

                DashboardFragment.MyDrawChart(root, datas);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        spinner.setSelection(0, false);
        return root;
    }
}