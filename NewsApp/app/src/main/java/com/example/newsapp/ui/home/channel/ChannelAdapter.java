package com.example.newsapp.ui.home.channel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;

import java.util.Collections;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<ChannelItem> mList;
    private int selectedSize;       //表明当前已选的类别
    private int fixSize;           //已选频道中固定频道大小
    private int mTabY;            //Tab距离parent的Y的距离

    public ChannelAdapter(Context mContext, List<ChannelItem> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public int getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(int selectedSize) {
        this.selectedSize = selectedSize;
    }

    public int getFixSize() {
        return fixSize;
    }

    public void setFixSize(int fixSize) {
        this.fixSize = fixSize;
    }

    public int getItemSpanSize(int position) {
        return mList.get(position).getSpanSize();
    }

    /*和ViewHolder相关部分*/
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(viewType, parent, false);
        if (viewType == R.layout.channel_item) {
            // 普通的频道
            return new ChannelHolder(view);
        } else {
            // 分割
            return new DividerHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelHolder) {
            setChannel((ChannelHolder) holder, mList.get(position));
        } else {
            setDivider((DividerHolder) holder);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    // 移动频道，逐个交换，保持连续，只操作数据
    void itemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getLayoutId();   //类别直接返回对应layout的ID
    }

    // 设置频道的数据和点击事件
    private void setChannel(final ChannelHolder holder, ChannelItem item) {
        final int position = holder.getLayoutPosition();
        holder.nameTv.setText(item.getName());
        holder.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getLayoutPosition() < selectedSize + 1) {
                    //上面已经选中的 点击移除
                    if (holder.getLayoutPosition() > fixSize) {
                        removeFromSelected(holder);
                    }
                } else {
                    //下面的没有选择的 点击添加到已选频道
                    itemMove(holder.getLayoutPosition(), selectedSize);
                    holder.deleteIcon.setVisibility(View.VISIBLE);
                    selectedSize++;
                    notifyItemChanged(selectedSize);
                }
            }
        });
        holder.nameTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //返回true 防止长按拖拽事件跟点击事件冲突
                return true;
            }
        });
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromSelected(holder);
            }
        });

        //tab下面的不显示删除按钮
        if (position - 1 < fixSize || position > selectedSize) {
            holder.deleteIcon.setVisibility(View.GONE);
        } else {
            holder.deleteIcon.setVisibility(View.VISIBLE);
        }
    }

    private void setDivider(DividerHolder holder) {
        // 对于分界，什么也不做
    }

    // 点击移除该频道后的回调
    private void removeFromSelected(ChannelHolder holder) {
        int position = holder.getLayoutPosition();
        holder.deleteIcon.setVisibility(View.GONE);
        //移除的频道当前选中的频道，直接调用系统的移除动画
        itemMove(position, selectedSize);
        //通知RecyclerView数据内容变化
        notifyItemRangeChanged(selectedSize, 1);
        selectedSize--;
    }

    public static class ChannelHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        ImageView deleteIcon;

        public ChannelHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.channel_name);
            deleteIcon = itemView.findViewById(R.id.channel_delete);
        }
    }

    public static class DividerHolder extends RecyclerView.ViewHolder {

        public DividerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
