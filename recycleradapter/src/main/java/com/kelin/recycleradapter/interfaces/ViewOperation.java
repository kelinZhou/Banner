package com.kelin.recycleradapter.interfaces;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

/**
 * 描述 描述View相关的操作。
 * 创建人 kelin
 * 创建时间 2017/4/10  下午3:44
 * 版本 v 1.0.0
 */

public interface ViewOperation {

    /**
     * 根据{@link View}的Id获取一个{@link View}。
     *
     * @param id 一个{@link View}的Id。
     * @return 返回一个 {@link View}对象。
     */
    <T extends View> T getView(@IdRes int id);

    /**
     * 设置背景
     *
     * @param drawable 需要设置的 {@link Drawable} 对象。
     * @param viewIds  要设置背景的 {@link View} 的ID，支持多个。
     */
    void setBackGround(Drawable drawable, @IdRes int... viewIds);

    /**
     * 设置背景颜色。
     * @param color 要设置的颜色。
     * @param viewIds  要设置背景颜色的 {@link View} 的ID，支持多个。
     */
    void setBackgroundColor(@ColorInt int color, @IdRes int... viewIds);

    /**
     * 设置背景Selector。
     * @param tint {@link ColorStateList} 对象。
     * @param viewIds  要设置Selector的 {@link View} 的ID，支持多个。
     */
    void setBackgroundTintList(@Nullable ColorStateList tint, @IdRes int... viewIds);

    /**
     * 设置背景资源。
     * @param drawable 资源文件ID。
     * @param viewIds  要设置背景的 {@link View} 的ID，支持多个。
     */
    void setBackgroundResource(@DrawableRes int drawable, @IdRes int... viewIds);

    /**
     * 设置文字。
     *
     * @param textViewId {@link TextView} 或其子类 {@link View} 的资源ID。
     * @param text       要设置的文字。
     */
    void setText(@IdRes int textViewId, CharSequence text);

    /**
     * 设置文字。
     *
     * @param textViewId {@link TextView} 或其子类 {@link View} 的资源ID。
     * @param textRes    要设置的文字的资源ID。
     */
    void setText(@IdRes int textViewId, @StringRes int textRes);

    /**
     * 设置提示文字。
     *
     * @param textViewId {@link TextView} 或其子类 {@link View} 的资源ID。
     * @param text       要设置的文字。
     */
    void setHint(@IdRes int textViewId, CharSequence text);

    /**
     * 设置提示文字。
     *
     * @param textViewId {@link TextView} 或其子类 {@link View} 的资源ID。
     * @param textRes    要设置的文字的资源ID。
     */
    void setHint(@IdRes int textViewId, @StringRes int textRes);

    /**
     * 将制定的 {@link View} 设置为完全不可见。
     *
     * @param viewIds 要设置的 {@link View} 的ID。
     */
    void setGone(@IdRes int... viewIds);

    /**
     * 将制定的 {@link View} 设置为不可见。
     *
     * @param viewIds 要设置的 {@link View} 的ID。
     */
    void setInvisible(@IdRes int... viewIds);

    /**
     * 将指定的 {@link View} 设置为可见。
     *
     * @param viewIds 要设置的 {@link View} 的ID。
     */
    void setVisible(@IdRes int... viewIds);

    /**
     * 设置字体颜色。
     * @param color {@link ColorStateList} 对象。
     * @param viewIds 要设置的 {@link TextView} 的ID。
     */
    void setTextColor(@NonNull ColorStateList color, @IdRes int... viewIds);

    /**
     * 设置字体颜色。
     * @param color 要设置的颜色的int值。
     * @param viewIds 要设置的 {@link TextView} 的ID。
     */
    void setTextColor(@ColorInt int color, @IdRes int... viewIds);

    /**
     * 给imageView设置图片。
     * @param viewId 要设置的 {@link android.widget.ImageView} 的ID。
     * @param img 图片资源。
     */
    void setImageResource(@IdRes int viewId, @DrawableRes int img);
}
