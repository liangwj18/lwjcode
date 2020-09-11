package com.example.newsapp.ui.dashboard.detail;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.ui.dashboard.GraphInfo;
import com.example.newsapp.ui.dashboard.RelationInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> keyList;
    private List<String> propertyList;
    private Context mContext;
    private GraphInfo mInfo;
    private int TYPE_HEADER = 1;
    private int TYPE_PROPERTY = 2;

    public static class PropertyHolder extends RecyclerView.ViewHolder {
        private TextView keyTv;
        private TextView contentTv;

        public PropertyHolder(View view) {
            super(view);
            keyTv = view.findViewById(R.id.property_key);
            contentTv = view.findViewById(R.id.property_content);
        }

        public void bindData(String key, String property) {
            keyTv.setText(key);
            contentTv.setText(property);
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView abstractTv;

        public HeaderHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.entity_image_view);
            abstractTv = view.findViewById(R.id.entity_abstract);
        }

        public void bindData(String imageURL, String info) {
            // 加载描述和图片
            if (info != null && info.length() > 0)
                abstractTv.setText(info);
            else
                abstractTv.setVisibility(View.GONE);
            abstractTv.setText(info);
            if (imageURL != null)
                Picasso.get().load(imageURL).into(imageView);
            else
                imageView.setVisibility(View.GONE);
        }
    }

    public PropertyAdapter(GraphInfo info, Context context) {
        keyList = new ArrayList<>();
        propertyList = new ArrayList<>();
        mContext = context;
        mInfo = info;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_item_property_header, parent, false);
            return new HeaderHolder(view);
        } else {
            // TYPE_PROPERTY
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_item_property, parent, false);
            return new PropertyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bindData(mInfo.getImg(), mInfo.getInfo());
        } else if (holder instanceof PropertyHolder) {
            ((PropertyHolder) holder).bindData(keyList.get(position - 1), propertyList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return keyList.size() + 1;  //有一个header
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_PROPERTY;
        }
    }

    public void updateData(List<String> key, List<String> property) {
        // 每次对应着刷新
        keyList.clear();
        keyList.addAll(key);
        propertyList.clear();
        propertyList.addAll(property);
        notifyDataSetChanged();
    }
}