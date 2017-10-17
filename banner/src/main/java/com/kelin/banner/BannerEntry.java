package com.kelin.banner;

import android.view.View;
import android.view.ViewGroup;

/**
 * 描述 Banner中的模型
 * 创建人 kelin
 * 创建时间 2017/7/21  下午4:01
 * 版本 v 1.0.0
 */

public interface BannerEntry<VALUE> {

    /**
     * 获取当前页面的布局视图。
     * @param parent 当前的布局视图的父节点布局。
     * @return 返回当前页面所要显示的View。
     */
    View onCreateView(ViewGroup parent);

    /**
     * 获取标题。
     * @return 返回当前条目的标题。
     */
    CharSequence getTitle();

    /**
     * 获取子标题。
     * @return 返回当前条目的子标题。
     */
    CharSequence getSubTitle();

    /**
     * 获取当前页面的数据。改方法为辅助方法，是为了方便使用者调用而提供的，Api本身并没有任何调用。如果你不需要该方法可以空实现。
     * @return 返回当前页面的数据。
     */
    VALUE getValue();
}
