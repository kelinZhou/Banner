package com.kelin.banner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.kelin.banner.R;

/**
 * 描述 Banner的点型指示器控件。
 * 创建人 kelin
 * 创建时间 2017/7/25  下午6:06
 * 版本 v 1.0.0
 */

public class PointIndicatorView extends BannerIndicator {

    /**
     * 默认的点的颜色。
     */
    private static final int COLOR_DEFAULT_POINT_COLOR = 0x44FFFFFF;
    /**
     * 默认的被选中的点的颜色。
     */
    private static final int COLOR_DEFAULT_SELECTED_POINT_COLOR = 0xFFFFFFFF;
    /**
     * 表示未被选中的点为空心。
     */
    private static final int STYLE_HOLLOW_NORMAL = 0x01;
    /**
     * 表示被选中的点为空心。
     */
    private static final int STYLE_HOLLOW_SELECTED = 0x02;

    /**
     * 点的颜色。
     */
    @ColorInt
    private int mPointColor = COLOR_DEFAULT_POINT_COLOR;
    /**
     * 选中时点的颜色。
     */
    @ColorInt
    private int mSelectedPointColor = COLOR_DEFAULT_SELECTED_POINT_COLOR;
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
    private int mNormalPointDiameter;
    /**
     * 被选中的点的直径。
     */
    private int mSelectedPointDiameter;
    /**
     * 点与点之间的间距。
     */
    private float mPointSpacing;
    /**
     * 点的空心样式。
     */
    private int hollowStyle;
    /**
     * 边框线的宽度。
     */
    private int strokeWidth;

    public PointIndicatorView(Context context) {
        this(context, null);
    }


