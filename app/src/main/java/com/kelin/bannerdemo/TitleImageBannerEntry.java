package com.kelin.bannerdemo;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kelin.banner.BannerEntry;

/**
 * 创建人 kelin
 * 创建时间 2017/7/25  下午5:12
 * 版本 v 1.0.0
 */

public class TitleImageBannerEntry implements BannerEntry<String> {
    private final String url;
    private String title;
    @DrawableRes
    private int imgRes;

    TitleImageBannerEntry(String title, int imgRes, String url) {
        this.title = title;
        this.imgRes = imgRes;
        this.url = url;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_title_banner_item, parent, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
        imageView.setImageResource(imgRes);
        return view;
    }

    /**
     * 获取标题
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
     * 获取当前页面的数据。
     *
     * @return 返回当前页面的数据。
     */
    @Override
    public String getValue() {
        return url;
    }
}
