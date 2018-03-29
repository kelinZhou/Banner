package com.kelin.bannerdemo;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * <strong>描述: </strong> 卷轴画效果的翻页动画
 * <p><strong>创建人: </strong> kelin
 * <p><strong>创建时间: </strong> 2018/3/28  下午2:42
 * <p><strong>版本: </strong> v 1.0.0
 */

public class ScrollPaintingPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        View child;
        if (page instanceof ViewGroup && (child = ((ViewGroup) page).getChildAt(0)) != null) {
            int pageWidth = page.getWidth();
            if (position >= -1 && position <= 1) {
                float translationX = pageWidth * -position;
                if (position <= 0) {
                    child.setTranslationX(translationX);
                    if (position == 0) {
                        page.setTranslationX(translationX);
                    }
                } else {
                    page.setTranslationX(translationX);
                }
            } else {
                child.setTranslationX(0);
                page.setTranslationX(0);
            }
        }
    }
}
