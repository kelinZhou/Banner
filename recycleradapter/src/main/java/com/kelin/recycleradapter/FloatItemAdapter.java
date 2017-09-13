package com.kelin.recycleradapter;

import android.support.annotation.NonNull;

import com.kelin.recycleradapter.holder.ItemViewHolder;
import com.kelin.recycleradapter.holder.ViewHelper;

import java.util.List;

/**
 * 描述 悬浮条目的子适配器。<p>使用了该类并不一定就会有悬浮吸顶效果，您还要在布局文件中添加  {@link FloatLayout} 控件，
 * {@link FloatLayout} 在xml文件中的的摆放必须满足以下特点：
 * <p>1.必须和 {@link android.support.v7.widget.RecyclerView RecyclerView} 是可重叠关系，也就是说用来承载 {@link android.support.v7.widget.RecyclerView RecyclerView} 和 {@link FloatLayout} 的布局容器必须是
 * 允许子View可以重叠的Layout，例如：{@link android.widget.RelativeLayout} 或 {@link android.widget.FrameLayout} 等。
 * <p>2.由于Android并不是任何时候都允许修改View的层级，所以在xml布局文件中 {@link FloatLayout} 的层级必须在 {@link android.support.v7.widget.RecyclerView RecyclerView} 之上。
 * 创建人 kelin
 * 创建时间 2017/6/29  上午9:23
 * 版本 v 1.0.0
 */

public class FloatItemAdapter<D> extends SuperItemAdapter<D> {

    private ItemViewHolder<D> mFloatLayoutBinder;

    public FloatItemAdapter(@NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        super(holderClass);
    }

    public FloatItemAdapter(@NonNull Class<? extends ItemViewHolder<D>> holderClass, D d) {
        super(holderClass, d);
    }

    public void setItemData(D d) {
        setItemData(d, true);
    }

    public void setItemData(D d, boolean refresh) {
        getDataList().clear();
        getDataList().add(d);
        if (refresh) {
            mapNotifyItemChanged(0);
        }
    }

    @Override
    public D getObject(int position) {
        return super.getObject(0);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder<D> holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (mFloatLayoutBinder == null) {
            mFloatLayoutBinder = holder;
        }
    }

    @Override
    public int getItemSpanSize() {
        return SPAN_SIZE_FULL_SCREEN;
    }

    void onBindFloatViewData(ViewHelper floatViewHelper, D d) {
        mFloatLayoutBinder.onBindFloatLayoutData(floatViewHelper, d);
        bindItemClickEvent(floatViewHelper, mFloatLayoutBinder);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
