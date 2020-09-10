package com.example.newsapp.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.orm.util.NamingHelper;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.openapi.IWBAPI;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NewsInfo> mNewsList;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;
    private IWBAPI mWBAPI;

    private final int newsType = 1;   //表示新闻
    private final int footerType = 2; //表示底部加载提示

    enum LoadingType {NORMAL, LOADING_MORE, NO_MORE}

    private LoadingType footerState = LoadingType.NORMAL;

    public static class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTv;
        private TextView timeTv;
        private TextView typeTv;
        private TextView sourceTv;
        private TextView shortCutTv;
        private Button shareBtn;
        private IWBAPI mWBAPI;

        public NewsHolder(View view, IWBAPI iwbapi) {
            super(view);
            titleTv = view.findViewById(R.id.news_list_title);
            timeTv = view.findViewById(R.id.news_list_time);
            typeTv = view.findViewById(R.id.news_list_type);
            sourceTv = view.findViewById(R.id.news_list_source);
            shortCutTv = view.findViewById(R.id.news_list_short_cut);
            shareBtn = view.findViewById(R.id.news_list_share_btn);
            shareBtn.setOnClickListener(this);
            mWBAPI = iwbapi;
        }

        public void bindData(NewsInfo info) {
            titleTv.setText(info.getTitle());
            timeTv.setText(info.getTime().split(" ")[0]);
            typeTv.setText(info.getNewsType());
            sourceTv.setText(info.getSource());
            shortCutTv.setText(info.getContent());
        }

        private String getShareContent() {
            StringBuilder builder = new StringBuilder();
            builder.append("[Title] : " + titleTv.getText() + "\n\n");
            builder.append("[Type] : " + typeTv.getText() + "\n");
            builder.append("[Time] : " + timeTv.getText() + "\n");
            builder.append("[Source] : " + sourceTv.getText() + "\n\n");
            builder.append("[content] : " + shortCutTv.getText());
            builder.append("\n\n来自NewsAPP客户端自动生成");
            return builder.toString();
        }

        private void doWeiboShare() {
            Log.i("Weibo", "Start to share");
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
            doWeiboShare();
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

    public NewsAdapter(AdapterView.OnClickListener listener, Context context, IWBAPI mWBAPI) {
        mNewsList = new ArrayList<>();
        this.listener = listener;
        this.context = context;
        this.mWBAPI = mWBAPI;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == newsType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_item_type, parent, false);
            view.setOnClickListener(listener);  //绑定点击监听器
            return new NewsHolder(view, mWBAPI);
        } else if (viewType == footerType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_view, parent, false);
            return new FootHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsHolder) {
            NewsInfo info = mNewsList.get(position);
            ((NewsHolder) holder).bindData(info);
            // 如果数据库查到存在，那么变灰
            if (BrowsingHistory.find(BrowsingHistory.class, "my_id = ?",
                    mNewsList.get(position).getMyId()).size() != 0) {
                ((NewsHolder) holder).titleTv.setTextColor(context.getColorStateList(R.color.grey));
            } else {
                ((NewsHolder) holder).titleTv.setTextColor(context.getColorStateList(R.color.black));
            }
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
        return mNewsList.size() + 1;    //有一个footer
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

    public void updateData(List<NewsInfo> newData) {
        if (newData != null)
            mNewsList.addAll(newData);
        changeState(LoadingType.NORMAL);
    }

    public NewsInfo getPositionItem(int position) {
        return mNewsList.get(position);
    }

    public void itemPressed(int position) {
        // 保存浏览记录到数据库
        BrowsingHistory history = new BrowsingHistory(mNewsList.get(position).getMyId());
        history.save();
    }

    // 返回是否有新的数据
    public boolean checkUpdateData(NewsInfo newData) {
        // 比较并添加数据
        NewsInfo current = mNewsList.get(0);
        if (!newData.getMyId().equals(current.getMyId()))  // 字符串比较用equals
            return true;
        else
            return false;
    }

    public void resetData(List<NewsInfo> newData) {
        int oldSize = mNewsList.size();
        mNewsList.clear();
        for (NewsInfo info : newData)
            mNewsList.add(info);
        notifyDataSetChanged();
    }
}
