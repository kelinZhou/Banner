package com.kelin.banner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

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

    private static final int MIN_SPACE_PADDING_TOP_OR_BOTTOM = 0x0000_0006;
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
    /**
     * 用来记录测量过后的所有文字的宽度。
     */
    private float mMeasureTextWidth;

    public NumberIndicatorView(Context context) {
        this(context, null);
    }

    public NumberIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitAttrs(Context context, AttributeSet attrs) {
        super.onInitAttrs(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberIndicatorView);
        int textSize = sp2px(DEFAULT_TEXT_SIZE);
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
     * @param separator 要设置的分割符。
     */
    public void setSeparator(String separator) {
        mSeparator = separator;
        invalidate();
    }

    /**
     * 设置所有文字的颜色。
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
    protected void onMeasureWidthAndHeight(int[] rect) {
        mMeasureTextWidth = getPaint().measureText(getContentText());
        rect[0] = (int) (getPaddingLeft() + getPaddingRight() + mMeasureTextWidth + 0.5f);
        rect[1] = (int) (getPaddingTop() + getPaddingBottom() + Math.abs(getPaint().ascent() + getPaint().descent()) + (MIN_SPACE_PADDING_TOP_OR_BOTTOM << 1) + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String contentText = getContentText();
        Paint paint = getPaint();
        if (mTextColor != Color.TRANSPARENT) {
            canvas.drawText(contentText, getPaddingLeft(), getHeight() - MIN_SPACE_PADDING_TOP_OR_BOTTOM - getPaddingBottom(), paint);
        } else {
            String curPageNum = String.valueOf(getCurPageNum());

            float startLocation = getPaddingLeft() + (getWidth() - mMeasureTextWidth) / 2;
            drawText(canvas, paint, curPageNum, startLocation, mCurrentPageTextColor);

            startLocation += paint.measureText(curPageNum);
            drawText(canvas, paint, mSeparator, startLocation, mSeparatorTextColor);

            startLocation += paint.measureText(mSeparator);
            drawText(canvas, paint, String.valueOf(getTotalCount()), startLocation, mTotalPageTextColor);
        }
    }

    private void drawText(Canvas canvas, Paint paint, String text, float startLocation, int textColor) {
        paint.setColor(textColor);
        canvas.drawText(text, startLocation, getHeight() - MIN_SPACE_PADDING_TOP_OR_BOTTOM - getPaddingBottom(), paint);
    }

    private String getContentText() {
        return String.format(Locale.CHINA, "%d%s%d", getCurPageNum(), mSeparator, getTotalCount());
    }

    private int getCurPageNum() {
        return getCurPosition() + 1;
    }

    private int sp2px(float sp) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }
}
