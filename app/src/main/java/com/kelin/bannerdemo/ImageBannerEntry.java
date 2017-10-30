package com.kelin.bannerdemo;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelin.banner.BannerEntry;

/**
 * 创建人 kelin
 * 创建时间 2017/7/25  下午5:12
 * 版本 v 1.0.0
 */

public class ImageBannerEntry implements BannerEntry<String> {
    private String title;
    @DrawableRes
    private int imgRes;

    ImageBannerEntry(String title, int imgRes) {
        this.title = title;
        this.imgRes = imgRes;
    }

    /**
     * 获取当前页面的布局视图。
     *
     * @param parent 当前的布局视图的父节点布局。
     * @return 返回当前页面所要显示的View。
     */
    @Override
    public View onCreateView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_banner_item, parent, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        imageView.setImageResource(imgRes);
        tvTitle.setText(title);
        return view;
    }

    /**
     * 获取标题。
     *
     * @return 返回当前条目的标题。
     */
    @Override
    public CharSequence getTitle() {
        return title;
    }

    /**
     * 获取子标题。
     *
     * @return 返回当前条目的子标题。
     */
    @Nullable
    @Override
    public CharSequence getSubTitle() {
        return null;
    }

    /**
     * 获取当前页面的数据。改方法为辅助方法，是为了方便使用者调用而提供的，Api本身并没有任何调用。如果你不需要该方法可以空实现。
     *
     * @return 返回当前页面的数据。
     */
    @Override
    public String getValue() {
        return title;
    }
}
