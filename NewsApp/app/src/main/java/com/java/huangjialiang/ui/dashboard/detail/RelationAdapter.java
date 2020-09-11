package com.java.huangjialiang.ui.dashboard.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.huangjialiang.R;
import com.java.huangjialiang.ui.dashboard.RelationInfo;

import java.util.ArrayList;
import java.util.List;


public class RelationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RelationInfo> mRelationInfoList;
    private AdapterView.OnClickListener listener;
    private Context mContext;

    public static class RelationHolder extends RecyclerView.ViewHolder {
        private TextView labelTv;
        private TextView relationTv;
        private ImageView imageView;

        public RelationHolder(View view) {
            super(view);
            labelTv = view.findViewById(R.id.relation_label);
            relationTv = view.findViewById(R.id.relation_relation);
            imageView = view.findViewById(R.id.ward_icon);
        }

        public void bindData(RelationInfo relationInfo) {
            labelTv.setText(relationInfo.getLabel());
            relationTv.setText(relationInfo.getRelation());
            if(relationInfo.isForward()){
                imageView.setImageResource(R.mipmap.forward);
            }else{
                imageView.setImageResource(R.mipmap.backward);
            }
        }
    }

    public RelationAdapter(AdapterView.OnClickListener listener, Context context) {
        mRelationInfoList = new ArrayList<>();
        this.listener = listener;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_item_relation, parent, false);
        view.setOnClickListener(listener);  //绑定点击监听器
        return new RelationHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((RelationHolder) holder).bindData(mRelationInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mRelationInfoList.size();
    }

    public RelationInfo getPositionItem(int position) {
        return mRelationInfoList.get(position);
    }

    public void updateData(List<RelationInfo> initData) {
        // 每次对应着刷新
        mRelationInfoList.clear();
        mRelationInfoList.addAll(initData);
        notifyDataSetChanged();
    }
}