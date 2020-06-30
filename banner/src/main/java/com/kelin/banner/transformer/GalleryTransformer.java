package com.kelin.banner.transformer;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * **描述:** 立体效果的Transformer。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2019-08-28  16:26
 * <p>
 * **版本:** v 1.0.0
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GalleryTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        float scaleValue = 1 - Math.abs(position) * 0.3F;
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);
        page.setAlpha(scaleValue);
        //设置缩放的x轴中心点
        page.setPivotX(page.getWidth() * (1 - position - (position > 0 ? 1 : -1) * 0.75f) * 0.5f);
        //设置缩放的y轴中心点
        page.setPivotY(page.getHeight() / 2.0F);
        page.setElevation(position > -0.25 && position < 0.25 ? 1 : 0);
    }
}
