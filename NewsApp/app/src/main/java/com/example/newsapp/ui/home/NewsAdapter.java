package com.example.newsapp.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.orm.util.NamingHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NewsInfo> mNewsList;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;

    private final int newsType = 1;   //表示新闻
    private final int footerType = 2; //表示底部加载提示

    public enum LoadingType {NORMAL, LOADING_MORE, NO_MORE}

    private LoadingType footerState = LoadingType.NORMAL;

    public static class NewsHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private TextView timeTv;
        private TextView typeTv;
        private TextView sourceTv;

        public NewsHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.news_list_title);
            timeTv = view.findViewById(R.id.news_list_time);
            typeTv = view.findViewById(R.id.news_list_type);
            sourceTv = view.findViewById(R.id.news_list_source);
        }

        public void bindData(NewsInfo info) {
            titleTv.setText(info.title);
            timeTv.setText(info.time);
            typeTv.setText(info.newsType);
            sourceTv.setText(info.source);
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

    public NewsAdapter(AdapterView.OnClickListener listener, Context context) {
        mNewsList = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == newsType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_item_type, parent, false);
            view.setOnClickListener(listener);  //绑定点击监听器
            return new NewsHolder(view);
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
                    mNewsList.get(position).myId).size() != 0) {
                Log.i("PRESSED", "position = " + position + info.title.substring(0, 10));
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
        mNewsList.addAll(newData);
        changeState(LoadingType.NORMAL);
    }

    public NewsInfo getPositionItem(int position) {
        return mNewsList.get(position);
    }

    public void itemPressed(int position) {
        // 保存浏览记录到数据库
        BrowsingHistory history = new BrowsingHistory(mNewsList.get(position).myId);
        history.save();
    }

    // 返回是否有新的数据
    public boolean checkUpdateData(NewsInfo newData) {
        // 比较并添加数据
        NewsInfo current = mNewsList.get(0);
        Log.i("CURRENT", current.myId);
        Log.i("CURRENT", newData.myId);
        if (!newData.myId.equals(current.myId))  // 字符串比较用equals
            return true;
        else
            return false;
    }

    public void resetData(List<NewsInfo> newData) {
        mNewsList = newData;
        notifyDataSetChanged();
    }
}
