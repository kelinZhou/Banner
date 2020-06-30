package com.kelin.banner.transformer;

import android.annotation.SuppressLint;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * **描述:** 缩放动画，收集于CSDN。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2018/9/7  上午10:04
 * <p>
 * **版本:** v 1.0.0
 */
public class CardPageTransformer implements ViewPager.PageTransformer {
    /**
     * 偏移量
     */
    private int mScaleOffset;

    public CardPageTransformer() {
        this(40);
    }

    /**
     * @param mScaleOffset 缩放偏移量 单位 px
     */
    public CardPageTransformer(int mScaleOffset) {
        this.mScaleOffset = mScaleOffset;
    }

    @SuppressLint("NewApi")
    public void transformPage(View page, float position) {
        if (position < -1) {
            page.setTranslationX(0);
            page.setRotation(0);
        } else if (position <= 0.0f) {//被滑动的那页 position 是-下标~ 0
            //旋转角度 45° * -0.1 = -4.5°
            page.setRotation((45 * position));
            //X轴偏移 li: 300/3 * -0.1 = -10
            page.setTranslationX((page.getWidth() / 3 * position));
        } else {
            //缩放比例
            page.setTranslationX(0);
            page.setRotation(0);
            float scale = (page.getWidth() - mScaleOffset * position) / (float) (page.getWidth());
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setTranslationX((-page.getWidth() * position));
            page.setTranslationY((mScaleOffset * 0.8f) * position);
        }
    }
}
