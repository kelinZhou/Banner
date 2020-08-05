package com.kelin.banner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import com.kelin.banner.R;

import java.util.Locale;

/**
 * 描述 Banner的数字型指示器控件。
 * 创建人 kelin
 * 创建时间 2017/10/13  下午2:48
 * 版本 v 1.0.0
 */

public class NumberIndicatorView extends BannerIndicator {

    private static final int DEFAULT_TEXT_SIZE = 0x0000_000f;
    private static final String DEFAULT_SEPARATOR = "/";

    private float MIN_SPACE_PADDING_TOP_OR_BOTTOM;
    /**
     * 用来记录分割符号。
     */
    private String mSeparator;
    /**
     * 用来记录字体颜色。
     */
    @ColorInt
    private int mTextColor;
    /**
     * 分隔符颜色。
     */
    @ColorInt
    private int mSeparatorTextColor;
    /**
     * 当前页颜色。
     */
    @ColorInt
    private int mCurrentPageTextColor;
    /**
     * 总页数颜色。
     */
    @ColorInt
    private int mTotalPageTextColor;

    public NumberIndicatorView(Context context) {
        this(context, null);
    }

    public NumberIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("Recycle")
    public NumberIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.NumberIndicatorView);
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        int textSize = (int) (DEFAULT_TEXT_SIZE * fontScale + 0.5f);
        mTextColor = mSeparatorTextColor = mCurrentPageTextColor = mTotalPageTextColor = getPaint().getColor();
        String separator = null;
        if (typedArray != null) {
            textSize = typedArray.getDimensionPixelSize(R.styleable.NumberIndicatorView_android_textSize, textSize);
            separator = typedArray.getString(R.styleable.NumberIndicatorView_separator);
            mTextColor = typedArray.getColor(R.styleable.NumberIndicatorView_android_textColor, mTextColor);
            mSeparatorTextColor = typedArray.getColor(R.styleable.NumberIndicatorView_separatorTextColor, mTextColor);
            mCurrentPageTextColor = typedArray.getColor(R.styleable.NumberIndicatorView_currentPageTextColor, mTextColor);
            mTotalPageTextColor = typedArray.getColor(R.styleable.NumberIndicatorView_totalPageTextColor, mTextColor);
            typedArray.recycle();
        }
        MIN_SPACE_PADDING_TOP_OR_BOTTOM = (int) (0x0000_0002 * (textSize / 15.0));
        if (mSeparatorTextColor == mCurrentPageTextColor && mTotalPageTextColor == mCurrentPageTextColor) {
            //如果这三个颜色相等则将这个颜色赋值给textColor。
            if (mSeparatorTextColor != mTextColor) {
                mTextColor = mSeparatorTextColor;
                getPaint().setColor(mTextColor);
            }
        } else {
            mTextColor = Color.TRANSPARENT;
        }
        mSeparator = TextUtils.isEmpty(separator) ? DEFAULT_SEPARATOR : separator;
        getPaint().setTextSize(textSize);
    }

    /**
     * 设置分隔符号文本。
     *
     * @param separator 要设置的分割符。
     */
    public void setSeparator(String separator) {
        mSeparator = separator;
        invalidate();
    }

    /**
     * 设置所有文字的颜色。
     *
     * @param textColor 要设置的颜色。
     */
    public void setTextColor(@ColorInt int textColor) {
        if (textColor == mTextColor) {
            return;
        }
        mTextColor = mCurrentPageTextColor = mSeparatorTextColor = mTotalPageTextColor = textColor;
        getPaint().setColor(mTextColor);
        invalidate();
    }

    /**
     * 设置分隔符文字颜色。
     *
     * @param separatorTextColor 要设置的颜色。
     */
    public void setSeparatorTextColor(@ColorInt int separatorTextColor) {
        if (mSeparatorTextColor == separatorTextColor) {
            return;
        }
        if (mTextColor != separatorTextColor) {
            mTextColor = Color.TRANSPARENT;
        }
        mSeparatorTextColor = separatorTextColor;
        invalidate();
    }

    /**
     * 设置当前页文字颜色。
     *
     * @param currentPageTextColor 要设置的颜色。
     */
    public void setCurrentPageTextColor(@ColorInt int currentPageTextColor) {
        if (mCurrentPageTextColor == currentPageTextColor) {
            return;
        }
        if (mTextColor != currentPageTextColor) {
            mTextColor = Color.TRANSPARENT;
        }
        mCurrentPageTextColor = currentPageTextColor;
        invalidate();
    }

    /**
     * 设置总页数文字颜色。
     *
     * @param totalPageTextColor 要设置的颜色。
     */
    public void setTotalPageTextColor(@ColorInt int totalPageTextColor) {
        if (mTotalPageTextColor == totalPageTextColor) {
            return;
        }
        if (mTextColor != totalPageTextColor) {
            mTextColor = Color.TRANSPARENT;
        }
        mTotalPageTextColor = totalPageTextColor;
        invalidate();
    }

    @Override
    protected int onMeasureHeight() {
        return (int) (Math.abs(getPaint().ascent() + getPaint().descent()) + (MIN_SPACE_PADDING_TOP_OR_BOTTOM * 2) + 0.5f);
    }

    @Override
    protected int onMeasureWidth() {
        return (int) (getPaint().measureText(getContentText()) + 0.5f);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = getPaint();

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int width = getWidth();
        int measureWidth = getContentWidth();
        int startX;
        int height = getHeight();
        int measureHeight = getContentHeight();
        int baseLine;

        if (haveGravityFlag(Gravity.LEFT)) {
            //默认按照Gravity.START算。
            startX = paddingLeft;
        } else if (haveGravityFlag(Gravity.RIGHT)) {
            startX = width - measureWidth + paddingLeft;
        } else if (haveGravityFlag(Gravity.CENTER_HORIZONTAL)) {
            startX = ((width - measureWidth) >>> 1) - getPaddingRight() + paddingLeft;
        } else {
            //默认按照Gravity.START算。
            startX = paddingLeft;
        }

        int paddingBottom = (int) (getPaddingBottom() + MIN_SPACE_PADDING_TOP_OR_BOTTOM + 0.5f);
        if (haveGravityFlag(Gravity.TOP)) {
            //默认按照Gravity.TOP算。
            baseLine = measureHeight + paddingTop;
        } else if (haveGravityFlag(Gravity.BOTTOM)) {
            baseLine = height - paddingBottom;
        } else if (haveGravityFlag(Gravity.CENTER_VERTICAL)) {
            baseLine = (int) (((height + measureHeight) / 2f) - paddingBottom + paddingTop);
        } else {
            //默认按照Gravity.TOP算。
            baseLine = measureHeight + paddingTop;
        }
        if (mTextColor != Color.TRANSPARENT) {
            drawText(canvas, paint, getContentText(), startX, mTextColor, baseLine);
        } else {
            String curPageNum = String.valueOf(getCurPageNum());
            drawText(canvas, paint, curPageNum, startX, mCurrentPageTextColor, baseLine);

            startX += paint.measureText(curPageNum);
            drawText(canvas, paint, mSeparator, startX, mSeparatorTextColor, baseLine);

            startX += paint.measureText(mSeparator);
            drawText(canvas, paint, String.valueOf(getTotalCount()), startX, mTotalPageTextColor, baseLine);
        }
    }

    private void drawText(Canvas canvas, Paint paint, String text, float startLocation, int textColor, int y) {
        paint.setColor(textColor);
        canvas.drawText(text, startLocation, y, paint);
    }

    /**
     * 获取当前控件中文字。
     *
     * @return 返回当前控件中文字。
     */
    public String getContentText() {
        return String.format(Locale.CHINA, "%d%s%d", getCurPageNum(), mSeparator, getTotalCount());
    }

    /**
     * 获取当前的页码数字号。
     *
     * @return 返回一个可以展示给用户看的页码数字。
     */
    public int getCurPageNum() {
        return getCurPosition() + 1;
    }
}
