package com.example.newsapp.ui.dashboard.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.ui.dashboard.GraphInfo;

import java.util.ArrayList;
import java.util.List;

public class EntityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GraphInfo> mInfoList;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;

    public static class EntityHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;

        public EntityHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.graph_list_title);
        }

        public void bindData(String title) {
            titleTv.setText(title);
        }
    }

    public EntityAdapter(AdapterView.OnClickListener listener, Context context) {
        mInfoList = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_item_graph, parent, false);
        view.setOnClickListener(listener);  //绑定点击监听器
        return new EntityHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EntityHolder) {
            ((EntityHolder) holder).bindData(mInfoList.get(position).getLabel());
        }
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    public GraphInfo getPositionItem(int position) {
        return mInfoList.get(position);
    }

    public void updateData(List<GraphInfo> initData) {
        // 每次对应着刷新
        mInfoList.clear();
        mInfoList.addAll(initData);
        notifyDataSetChanged();
    }
}
