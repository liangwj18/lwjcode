package com.example.newsapp.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;

import java.util.ArrayList;
import java.util.List;

public class unusedGraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GraphInfo> mNewsList;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;

    private final int newsrelation = 1;   //表示新闻
    private final int footerType = 2; //表示底部加载提示
    enum LoadingType {NORMAL, LOADING_MORE, NO_MORE}

    private unusedGraphAdapter.LoadingType footerState = unusedGraphAdapter.LoadingType.NORMAL;

    public static class GraphHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;


        public GraphHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.graph_list_title);

        }

        public void bindData(GraphInfo info) {
            titleTv.setText(info.getLabel());
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

    public unusedGraphAdapter(AdapterView.OnClickListener listener, Context context) {
        mNewsList = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewrelation) {
        if (viewrelation == newsrelation) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_item_type_graph, parent, false);
            view.setOnClickListener(listener);  //绑定点击监听器
            return new GraphHolder(view);}
        else if (viewrelation == footerType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer_view, parent, false);
            return new unusedGraphAdapter.FootHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mNewsList.size()==0) return ;
        if (holder instanceof GraphHolder) {
            GraphInfo info = mNewsList.get(position);
            ((GraphHolder) holder).bindData(info);
            // 如果数据库查到存在，那么变灰

            ((GraphHolder) holder).titleTv.setTextColor(context.getColorStateList(R.color.black));
        }
        else {
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



    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footerType;
        } else {
            return newsrelation;
        }
    }

    public void updateData(List<GraphInfo> newData) {

        mNewsList.addAll(newData);
        footerState = LoadingType.NORMAL;
        notifyDataSetChanged();
    }

    public GraphInfo getPositionItem(int position) {
        return mNewsList.get(position);
    }



    public void resetData(List<GraphInfo> newData) {
        mNewsList = newData;
        notifyDataSetChanged();
    }
}
