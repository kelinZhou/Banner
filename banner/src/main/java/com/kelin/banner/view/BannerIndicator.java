package com.kelin.banner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
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
     * 画笔。
     */
    private final Paint mPaint;
    int mGravity = Gravity.CENTER;
    private int mMeasureWidth;
    private int mMeasureHeight;

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
            setGravity(typedArray.getInt(R.styleable.BannerIndicator_android_gravity, Gravity.CENTER));
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
        int measureWidth = mMeasureWidth = onMeasureWidth();
        int realWidth = measureWidth + getPaddingLeft() + getPaddingRight();
        int measureHeight = mMeasureHeight = onMeasureHeight();
        int realHeight = measureHeight + getPaddingTop() + getPaddingBottom();
        if (widthMode != MeasureSpec.AT_MOST) {
            realWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (heightMode != MeasureSpec.AT_MOST) {
            realHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(realWidth, realHeight);
    }

    protected abstract int onMeasureWidth();

    protected abstract int onMeasureHeight();

    protected int getMeasureWidth() {
        return mMeasureWidth;
    }

    protected int getMeasureHeight() {
        return mMeasureHeight;
    }

    protected final boolean haveGravityFlag(int gravity) {
        return haveGravityFlag(mGravity, gravity);
    }

    protected final boolean haveGravityFlag(int flags, int gravity) {
        return (flags & gravity) == gravity;
    }

    /**
     * 设置偏移。只支持以下值的单一配置及合理组合:
     * <p>{@link Gravity#TOP}、{@link Gravity#BOTTOM}、{@link Gravity#LEFT}、{@link Gravity#RIGHT}、{@link Gravity#START}
     * 、{@link Gravity#END}、{@link Gravity#CENTER}、{@link Gravity#CENTER_VERTICAL}、{@link Gravity#CENTER_HORIZONTAL}。
     * 可以同时配置多个值，多个值之间用"<font color="blue">|</font>"(或)符号连接。但是不支持{@link Gravity#FILL}、
     * {@link Gravity#FILL_VERTICAL}、{@link Gravity#RELATIVE_HORIZONTAL_GRAVITY_MASK}、以及{@link Gravity#FILL_HORIZONTAL}
     * 等类似配置。
     *
     * @param gravity 要设置的值。
     */
    @SuppressLint("RtlHardcoded")
    public final void setGravity(int gravity) {
        gravity &= 0x0000_ffff;
        if (haveGravityFlag(gravity, Gravity.FILL)) {
            gravity = Gravity.LEFT | Gravity.TOP;
        }else if (haveGravityFlag(Gravity.FILL_VERTICAL)) {
            gravity = Gravity.TOP;
        }else{ if (haveGravityFlag(Gravity.FILL_HORIZONTAL)) {
            gravity = Gravity.LEFT;
        }}
        mGravity = gravity;
    }
}
