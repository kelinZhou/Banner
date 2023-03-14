package com.kelin.banner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.kelin.banner.R;

/**
 * **描述:** 线条型的Banner指示器。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2023/3/14 3:50 PM
 * <p>
 * **版本:** v 1.0.0
 */
public class LineIndicatorView extends BannerIndicator {

    /**
     * 线条宽度。
     */
    private int lineWidth;
    /**
     * 线条高度。
     */
    private int lineHeight;
    /**
     * 线条与线条之间的间距。
     */
    private int lineSpacing;

    /**
     * 点的颜色。
     */
    @ColorInt
    private int lineColor = 0x44000000;
    /**
     * 选中时点的颜色。
     */
    @ColorInt
    private int selectedLineColor;

    public LineIndicatorView(Context context) {
        this(context, null);
    }

    public LineIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.LineIndicatorView);
        float densityDpi = getDensityDpi();
        lineHeight = (int) (densityDpi * 4F + 0.5F);
        lineSpacing = lineHeight;
        lineWidth = (int) (densityDpi * 20F + 0.5F);
        selectedLineColor = getResources().getColor(R.color.colorAccent);
        if (typedArray != null) {
            lineHeight = (int) typedArray.getDimension(R.styleable.LineIndicatorView_lineHeight, lineHeight);
            lineWidth = (int) typedArray.getDimension(R.styleable.LineIndicatorView_lineWidth, lineWidth);
            lineSpacing = (int) typedArray.getDimension(R.styleable.LineIndicatorView_lineSpacing, lineSpacing);
            lineColor = typedArray.getColor(R.styleable.LineIndicatorView_lineColor, lineColor);
            selectedLineColor = typedArray.getColor(R.styleable.LineIndicatorView_selectedLineColor, selectedLineColor);
            typedArray.recycle();
        }
    }


    @Override
    protected int onMeasureWidth() {
        return getTotalCount() * lineWidth + ((getTotalCount() - 1) * lineSpacing);
    }

    @Override
    protected int onMeasureHeight() {
        return lineHeight;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int startX;
        int paddingLeft = getPaddingLeft();
        int width = getWidth();
        int measureWidth = getContentWidth();
        int height = getHeight();
        int measureHeight = getContentHeight();
        int startY;

        if (haveGravityFlag(Gravity.LEFT)) {
            startX = paddingLeft;
        }else if (haveGravityFlag(Gravity.RIGHT)) {
            startX = width - measureWidth + paddingLeft;
        } else if (haveGravityFlag(Gravity.CENTER_HORIZONTAL)) {
            startX = ((width - measureWidth) >>> 1) - getPaddingRight() + paddingLeft;
        } else {
            startX = paddingLeft;
        }

        int paddingTop = getPaddingTop();
        if (haveGravityFlag(Gravity.TOP)) {
            startY = paddingTop;
        }else if (haveGravityFlag(Gravity.BOTTOM)) {
            startY = height - measureHeight - getPaddingBottom();
        } else if (haveGravityFlag(Gravity.CENTER_VERTICAL)) {
            startY = (height >>> 1) - (measureHeight >>> 1) - getPaddingBottom() + paddingTop;
        } else {
            //默认按照Gravity.TOP算。
            startY = paddingTop;
        }
        Paint paint = getPaint();
        int count = getTotalCount();
        int curPosition = getCurPosition();
        RectF rect = new RectF(startX, startY, startX + lineWidth, startY + lineHeight);
        for (int i = 0; i < count; i++) {
            paint.setColor(i == curPosition ? selectedLineColor : lineColor);
            int r = lineHeight >>> 1;
            canvas.drawRoundRect(rect, r, r, paint);
            startX += lineSpacing + lineWidth;
            rect.left = startX;
            rect.right = startX + lineWidth;
        }
    }
}
