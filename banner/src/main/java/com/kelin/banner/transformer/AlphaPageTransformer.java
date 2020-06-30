package com.kelin.banner.transformer;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * 描述 透明转场动画。
 * 创建人 kelin
 * 创建时间 2017/7/25  下午5:47
 * 版本 v 1.0.0
 */

public class AlphaPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1 + position);
            view.setTranslationX(pageWidth * -position);
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1 - position);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

        } else { // (1,+Infinity]
            view.setAlpha(0);
        }
    }
}
