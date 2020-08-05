package com.kelin.banner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.kelin.banner.R;
import com.kelin.banner.page.Pageable;

/**
 * 描述 轮播图的指示器。
 * 创建人 kelin
 * 创建时间 2017/10/13  下午1:51
 * 版本 v 1.0.0
 */

public abstract class BannerIndicator extends View implements Pageable {

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
    /**
     * 用来存放所有的gravity属性。
     */
    int mGravity = Gravity.CENTER;
    /**
     * 用来记录内容区域的宽度。
     */
    private int mContentWidth;
    /**
     * 用来记录内容区域的高度。
     */
    private int mContentHeight;

    public BannerIndicator(Context context) {
        this(context, null);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("Recycle")
    public BannerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
        int gravity = Gravity.CENTER;
        if (typedArray != null) {
            mTotalCount = typedArray.getInt(R.styleable.BannerIndicator_totalCount, 0);
            gravity = typedArray.getInt(R.styleable.BannerIndicator_android_gravity, gravity);
            typedArray.recycle();
        }
        setGravity(gravity);
    }

    /**
     * 设置点的个数，如果你是配合 {@link BannerView } 使用的话，这不需要进行设置。只需要调用
     * {@link BannerView#setIndicatorView(View)} 方法或者为{@link BannerView}
     * 配置"app:bannerIndicator="@+id/banner_view"属性即可。
     *
     * @param totalPage 总的数量。
     * @see BannerView#setIndicatorView(View)
     */
    @Override
    public final void setTotalPage(int totalPage) {
        mTotalCount = totalPage;
    }

    /**
     * 设置当前选中的位置。如果你是配合 {@link BannerView Banner} 使用的话，这不需要进行设置。只需要调用
     * {@link BannerView#setIndicatorView(View)} 方法或者为{@link BannerView}
     * 配置"app:bannerIndicator"属性即可。
     *
     * @param position 当前的索引位置。
     */
    @Override
    public final void setCurrentPage(int position) {
        mCurPosition = position;
        requestLayout();
    }

    /**
     * 获取总的数量。
     *
     * @return 返回总的数量。
     */
    public final int getTotalCount() {
        return mTotalCount;
    }

    /**
     * 获取当前的指针位置。
     *
     * @return 返回当前的指针位置。
     */
    public final int getCurPosition() {
        return mCurPosition;
    }

    public final Paint getPaint() {
        return mPaint;
    }

    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = mContentWidth = onMeasureWidth();
        int realWidth = measureWidth + getPaddingLeft() + getPaddingRight();
        int measureHeight = mContentHeight = onMeasureHeight();
        int realHeight = measureHeight + getPaddingTop() + getPaddingBottom();
        if (widthMode != MeasureSpec.AT_MOST) {
            realWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (heightMode != MeasureSpec.AT_MOST) {
            realHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(realWidth, realHeight);
    }

    /**
     * 当需要测量宽度的时候调用。你不需要考虑padding的存在，只需要将你所需要的最小宽度测量出来并返回就好了。
     *
     * @return 返回你测量过后的所得到的不包含padding的宽度。
     */
    protected abstract int onMeasureWidth();

    /**
     * 当需要测量高度的时候调用。你不需要考虑padding的存在，只需要将你所需要的最小高度测量出来并返回就好了。
     *
     * @return 返回你测量过后的所得到的不包含padding的高度。
     */
    protected abstract int onMeasureHeight();

    /**
     * 获取内容区域的宽度。
     *
     * @return 返回内容区域所占用的宽度，其实这个值就是{@link #onMeasureWidth()}方法的返回值。
     * @see #onMeasureWidth()
     */
    protected final int getContentWidth() {
        return mContentWidth;
    }

    /**
     * 获取内容区域的高度。
     *
     * @return 返回内容区域所占用的高度，其实这个值就是{@link #onMeasureHeight()}方法的返回值。
     * @see #onMeasureHeight()
     */
    protected final int getContentHeight() {
        return mContentHeight;
    }

    /**
     * 判断当前所有的Gravity属性中是否包含某个属性。
     *
     * @param gravity 要判断的是否包含的属性。
     * @return 如果包含则返回true，否则返回false。
     */
    protected final boolean haveGravityFlag(int gravity) {
        return haveFlag(mGravity, gravity);
    }

    /**
     * 判断某个flag标识容器中是否存在指定的标识。
     *
     * @param flags 标识容器。
     * @param flag  要判断的标识。
     * @return 存在则返回true，否则返回false。
     */
    protected final boolean haveFlag(int flags, int flag) {
        return (flags & flag) == flag;
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
        gravity &= 0x00ff;
        if (haveFlag(gravity, Gravity.FILL)) {
            gravity = Gravity.LEFT | Gravity.TOP;
        } else if (haveFlag(gravity, Gravity.FILL_VERTICAL)) {
            gravity = Gravity.TOP;
        } else {
            if (haveFlag(gravity, Gravity.FILL_HORIZONTAL)) {
                gravity = Gravity.LEFT;
            }
        }
        mGravity = gravity;
    }
}
