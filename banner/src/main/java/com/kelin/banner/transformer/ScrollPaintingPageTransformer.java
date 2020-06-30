package com.kelin.banner.transformer;

import androidx.annotation.IntRange;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * <strong>描述: </strong> 卷轴画效果的翻页动画
 * <p><strong>创建人: </strong> kelin
 * <p><strong>创建时间: </strong> 2018/3/28  下午2:42
 * <p><strong>版本: </strong> v 1.0.0
 */

public class ScrollPaintingPageTransformer implements ViewPager.PageTransformer {

    private final int factor;

    /**
     * 构造一个卷轴画效果的PageTransformer。
     */
    public ScrollPaintingPageTransformer() {
        this(1);
    }

    /**
     * 构造一个卷轴画效果的PageTransformer。
     * @param factor 该参数决定了前后两页所重叠的部分为多少，如果不希望前后两页有所重叠，那么该参数的值应当为1。
     *               如果你希望重叠的部分占整个页面的一半(也就是二分之一)的话那么该参数的值应当为2。其实你可以把
     *               该参数看成是分母，分子永远为1。
     */
    public ScrollPaintingPageTransformer(@IntRange(from = 1, to = 10) int factor) {
        this.factor = factor;
    }

    @Override
    public void transformPage(View page, float position) {
        View child;
        if (page instanceof ViewGroup && (child = ((ViewGroup) page).getChildAt(0)) != null) {
            int pageWidth = page.getWidth();
            if (position >= -1 && position <= 1) {;
                float translationX = pageWidth * -position / factor;
                if (position <= 0) {
                    child.setTranslationX(translationX);
                    if (position == 0) {
                        page.setTranslationX(translationX);
                    }
                } else {
                    child.setTranslationX(0);
                    page.setTranslationX(translationX);
                }
            } else {
                child.setTranslationX(0);
                page.setTranslationX(0);
            }
        }
    }
}
