package com.kelin.recycleradapter.holder;

import android.view.View;

/**
 * 描述 加载更多时的ViewHolder。
 * 创建人 kelin
 * 创建时间 2017/4/26  下午5:51
 * 版本 v 1.0.0
 */
public class CommonNoDataViewHolder extends ItemViewHolder<Object> {

    public CommonNoDataViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBindData(int position, Object o) {}

    @Override
    public boolean areItemsTheSame(Object oldItemData, Object newItemDate) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(Object oldItemData, Object newItemDate) {
        return true;
    }
}
