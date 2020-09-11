package com.java.huangjialiang.ui.dashboard.cluster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.huangjialiang.R;

import java.util.ArrayList;
import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mTypeList;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;

    public static class TypeHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;

        public TypeHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.cluster_type_title);
        }

        public void bindData(String title) {
            titleTv.setText(title);
        }
    }

    public TypeAdapter(AdapterView.OnClickListener listener, Context context) {
        mTypeList = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_item_cluster, parent, false);
        view.setOnClickListener(listener);  //绑定点击监听器
        return new TypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TypeHolder) holder).bindData(mTypeList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTypeList.size();
    }

    public String getPositionItem(int position) {
        return mTypeList.get(position);
    }

    public void updateData(List<String> initData) {
        // 每次对应着刷新
        mTypeList.clear();
        mTypeList.addAll(initData);
        notifyDataSetChanged();
    }
}
