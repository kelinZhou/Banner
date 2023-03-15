package com.kelin.banner.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kelin.banner.R;

/**
 * **描述:** 滑动加载更多组件。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2023/3/15 10:37 AM
 * <p>
 * **版本:** v 1.0.0
 */
public class SlideShowMoreLayout extends LinearLayout {

    private OverScroller mScroller = new OverScroller(getContext());

    private BannerView mBanner;
    /**
     * 用来记录上一次处理的加载更多的状态，方式重复调用。
     */
    private boolean lastLoadMoreState = false;
    /**
     * 加载更多布局最小漏出多少认为可以加载更多。
     */
    private int minShowMoreWidth;
    /**
     * 加载更多布局的宽度。
     */
    private int showMoreWidth;
    /**
     * 滑动加载更多的提示文字。
     */
    private String slideLoadMoreText;
    /**
     * 释放加载更多的提示文字。
     */
    private String releaseLoadMoreText;
    /**
     * 上一次按下的X轴坐标。
     */
    private float lastX;
    /**
     * 用来记录当前是否拦截滑动事件。
     */
    boolean needInterceptSlide;
    /**
     * 加载更多的箭头图标。
     */
    private int showMoreArrowIcon;
    /**
     * 加载更多的箭头组件。
     */
    private ImageView arrowIconView;
    /**
     * 加载更多的显示更多的提示文字组件。
     */
    private TextView showMoreTextView;
    /**
     * 加载更多的监听。
     */
    private OnLoadMoreListener onLoadMoreListener;

    public SlideShowMoreLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowMoreLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideShowMoreLayout);
        showMoreArrowIcon = typedArray.getResourceId(R.styleable.SlideShowMoreLayout_arrowIcon, R.drawable.kelin_banner_load_more_ic_arrow_left_circle);
        slideLoadMoreText = typedArray.getString(R.styleable.SlideShowMoreLayout_slideLoadMoreText);
        if (TextUtils.isEmpty(slideLoadMoreText)) {
            slideLoadMoreText = getResources().getString(R.string.kelin_banner_def_slide_load_more_text);
        }
        releaseLoadMoreText = typedArray.getString(R.styleable.SlideShowMoreLayout_releaseLoadMoreText);
        if (TextUtils.isEmpty(releaseLoadMoreText)) {
            releaseLoadMoreText = getResources().getString(R.string.kelin_banner_def_release_load_more_text);
        }
        typedArray.recycle();
    }

    /**
     * 设置加载更多的监听。
     * @param l 监听对象。
     */
    public void setOnLoadMoreListener(OnLoadMoreListener l) {
        this.onLoadMoreListener = l;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        showMoreWidth = getChildAt(1).getMeasuredWidth();
        minShowMoreWidth = showMoreWidth >>> 1;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBanner = (BannerView) getChildAt(0);
        LayoutInflater.from(getContext()).inflate(R.layout.kelin_banner_layout_show_more, this);
        arrowIconView = findViewById(R.id.ivBannerShowMoreArrow);
        arrowIconView.setImageResource(showMoreArrowIcon);
        showMoreTextView = findViewById(R.id.tvBannerShowMoreText);
        showMoreTextView.setText(slideLoadMoreText);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //如果Banner的当前页已经是最后一页并且当前为向左滑动。
                if (mBanner.getCurrentItem() == mBanner.getEntries().size() - 1 && lastX > ev.getX()) {
                    needInterceptSlide = true; //拦截滑动事件。
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return needInterceptSlide;
        } else {
            return false;
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = lastX - event.getX();
                if (dx != 0) {
                    if (dx > 0) {  //左滑
                        if (getScrollX() + dx >= showMoreWidth || dx >= showMoreWidth) {
                            scrollTo(showMoreWidth, 0);
                            return super.onTouchEvent(event);
                        }
                    } else { //右滑
                        if (getScrollX() + dx <= 0) {
                            scrollTo(0, 0);
                            return super.onTouchEvent(event);
                        }
                    }
                }
                checkLoadMoreState(getScrollX() + dx);
                scrollBy((int) dx, 0);
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                smoothToNormal();
                if (onLoadMoreListener != null && lastLoadMoreState) {
                    onLoadMoreListener.onLoadMore();
                }
                needInterceptSlide = false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            checkLoadMoreState(0);
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private void smoothToNormal() {
        mScroller.forceFinished(true);
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 300);
        invalidate();
    }

    private void checkLoadMoreState(float scrollX) {
        boolean showLoadMore = scrollX >= minShowMoreWidth;
        if (lastLoadMoreState != showLoadMore) {
            lastLoadMoreState = showLoadMore;
            ObjectAnimator animation;
            if (showLoadMore) {
                //释放查看详情
                showMoreTextView.setText(releaseLoadMoreText);
                animation = ObjectAnimator.ofFloat(arrowIconView, "rotation", 0, 180);
            } else {
                //滑动查看详情
                showMoreTextView.setText(slideLoadMoreText);
                animation = ObjectAnimator.ofFloat(arrowIconView, "rotation", 180, 0);
            }
            animation.setDuration(500);
            animation.start();
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
