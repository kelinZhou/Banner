package com.kelin.bannerdemo;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 创建人 kelin
 * 创建时间 2017/7/25  下午5:47
 * 版本 v 1.0.0
 */

class DepthPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        float MIN_SCALE = 0.75f;
        int pageWidth = page.getWidth();
        if (position < -1) {
            page.setAlpha(0);
        } else if (position <= 0) {
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        } else if (position <= 1) {
            page.setAlpha(1 - position);
            page.setTranslationX(pageWidth * -position);
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        } else {
            page.setAlpha(0);
        }
    }
}
