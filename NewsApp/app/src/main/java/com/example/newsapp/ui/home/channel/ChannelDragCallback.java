package com.example.newsapp.ui.home.channel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;

public class ChannelDragCallback extends ItemTouchHelper.Callback {
    private ChannelAdapter mAdapter;
    private Paint mPaint;    //虚线画笔，用来画虚线的
    private int mPadding;   //虚线框框跟按钮间的距离
    private int greyColor;

    public ChannelDragCallback(ChannelAdapter mAdapter, int mPadding, int color) {
        this.mAdapter = mAdapter;
        this.mPadding = mPadding;
        greyColor = color;
        mPaint = new Paint();
        mPaint.setColor(greyColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect pathEffect = new DashPathEffect(new float[]{5f, 5f}, 5f);    //虚线
        mPaint.setPathEffect(pathEffect);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //固定位置及tab下面的channel不能拖动
        if (viewHolder.getLayoutPosition() < mAdapter.getFixSize() + 1 || viewHolder.getLayoutPosition() > mAdapter.getSelectedSize()) {
            return 0;
        }
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();   //拖动的position
        int toPosition = target.getAdapterPosition();     //释放的position
        //固定位置及tab下面的channel不能拖动
        if (toPosition < mAdapter.getFixSize() + 1 || toPosition > mAdapter.getSelectedSize())
            return false;
        mAdapter.itemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //不支持滑动
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (dX != 0 && dY != 0 || isCurrentlyActive) {
            //长按拖拽时底部绘制一个虚线矩形
            c.drawRect(viewHolder.itemView.getLeft(),viewHolder.itemView.getTop()-mPadding,viewHolder.itemView.getRight(),viewHolder.itemView.getBottom(),mPaint);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState==ACTION_STATE_DRAG){
            //长按时调用
            ChannelAdapter.ChannelHolder holder= (ChannelAdapter.ChannelHolder) viewHolder;
            holder.nameTv.setBackgroundColor(Color.parseColor("#FDFDFE"));  // 拖动变白
            holder.deleteIcon.setVisibility(View.GONE);     //拖动时不显示删除图标
            holder.nameTv.setElevation(5f);     //纵向提升的效果
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 交互完之后，使用该方法重写View
        super.clearView(recyclerView, viewHolder);
        ChannelAdapter.ChannelHolder holder= (ChannelAdapter.ChannelHolder) viewHolder;
        holder.nameTv.setBackgroundColor(greyColor);
        holder.nameTv.setElevation(0f);     // 取消提升
        holder.deleteIcon.setVisibility(View.VISIBLE);
    }
}
