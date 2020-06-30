package com.kelin.banner.page;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * 描述 中间大两边缩放变小的 {@link ViewPager.PageTransformer}
 * 创建人 kelin
 * 创建时间 2017/7/25  下午3:59
 * 版本 v 1.0.0
 */

public class CenterBigTransformer implements ViewPager.PageTransformer {
    private float scale;
    private int pageWidth;
    private int pageHeight;

    public CenterBigTransformer(@FloatRange(from = 0.0f, to = 1.0f) float scaleSize) {
        this.scale = scaleSize;
    }

    /**
     * 改变页面。
     *
     * @param page     需要执行动画的View。
     * @param position 相对于中间位置的位置，有三个临界值：-1、0、1。 其中0位正中心位置。1是向右的一个完整页面的位置。
     *                 -1是向左的一个完整页面的位置。
     */
    @Override
    public final void transformPage(@NonNull View page, float position) {
        //因为一个页面中做多可以出现三个页面所以超出范围的不计算。

        //设置缩放的x轴中心点
        page.setPivotX((page.getWidth() != 0 ? (pageWidth = page.getWidth()) : pageWidth) / 2);
        //设置缩放的y轴中心点
        page.setPivotY((page.getHeight() != 0 ? (pageHeight = page.getHeight()) : pageHeight) / 2);


        //设置缩放比例
        if (position == 0) {
            page.setScaleY(1);
        } else if (position >= -1 && position <= 1) {
            page.setScaleY(1 - Math.abs(position) * (1 - scale));
        } else {
            page.setScaleY(scale);
        }
    }
}