    public PointIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("Recycle")
    public PointIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.PointIndicatorView);
        float scale = getResources().getDisplayMetrics().densityDpi;
        float defaultPointRadius = (int) (3 * (scale / 160) + 0.5f);
        float selectedPointRadius = 0;
        int normalPointDiameter = 0;
        int selectedPointDiameter = 0;
        float pointSpacing = 0;
        if (typedArray != null) {
            mPointColor = typedArray.getColor(R.styleable.PointIndicatorView_pointColor, COLOR_DEFAULT_POINT_COLOR);
            mSelectedPointColor = typedArray.getColor(R.styleable.PointIndicatorView_selectedPointColor, COLOR_DEFAULT_SELECTED_POINT_COLOR);
            defaultPointRadius = typedArray.getDimension(R.styleable.PointIndicatorView_pointRadius, defaultPointRadius);
            normalPointDiameter = (int) (defaultPointRadius * 2);
            selectedPointRadius = typedArray.getDimension(R.styleable.PointIndicatorView_selectedPointRadius, defaultPointRadius);
            selectedPointDiameter = (int) (selectedPointRadius * 2);
            pointSpacing = typedArray.getDimension(R.styleable.PointIndicatorView_pointSpacing, Math.min(normalPointDiameter, selectedPointDiameter));
            hollowStyle = typedArray.getInt(R.styleable.PointIndicatorView_hollowStyle, 0);
            float sw = typedArray.getDimension(R.styleable.PointIndicatorView_strokeSize, 0);
            strokeWidth = (int) (sw < 0 ? 0 : sw + 2);
            getPaint().setStrokeWidth(sw < 0 ? 0 : sw);
            typedArray.recycle();
        }
        mPointRadius = defaultPointRadius;
        mNormalPointDiameter = normalPointDiameter == 0 ? (int) (mPointRadius * 2) : normalPointDiameter;
        mSelectedPointRadius = selectedPointRadius == 0 ? mPointRadius : selectedPointRadius;
        mSelectedPointDiameter = selectedPointDiameter == 0 ? (int) (mSelectedPointRadius * 2) : selectedPointDiameter;
        mPointSpacing = pointSpacing;
    }

    /**
     * 设置点颜色。
     *
     * @param pointColor 要设置的颜色，默认为{@link #COLOR_DEFAULT_POINT_COLOR}。
     */
    public void setPointColor(@ColorInt int pointColor) {
        mPointColor = pointColor;
    }

    /**
     * 设置被选中的点的颜色。
     *
     * @param selectedPointColor 要设置的颜色，默认为{@link #COLOR_DEFAULT_SELECTED_POINT_COLOR}。
     */
    public void setSelectedPointColor(@ColorInt int selectedPointColor) {
        mSelectedPointColor = selectedPointColor;
    }

    /**
     * 设置点的半径。
     *
     * @param pointRadius 要设置的点的半径的px值, 默认为3dp。
     */
    public void setPointRadius(float pointRadius) {
        mPointRadius = pointRadius;
        mNormalPointDiameter = (int) (mPointRadius * 2);
    }

    /**
     * 设置选中时点的半径。
     *
     * @param pointRadius 要设置的点的半径的px值，默认和未选中时一样。
     */
    public void setSelectedPointRadius(float pointRadius) {
        mSelectedPointRadius = pointRadius;
        mSelectedPointDiameter = (int) (mSelectedPointRadius * 2);
    }

    /**
     * 设置点与点之间的间距。
     *
     * @param spacing 要设置的间距的px值，默认为点的选中和未选中状态下最小的那个直径。
     */
    public void setPointSpacing(float spacing) {
        mPointSpacing = spacing;
    }

    @Override
    protected int onMeasureWidth() {
        //所有点需要占用的宽度 + 所有间距的宽度 = 总宽度。
        int totalCount = getTotalCount();
        return (totalCount - 1) * mNormalPointDiameter + mSelectedPointDiameter + (int) ((totalCount - 1) * mPointSpacing) + strokeWidth * totalCount;
    }

    @Override
    protected int onMeasureHeight() {
        //最大的点的直径 = 总高度。
        return Math.max(mSelectedPointDiameter + strokeWidth, mNormalPointDiameter + strokeWidth);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int width = getWidth();
        int measureWidth = getContentWidth();
        int startX;
        int height = getHeight();
        int measureHeight = getContentHeight();
        int centerY;

        int halfStroke = strokeWidth / 2;
        if (haveGravityFlag(Gravity.LEFT)) {
            //默认按照Gravity.START算。
            startX = paddingLeft + halfStroke;
        } else if (haveGravityFlag(Gravity.RIGHT)) {
            startX = width - measureWidth + paddingLeft + halfStroke;
        } else if (haveGravityFlag(Gravity.CENTER_HORIZONTAL)) {
            startX = ((width - measureWidth) >>> 1) - getPaddingRight() + paddingLeft + halfStroke;
        } else {
            //默认按照Gravity.START算。
            startX = paddingLeft  + halfStroke;
        }

        if (haveGravityFlag(Gravity.TOP)) {
            //默认按照Gravity.TOP算。
            centerY = (measureHeight >>> 1) + paddingTop;
        } else if (haveGravityFlag(Gravity.BOTTOM)) {
            centerY = height - (measureHeight >>> 1) - getPaddingBottom();
        } else if (haveGravityFlag(Gravity.CENTER_VERTICAL)) {
            centerY = (height >>> 1) - getPaddingBottom() + paddingTop;
        } else {
            //默认按照Gravity.TOP算。
            centerY = (measureHeight >>> 1) + paddingTop;
        }


        float radius;
        int diameter;
        Paint paint = getPaint();
        int curPosition = getCurPosition();
        boolean hollowNormal = (hollowStyle & STYLE_HOLLOW_NORMAL) != 0;
        boolean hollowSelected = (hollowStyle & STYLE_HOLLOW_SELECTED) != 0;
        for (int i = 0; i < getTotalCount(); i++) {
            if (i == curPosition) {
                radius = mSelectedPointRadius;
                diameter = mSelectedPointDiameter;
                paint.setColor(mSelectedPointColor);
                paint.setStyle(hollowSelected ? Paint.Style.STROKE : strokeWidth > 0 ? Paint.Style.FILL_AND_STROKE : Paint.Style.FILL);
            } else {
                radius = mPointRadius;
                diameter = mNormalPointDiameter;
                paint.setColor(mPointColor);
                paint.setStyle(hollowNormal ? Paint.Style.STROKE : strokeWidth > 0 ? Paint.Style.FILL_AND_STROKE : Paint.Style.FILL);
            }
            canvas.drawCircle(startX + radius, centerY, radius, paint);
            startX += diameter + mPointSpacing;
        }
    }
}
