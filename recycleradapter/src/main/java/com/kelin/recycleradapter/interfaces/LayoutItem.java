package com.kelin.recycleradapter.interfaces;

import android.view.View;

/**
 * 描述 用来描述条目的共性信息。
 * 创建人 kelin
 * 创建时间 2017/6/27  下午5:05
 * 版本 v 1.0.0
 */

public interface LayoutItem {

    /**
     * 获取当前的布局位置。
     * @return 返回列表中的布局位置。
     */
    int getLayoutPosition();

    /**
     * 获取跟布局的View。
     * @return 返回条目跟布局的View。
     */
    View getItemView();
}
