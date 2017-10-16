package com.kelin.banner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private int mSeparatorTextColor;
    private int mCurrentPageTextColor;
    private int mTotalPageTextColor;
    private int mTextColor;
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
            getPaint().setTextAlign(Paint.Align.CENTER);
        } else {
            mTextColor = Color.TRANSPARENT;
        }
        mSeparator = TextUtils.isEmpty(separator) ? DEFAULT_SEPARATOR : separator;
        getPaint().setTextSize(textSize);
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
            canvas.drawText(contentText, getWidth() / 2, getHeight() - MIN_SPACE_PADDING_TOP_OR_BOTTOM - getPaddingBottom(), paint);
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
