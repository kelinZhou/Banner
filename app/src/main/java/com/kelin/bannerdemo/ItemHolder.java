package com.kelin.bannerdemo;

import android.view.View;

import com.kelin.recycleradapter.holder.ItemLayout;
import com.kelin.recycleradapter.holder.ItemViewHolder;

/**
 * 创建人 kelin
 * 创建时间 2017/7/27  上午10:11
 * 版本 v 1.0.0
 */
@ItemLayout(android.R.layout.simple_expandable_list_item_1)
class ItemHolder extends ItemViewHolder<String> {
    protected ItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBindData(int i, String s) {
        setText(android.R.id.text1, s);
    }
}
