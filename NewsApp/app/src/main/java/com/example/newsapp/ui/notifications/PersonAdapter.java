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
    private List<PersonInfo> mNewsList;
    private AdapterView.OnClickListener listener;   //点击监听器
    private Context context;
    private IWBAPI mWBAPI;

    private final int newsType = 1;   //表示新闻
    private final int footerType = 2; //表示底部加载提示

    public enum LoadingType {NORMAL, LOADING_MORE, NO_MORE}

    private LoadingType footerState = LoadingType.NORMAL;

    public static class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTv;
        private TextView HindexTv;
        private TextView ActivityTv;
        private TextView SociabilityTv;
        private TextView CitationsTv;
        private TextView PubsTv;

        private TextView PositionTv;
        private TextView AffiliationTv;
        private ImageView PictureTv;

        private Button shareBtn;
        private IWBAPI mWBAPI;

        public PersonHolder(View view, IWBAPI iwbapi) {
            super(view);
            nameTv = view.findViewById(R.id.news_list_name);

            HindexTv = view.findViewById(R.id.news_list_Hindex);
            System.out.println(HindexTv+"!!!!!!!!!!!!!");
            ActivityTv = view.findViewById(R.id.news_list_Activity);
            SociabilityTv = view.findViewById(R.id.news_list_Sociability);
            CitationsTv = view.findViewById(R.id.news_list_Citations);
            PubsTv = view.findViewById(R.id.news_list_Pubs);
            PositionTv = view.findViewById(R.id.news_list_Position);
            AffiliationTv = view.findViewById(R.id.news_list_Affiliation);
            PictureTv = view.findViewById(R.id.news_list_Picture);

            shareBtn = view.findViewById(R.id.news_list_share_btn);
            shareBtn.setOnClickListener(this);
            mWBAPI = iwbapi;
        }

        public void bindData(PersonInfo info) {

            nameTv.setText(info.getName_zh());
            if (info.getName_zh().equals(""))
                nameTv.setText(info.getName());
            HindexTv.setText(String.valueOf(info.getIndices().getHindex()));
            ActivityTv.setText(String.valueOf(info.getIndices().getActivity()));
            SociabilityTv.setText(String.valueOf(info.getIndices().getSociability()));
            CitationsTv.setText(String.valueOf(info.getIndices().getCitations()));
            PubsTv.setText(String.valueOf(info.getIndices().getPubs()));

            PositionTv.setText(info.getProfile_info().getPosition());

            String Affiliation=info.getProfile_info().getAffiliation();
            if (Affiliation==null || Affiliation.equals(""))
                Affiliation=info.getProfile_info().getAffiliation_zh();
            AffiliationTv.setText(Affiliation);
            String imageURL=info.getAvatar();
            if (imageURL != null)
                Picasso.get().load(imageURL).into(PictureTv);

        }

        private String getShareContent() {
            StringBuilder builder = new StringBuilder();
            builder.append("[Name] : " + nameTv.getText() + "\n\n");
            builder.append("[Affiliation] : " + AffiliationTv.getText() + "\n");
         //   builder.append("[Time] : " + timeTv.getText() + "\n");
        //    builder.append("[Source] : " + sourceTv.getText() + "\n\n");
         //   builder.append("[content] : " + shortCutTv.getText());
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

    public PersonAdapter(AdapterView.OnClickListener listener, Context context, IWBAPI mWBAPI) {
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
            PersonInfo info = mNewsList.get(position);
            ((PersonHolder) holder).bindData(info);
            // 如果数据库查到存在，那么变灰
            if (BrowsingHistory.find(BrowsingHistory.class, "my_id = ?",
                    mNewsList.get(position).getMyID()).size() != 0) {
            //    ((PersonHolder) holder).titleTv.setTextColor(context.getColorStateList(R.color.grey));
            } else {
              //  ((PersonHolder) holder).titleTv.setTextColor(context.getColorStateList(R.color.black));
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

    public void updateData(List<PersonInfo> newData) {
        if (newData != null)
            mNewsList.addAll(newData);
        changeState(LoadingType.NORMAL);
    }

    public PersonInfo getPositionItem(int position) {
        return mNewsList.get(position);
    }

    public void itemPressed(int position) {
        // 保存浏览记录到数据库
        BrowsingHistory history = new BrowsingHistory(mNewsList.get(position).getMyID());
        history.save();
    }

    // 返回是否有新的数据
    public boolean checkUpdateData(NewsInfo newData) {
        // 比较并添加数据
        PersonInfo current = mNewsList.get(0);
        if (!newData.getMyId().equals(current.getMyID()))  // 字符串比较用equals
            return true;
        else
            return false;
    }

    public void resetData(List<PersonInfo> newData) {
        int oldSize = mNewsList.size();
        mNewsList.clear();
        for (PersonInfo info : newData)
            mNewsList.add(info);
        notifyDataSetChanged();
    }
}
