package com.java.huangjialiang.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.java.huangjialiang.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class DomesticFragment extends Fragment {

    private static final String ARG_PARAM1 = "name";

    private String name;
    Spinner spinner;

    public DomesticFragment() {
        // Required empty public constructor
    }

    public static DomesticFragment newInstance(String param1) {
        DomesticFragment fragment = new DomesticFragment();
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

    public int myParseInt(String st) {
        int ret = -1;
        if (st.equals("null")) return ret;
        else return Integer.parseInt(st);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_domestic_channel, container, false);

        spinner = root.findViewById(R.id.spinner);
        List<CountyInfo> list = CountyInfo.listAll(CountyInfo.class);
        LinkedHashSet<String> listS = new LinkedHashSet<String>();
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getCountry().equals("China")) {
                String province = list.get(i).getProvince();
                if (province.length() > 0)
                    listS.add(province);
            }
        }

        List<String> country_list = new ArrayList<String>(listS);
        String[] arrs = country_list.toArray(new String[country_list.size()]);
        Arrays.sort(arrs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrs);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Province = spinner.getSelectedItem().toString();
                //清除数据
                String args[] = new String[2];
                args[0] = Province;
                args[1] = "";

                List<CountyInfo> listRet = CountyInfo.find(CountyInfo.class, " Province = ? and County= ?", args);
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