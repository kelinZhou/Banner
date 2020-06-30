package com.kelin.bannerdemo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kelin.banner.SimpleBannerEntry;

/**
 * 创建人 kelin
 * 创建时间 2017/7/25  下午5:12
 * 版本 v 1.0.0
 */

public class ImageBannerEntry extends SimpleBannerEntry<String> {
    private String title;
    private String subTitle;
    private String imgUrl;

    ImageBannerEntry(String title, String subTitle, String imgUrl) {
        this.title = title;
        this.subTitle = subTitle;
        this.imgUrl = imgUrl;
    }

    /**
     * 获取当前页面的布局视图。
     *
     * @param parent 当前的布局视图的父节点布局。
     * @return 返回当前页面所要显示的View。
     */
    @Override
    @NonNull
    public View onCreateView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_banner_item, parent, false);
        ImageView imageView = view.findViewById(R.id.iv_image);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvSubTitle = view.findViewById(R.id.tv_sub_title);
        //这是的Glide代码只是为了掩饰加载网络图片，如果你还不了解Glide的使用可以参考官方文档或他人博客。
        //这个库没有集成图片框架是因为大家的项目中所使用的图片框架可能不是都是一样的。我认为使用什么图片框架应该由大家自己决定，而不是我来集成。
        Glide.with(parent.getContext())
                .load(imgUrl)
                .into(imageView);
        tvTitle.setText(title);
        tvSubTitle.setText(subTitle);
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
        return subTitle;
    }

    @NonNull
    @Override
    public String getImageUrl() {
        return imgUrl;
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
