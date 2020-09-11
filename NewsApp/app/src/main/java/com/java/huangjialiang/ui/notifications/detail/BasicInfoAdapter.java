package com.java.huangjialiang.ui.notifications.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.huangjialiang.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// 顺序是header profile indices
public class BasicInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String name, nameZh;
    private String avatar;
    private List<String> profileKey, profileValue;

    private Context mContext;

    final private int TYPE_HEADER = 1;
    final private int TYPE_PROFILE = 2;
    final int HEADER_NUM = 1;

    // 包括照片和姓名
    public static class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;
        private ImageView imageView;

        public HeaderHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.person_name);
            imageView = view.findViewById(R.id.person_avatar);
        }

        public void bindData(String name, String nameZh, String avatar) {
            if(nameZh.length() > 0)
                nameTv.setText(nameZh + "\n(" + name + ")");
            else
                nameTv.setText(name);
            if (avatar != null)
                Picasso.get().load(avatar).into(imageView);
        }
    }

    public static class ProfileHolder extends RecyclerView.ViewHolder {
        private TextView keyTv;
        private TextView valueTv;

        public ProfileHolder(View view) {
            super(view);
            keyTv = view.findViewById(R.id.profile_key);
            valueTv = view.findViewById(R.id.profile_value);
        }

        public void bindData(String key, String value) {
            keyTv.setText(key);
            valueTv.setText(value);
        }
    }

    public BasicInfoAdapter(Context context) {
        mContext = context;
        profileKey = new ArrayList<>();
        profileValue = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_person_header, parent, false);
            return new HeaderHolder(view);
        } else {
            // if (position < HEADER_NUM + profileKey.size())
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_person_profile, parent, false);
            return new ProfileHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bindData(name, nameZh, avatar);
        } else if (holder instanceof ProfileHolder) {
            ((ProfileHolder) holder).bindData(profileKey.get(position - HEADER_NUM), profileValue.get(position - HEADER_NUM));
        }
    }

    @Override
    public int getItemCount() {
        return HEADER_NUM + profileKey.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < HEADER_NUM) {
            return TYPE_HEADER;
        } else {
            //if (position < HEADER_NUM + profileKey.size())
            return TYPE_PROFILE;
        }
    }

    public void updateData(String name, String nameZh, String avatar,
                           List<String> profileKey, List<String> profileValue) {
        // 每次对应着刷新
        this.name = name;
        this.nameZh = nameZh;
        this.avatar = avatar;
        this.profileKey = profileKey;
        this.profileValue = profileValue;
        notifyDataSetChanged();
    }
}