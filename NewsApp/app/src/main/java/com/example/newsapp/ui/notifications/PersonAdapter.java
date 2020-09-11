package com.example.newsapp.ui.notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.ui.home.BrowsingHistory;
import com.example.newsapp.ui.home.NewsInfo;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PersonInfo> mPersonInfo;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;
    IWBAPI mWBAPI;

    private final int newsType = 1;   //表示新闻
    private final int footerType = 2; //表示底部加载提示

    public enum LoadingType {NORMAL, LOADING_MORE, NO_MORE}

    private LoadingType footerState = LoadingType.NORMAL;

    public static class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTv;

        private TextView positionTv;
        private ImageView imageView;

        private Button shareBtn;
        private IWBAPI mWBAPI;

        public PersonHolder(View view, IWBAPI iwbapi) {
            super(view);
            nameTv = view.findViewById(R.id.person_list_name);
            positionTv = view.findViewById(R.id.person_position);
            imageView = view.findViewById(R.id.person_list_avatar);
            shareBtn = view.findViewById(R.id.person_list_share);
            shareBtn.setOnClickListener(this);
            mWBAPI = iwbapi;
        }

        public void bindData(PersonInfo info) {
            nameTv.setText(info.getName_zh());
            if (info.getName_zh().equals(""))
                nameTv.setText(info.getName());
            positionTv.setText(info.getProfile_info().getPosition());
            String imageURL = info.getAvatar();
            if (imageURL != null)
                Picasso.get().load(imageURL).into(imageView);
        }

        private String getShareContent() {
            StringBuilder builder = new StringBuilder();
            builder.append("[Name] : " + nameTv.getText() + "\n\n");
            builder.append("\n\n来自NewsAPP客户端自动生成");
            return builder.toString();
        }

        private void doWeiboShare() {
            WeiboMultiMessage message = new WeiboMultiMessage();
            TextObject textObject = new TextObject();
            String text = getShareContent();
            // 分享文字
            textObject.text = text;
            message.textObject = textObject;
            mWBAPI.shareMessage(message, true);
        }

        @Override
        public void onClick(View view) {
            /*doWeiboShare();*/
        }
    }

    public static class FootHolder extends RecyclerView.ViewHolder {
        private TextView tipsTv;
        private ProgressBar progressBar;

        public FootHolder(View view) {
            super(view);
            tipsTv = view.findViewById(R.id.tips);
            progressBar = view.findViewById(R.id.progressbar);
        }
    }

    public PersonAdapter(AdapterView.OnClickListener listener, Context context, IWBAPI mWBAPI) {
        mPersonInfo = new ArrayList<>();
        this.listener = listener;
        this.context = context;
        this.mWBAPI = mWBAPI;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == newsType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_item_person, parent, false);
            view.setOnClickListener(listener);  //绑定点击监听器
            return new PersonHolder(view, mWBAPI);
        } else if (viewType == footerType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_view, parent, false);
            return new FootHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PersonHolder) {
            PersonInfo info = mPersonInfo.get(position);
            ((PersonHolder) holder).bindData(info);
        } else {
            FootHolder footHolder = (FootHolder) holder;
            switch (footerState) {
                case NORMAL:
                    footHolder.progressBar.setVisibility(View.GONE);
                    footHolder.tipsTv.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footHolder.progressBar.setVisibility(View.VISIBLE);
                    footHolder.tipsTv.setVisibility(View.VISIBLE);
                    footHolder.tipsTv.setText("正在加载...");
                    break;
                case NO_MORE:
                    footHolder.progressBar.setVisibility(View.GONE);
                    footHolder.tipsTv.setVisibility(View.VISIBLE);
                    footHolder.tipsTv.setText("已经划到底了");
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPersonInfo.size() + 1;    //有一个footer
    }

    public LoadingType getFooterState() {
        return footerState;
    }

    public void changeState(LoadingType type) {
        footerState = type;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footerType;
        } else {
            return newsType;
        }
    }

    public void updateData(List<PersonInfo> newData) {
        if (newData != null)
            mPersonInfo.addAll(newData);
        changeState(LoadingType.NORMAL);
    }

    public PersonInfo getPositionItem(int position) {
        return mPersonInfo.get(position);
    }
}
