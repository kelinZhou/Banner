package com.kelin.banner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.kelin.banner.R;

/**
 * 描述 Banner的指示器控件。
 * 创建人 kelin
 * 创建时间 2017/7/25  下午6:06
 * 版本 v 1.0.0
 */

public class BannerIndicatorView extends View {

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 用来记录点的个数。
     */
    private int mPointCount;
    /**
     * 用来记录当前的位置。
     */
    private int mCurPosition;
    /**
     * 点的颜色。
     */
    @ColorInt
    private int mPointColor;
    /**
     * 选中时点的颜色。
     */
    @ColorInt
    private int mSelectedPointColor;
    /**
     * 点的半径。
     */
    private float mPointRadius;
    /**
     * 选中的点的半径，默认为普通的点的半径。
     */
    private float mSelectedPointRadius;
    /**
     * 点的直径。
     */
    private int mPointDiameter;
    private int mSelectedPointDiameter;
    private float mPointSpacing;

    public BannerIndicatorView(Context context) {
        this(context, null);
    }


    public BannerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        if (attrs != null) {
            initAttrs(context, attrs);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicatorView);
        mPointCount = typedArray.getInt(R.styleable.BannerIndicatorView_pointCount, 0);
        mPointColor = typedArray.getColor(R.styleable.BannerIndicatorView_pointColor, 0x44FFFFFF);
        mSelectedPointColor = typedArray.getColor(R.styleable.BannerIndicatorView_selectedPointColor, Color.WHITE);
        final float scale = getResources().getDisplayMetrics().densityDpi;
        int defaultPointRadius = (int) (3 * (scale / 160) + 0.5f);
        mPointRadius = typedArray.getDimension(R.styleable.BannerIndicatorView_pointRadius, defaultPointRadius);
        mPointDiameter = (int) (mPointRadius * 2);
        mSelectedPointRadius = typedArray.getDimension(R.styleable.BannerIndicatorView_selectedPointRadius, mPointRadius);
        mSelectedPointDiameter = (int) (mSelectedPointRadius * 2);
        mPointSpacing = typedArray.getDimension(R.styleable.BannerIndicatorView_pointSpacing, Math.min(mPointDiameter, mSelectedPointDiameter));
        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(0);
    }

    /**
     * 设置点的个数，如果你是配合 {@link BannerView } 使用的话，这不需要进行设置。只需要调用
     * {@link BannerView#setPointIndicatorView(BannerIndicatorView)} 方法即可。
     * @param pointCount 点的数量。
     * @see BannerView#setPointIndicatorView(BannerIndicatorView)
     */
    public void setPointCount(int pointCount) {
        mPointCount = pointCount;
    }

    /**
     * 设置当前选中的位置。如果你是配合 {@link BannerView Banner} 使用的话，这不需要进行设置。只需要调用
     * {@link BannerView#setPointIndicatorView(BannerIndicatorView)} 方法即可。
     * @param position 要选中的位置。
     */
    public void setCurPosition(int position) {
        mCurPosition = position;
        requestLayout();
    }

    /**
     * 设置点颜色。
     * @param pointColor 要设置的颜色。
     */
    public void setPointColor(@ColorInt int pointColor) {
        mPointColor = pointColor;
    }

    /**
     * 设置被选中的点的颜色。
     * @param selectedPointColor 要设置的颜色。
     */
    public void setSelectedPointColor(@ColorInt int selectedPointColor) {
        mSelectedPointColor = selectedPointColor;
    }

    /**
     * 设置点的半径。
     * @param pointRadius 要设置的点的半径的px值。
     */
    public void setPointRadius(float pointRadius) {
        mPointRadius = pointRadius;
        mPointDiameter = (int) (mPointRadius * 2);
    }

    /**
     * 设置选中时点的半径。
     * @param pointRadius 要设置的点的半径的px值。
     */
    public void setSelectedPointRadius(float pointRadius) {
        mSelectedPointRadius = pointRadius;
        mSelectedPointDiameter = (int) (mSelectedPointRadius * 2);
    }

    /**
     * 设置点与点之间的间距。
     * @param spacing 要设置的间距的px值。
     */
    public void setPointSpacing(float spacing) {
        mPointSpacing = spacing;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //上padding + 下padding + 最大的点的直径 = 总高度。
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getPaddingTop() + getPaddingBottom() + Math.max(mSelectedPointDiameter, mPointDiameter), heightMode);
        //左padding + 右padding + 所有点需要占用的宽度 + 所有间距的宽度 = 总宽度。
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getPaddingStart() + getPaddingEnd() + ((mPointCount - 1) * mPointDiameter) + mSelectedPointDiameter + (int) ((mPointCount - 1) * mPointSpacing), widthMode);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius;
        float startX = getPaddingStart();
        int diameter;
        for (int i = 0; i < mPointCount; i++) {
            if (i == mCurPosition) {
                radius = mSelectedPointRadius;
                diameter = mSelectedPointDiameter;
                mPaint.setColor(mSelectedPointColor);
            } else {
                radius = mPointRadius;
                diameter = mPointDiameter;
                mPaint.setColor(mPointColor);
            }
            canvas.drawCircle(startX + radius, getHeight() / 2, radius, mPaint);
            startX +=  diameter + mPointSpacing;
        }
    }
}
