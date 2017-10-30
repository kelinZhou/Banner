package com.kelin.banner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.kelin.banner.R;

/**
 * 描述 轮播图的指示器。
 * 创建人 kelin
 * 创建时间 2017/10/13  下午1:51
 * 版本 v 1.0.0
 */

public abstract class BannerIndicator extends View {

    /**
     * 用来记录总的数量。
     */
    private int mTotalCount;
    /**
     * 用来记录当前的位置。
     */
    private int mCurPosition;
    /**
     * 画笔
     */
    private final Paint mPaint;

    public BannerIndicator(Context context) {
        this(context, null);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (attrs != null) {
            onInitAttrs(context, attrs);
        }
    }

    @CallSuper
    protected void onInitAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
        if (typedArray != null) {
            mTotalCount = typedArray.getInt(R.styleable.BannerIndicator_totalCount, 0);
            typedArray.recycle();
        }
    }

    /**
     * 设置点的个数，如果你是配合 {@link BannerView } 使用的话，这不需要进行设置。只需要调用
     * {@link BannerView#setIndicatorView(BannerIndicator)} 方法或者为{@link BannerView}
     * 配置"app:bannerIndicator="@+id/banner_view"属性即可。
     *
     * @param totalCount 总的数量。
     * @see BannerView#setIndicatorView(BannerIndicator)
     */
    public final void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    /**
     * 设置当前选中的位置。如果你是配合 {@link BannerView Banner} 使用的话，这不需要进行设置。只需要调用
     * {@link BannerView#setIndicatorView(BannerIndicator)} 方法或者为{@link BannerView}
     * 配置"app:bannerIndicator"属性即可。
     *
     * @param position 当前的索引位置。
     */
    public final void setCurPosition(int position) {
        mCurPosition = position;
        requestLayout();
    }

    protected final int getTotalCount() {
        return mTotalCount;
    }

    protected final int getCurPosition() {
        return mCurPosition;
    }

    public final Paint getPaint() {
        return mPaint;
    }

    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int[] rect = new int[2];
        if (getTotalCount() > 0) {
            onMeasureWidthAndHeight(rect);
        }
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(rect[1], heightMode);
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(rect[0], widthMode);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
    }

    /**
     * 当需要测量宽度和高度的时候调用。参数为一个长度为2的int数组，
     * 你应当将测量的宽度赋值给数组的第一个元素，高度赋值给第二个元素。
     */
    protected abstract void onMeasureWidthAndHeight(int[] rect);
}
