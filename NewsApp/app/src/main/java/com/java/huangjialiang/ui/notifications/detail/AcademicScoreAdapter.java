package com.java.huangjialiang.ui.notifications.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.huangjialiang.R;

import java.util.ArrayList;
import java.util.List;

public class AcademicScoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> key, value;
    private Context mContext;

    // 包括照片和姓名
    public static class KeyValueHolder extends RecyclerView.ViewHolder {
        private TextView keyTv;
        private TextView valueTv;

        public KeyValueHolder(View view) {
            super(view);
            keyTv = view.findViewById(R.id.person_key);
            valueTv = view.findViewById(R.id.person_value);
        }

        public void bindData(String key, String value) {
            keyTv.setText(key);
            valueTv.setText(value);
        }
    }

    public AcademicScoreAdapter(Context context) {
        mContext = context;
        key = new ArrayList<>();
        value = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_person_key_value, parent, false);
            return new KeyValueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof KeyValueHolder) {
            ((KeyValueHolder) holder).bindData(key.get(position), value.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return key.size();
    }


    public void updateData(List<String> newKey, List<String>newValue) {
        // 每次对应着刷新
        key = newKey;
        value = newValue;
        notifyDataSetChanged();
    }
}