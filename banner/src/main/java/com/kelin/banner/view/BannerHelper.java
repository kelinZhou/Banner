package com.kelin.banner.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.kelin.banner.BannerEntry;
import com.kelin.banner.page.CenterBigTransformer;

import java.util.List;

/**
 * 描述 Banner的帮助类。
 * 创建人 kelin
 * 创建时间 2017/7/27  下午5:02
 * 版本 v 1.0.0
 */

final class BannerHelper extends ViewBannerAdapter.OnPageClickListener implements View.OnTouchListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "--Banner";

    private static final int NOTHING_INT = 0xffff_ffff;
    /**
     * 当前的ViewPage对象。
     */
    private final BannerView mBannerView;
    /**
     * 记录是否已经开始轮播。
     */
    private boolean mIsStarted;
    /**
     * 翻页的间隔时长。
     */
    private int mPagingIntervalTime = 5000;

    /**
     * 用来翻页的任务。
     */
    private Runnable mPageDownRunnable = new Runnable() {
        @Override
        public void run() {
            mBannerView.setCurrentItem(mCurrentItem + 1, true);
            mHandler.postDelayed(this, mPagingIntervalTime);
        }
    };
    /**
     * 存放当前BannerView的适配器。
     */
    private ViewBannerAdapter mAdapter;
    /**
     * Banner的事件监听对象。
     */
    private BannerView.OnBannerEventListener mEventListener;
    private Handler mHandler;
    /**
     * 记录是否已经被销毁。
     */
    private boolean mIsPaused;
    /**
     * 记录上一次的触摸动作。
     */
    private int mLastAction = NOTHING_INT;
    /**
     * 当前的页面偏移值。
     */
    private float mCurPositionOffset = NOTHING_INT;
    /**
     * 当前正在被触摸的页数。
     */
    private int mCurrentTouchingPage = NOTHING_INT;
    /**
     * 当前的Scroller对象。
     */
    private BannerScroller mScroller;
    /**
     * 是否从未被启动过。
     */
    private boolean mIsNeverStarted = true;
    /**
     * 当前被选中的Item位置。
     */
    private int mCurrentItem;
    /**
     * 轮播图的页码指示器控件。
     */
    private BannerIndicator mIndicatorView;
    /**
     * 翻页动画减速倍数。
     */
    private int mMultiple = 1;
    /**
     * 用来显示标题的控件。
     */
    private TextView mTitleView;
    /**
     * 用来显示副标题的控件。
     */
    private TextView mSubTitleView;
    /**
     * 只有一张图片是的显示模式。
     */
    private int mSinglePageMode;
    /**
     * 用来记录指示器是否可用。
     */
    private boolean mIndicatorEnable;

    /**
     * 创建Banner对象。
     *
     * @param viewPager viewPager对象。
     */
    BannerHelper(@NonNull BannerView viewPager, int singlePageMode) {
        this(viewPager, singlePageMode, null, 0, 0);
    }

    /**
     * 创建Banner对象。
     *
     * @param viewPager viewPager对象。
     */
    BannerHelper(@NonNull BannerView viewPager, int singlePageMode, Interpolator interpolator, int pagingIntervalTime, int decelerateMultiple) {
        mBannerView = viewPager;
        mHandler = viewPager.getHandler() == null ? new Handler() : viewPager.getHandler();
        mScroller = new BannerScroller(viewPager.getContext(), interpolator == null ? new BannerInterpolator() : interpolator);
        mBannerView.replaceScroller(mScroller);
        mAdapter = new ViewBannerAdapter(this, this, singlePageMode);
        mSinglePageMode = singlePageMode;
        viewPager.setAdapter(mAdapter);
        viewPager.setOnTouchListener(this);
        viewPager.addOnPageChangeListener(this);
        setPagingIntervalTime(pagingIntervalTime);
        setMultiple(decelerateMultiple);
    }

    /**
     * 设置翻页的间隔时间，单位：毫秒。
     *
     * @param pagingIntervalTime 要设置的时长。
     */
    void setPagingIntervalTime(@Size(min = 1000) int pagingIntervalTime) {
        if (pagingIntervalTime >= 1) {
            mPagingIntervalTime = pagingIntervalTime;
        }
    }

    /**
     * 设置翻页动画减速倍数。
     *
     * @param multiple 要减速的倍数。默认为ViewPage的6倍。
     */
    void setMultiple(@Size(min = 2) int multiple) {
        if (multiple > 1) {
            mMultiple = multiple;
        }
    }

    /**
     * 设置事件监听。
     *
     * @param eventListener Banner事件监听对象。
     */
    void setOnBannerEventListener(BannerView.OnBannerEventListener eventListener) {
        mEventListener = eventListener;
    }

    /**
     * 设置页面指示器控件。
     *
     * @param indicatorView {@link BannerIndicator} 对象。
     */
    void setIndicatorView(@NonNull BannerIndicator indicatorView) {
        mIndicatorView = indicatorView;
        checkIndicatorEnable(mAdapter.getItems());
    }

    /**
     * 设置标题显示控件。
     *
     * @param titleView 用来显示标题的TextView。
     */
    void setTitleView(TextView titleView) {
        if (titleView != null) {
            titleView.setText(mAdapter.getItem(mCurrentItem).getTitle());
            mTitleView = titleView;
        }
    }

    /**
     * 设置副标题显示控件。
     *
     * @param subTitleView 用来显示副标题的TextView。
     */
    void setSubTitleView(TextView subTitleView) {
        if (subTitleView != null) {
            subTitleView.setText(mAdapter.getItem(mCurrentItem).getSubTitle());
            mSubTitleView = subTitleView;
        }
    }

    /**
     * 设置条目数据。
     *
     * @param items   {@link BannerEntry} 集合。
     * @param refresh 是否刷新页面。
     */
    void setEntries(List<? extends BannerEntry> items, boolean refresh) {
        checkIndicatorEnable(items);
        boolean update = mAdapter.setItems(items);
        if (refresh && update) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void checkIndicatorEnable(List<? extends BannerEntry> items) {
        if (mIndicatorView != null) {
            if (indicatorEnable(items)) {
                mIndicatorEnable = true;
                mIndicatorView.setVisibility(View.VISIBLE);
                mIndicatorView.setTotalCount(items.size());
            } else {
                mIndicatorEnable = false;
                mIndicatorView.setVisibility(View.GONE);
            }
        } else {
            mIndicatorEnable = false;
        }
    }

    /**
     * 判断Banner指示器是否可用。
     * @param items 当前轮播图中的数据集。
     * @return 可用返回true，不可用返回false。
     */
    private boolean indicatorEnable(List<? extends BannerEntry> items) {
        return items.size() > 1 || (mSinglePageMode == BannerView.CAN_NOT_PAGING_HAVE_INDICATOR || mSinglePageMode == BannerView.PAGING_AND_INDICATOR);
    }

    /**
     * 判断是否可以翻页。
     * @param items 当前轮播图中的数据集。
     * @return 可以翻页返回true，不可翻页返回false。
     */
    private boolean canPaging(List<? extends BannerEntry> items) {
        return items.size() > 1 || (mSinglePageMode == BannerView.CAN_PAGING_NO_INDICATOR || mSinglePageMode == BannerView.PAGING_AND_INDICATOR);
    }

    /**
     * 开始轮播。
     */
    void start() {
        start(true);
    }

    /**
     * 开始轮播。
     *
     * @param restoration 是否复位。
     */
    private void start(boolean restoration) {
        mHandler.removeCallbacksAndMessages(null);
        if (mIsStarted && !mIsPaused) {
            return;
        }
        mIsStarted = true;
        mIsPaused = false;
        if (restoration && mIsNeverStarted) {
            mIsNeverStarted = false;
            selectCenterPage(0);
        }

        if (canPaging(mAdapter.getItems())) {
            mHandler.postDelayed(mPageDownRunnable, mPagingIntervalTime);
        }
    }

    void selectCenterPage(int offset) {
        mBannerView.setCurrentItem(mAdapter.getCenterPageNumber() + offset);
    }

    /**
     * 停止轮播。
     */
    void stop() {
        pause();
        mIsStarted = false;
    }

    void pause() {
        if (!isPaused() && !mIsNeverStarted) {
            mIsPaused = true;
            mHandler.removeCallbacks(mPageDownRunnable);
        }
    }

    /**
     * 是否已经启动轮播。
     *
     * @return 如果已经启动播返回true，否则返回false。
     */
    boolean isStarted() {
        return mIsStarted;
    }

    private boolean isPaused() {
        return mIsPaused;
    }

    /**
     * 设置显示左右两边的页面，调用该方法前你必须在你的布局文件中为 {@link BannerView} 包裹一层布局。而这个布局的触摸事件默认
     * 会传递给 {@link BannerView}。
     *
     * @param showWidthDp         两边页面的宽度。单位dp。
     * @param reverseDrawingOrder 是否翻转动画。
     * @param pageTransformer     {@link ViewPager.PageTransformer} 对象。
     * @see ViewPager#setPageTransformer(boolean, ViewPager.PageTransformer)
     */
    void setShowLeftAndRightPage(int showWidthDp, boolean reverseDrawingOrder, ViewPager.PageTransformer pageTransformer) {
        BannerView viewPager = mBannerView;
        ViewGroup pagerBox = (ViewGroup) viewPager.getParent();
        pagerBox.setOnTouchListener(this);
        pagerBox.setClipChildren(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(2);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
        int margin = dp2px(showWidthDp > 0 ? showWidthDp : 12);
        lp.setMargins(margin, lp.topMargin, margin, lp.bottomMargin);
        viewPager.setLayoutParams(lp);
        viewPager.setPageTransformer(reverseDrawingOrder, pageTransformer == null ? new CenterBigTransformer(0.9f) : pageTransformer);
    }

    private int dp2px(int dp) {
        final float scale = mBannerView.getContext().getResources().getDisplayMetrics().densityDpi;
        return (int) (dp * (scale / 160) + 0.5f);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int parseAction = parseAction(action);
        if (mLastAction != parseAction) {
            mLastAction = parseAction;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    pause();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    reStart();
            }
        }
        return view == mBannerView ? mBannerView.onTouchEvent(motionEvent) : view == mBannerView.getParent() ? mBannerView.dispatchTouchEvent(motionEvent) : view.onTouchEvent(motionEvent);
    }

    void reStart() {
        if (isStarted() && isPaused()) {
            start(false);
        }
    }

    private int parseAction(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                return 0;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return 1;
            default:
                return NOTHING_INT;
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;
        int index = mAdapter.getIndex(position);
        if (mIndicatorEnable) {
            mIndicatorView.setCurPosition(index);
        }
        if (mTitleView != null) {
            mTitleView.setText(mAdapter.getItem(position).getTitle());
        }
        if (mSubTitleView != null) {
            mSubTitleView.setText(mAdapter.getItem(position).getSubTitle());
        }
        if (position == 0 || position == mAdapter.getCount()) {
            stop();
            pause();
        } else {
            if (mEventListener != null) {
                mEventListener.onPageSelected(mAdapter.getItem(position), index);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIsPaused) {
            mCurPositionOffset = positionOffset;
            mCurrentTouchingPage = position;
        }
        if (mEventListener != null) {
            mEventListener.onPageScrolled(mAdapter.getIndex(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING && mCurPositionOffset != NOTHING_INT && mCurrentTouchingPage != NOTHING_INT) {
            int nextPage = mBannerView.determineTargetPage(mCurrentTouchingPage, mCurPositionOffset);
            if (nextPage > mCurrentTouchingPage) {
                mScroller.setCardinal((1 - mCurPositionOffset) / 2);
            } else if (nextPage == mCurrentTouchingPage) {
                mScroller.setCardinal(mCurPositionOffset / 2);
            }
            mCurPositionOffset = NOTHING_INT;
            mCurrentTouchingPage = NOTHING_INT;
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
            mScroller.setCardinal(1);
        }
        if (mEventListener != null) {
            mEventListener.onPageScrollStateChanged(state);
        }
    }


    /**
     * 页面被点击的时候执行。
     *
     * @param entry 当前页面的 {@link BannerEntry} 对象。
     * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
     */
    @Override
    protected void onPageClick(BannerEntry entry, int index) {
        if (mEventListener != null) {
            mEventListener.onPageClick(entry, index);
        }
    }

    /**
     * 页面被长按的时候执行。
     *
     * @param entry 当前页面的 {@link BannerEntry} 对象。
     * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
     */
    @Override
    protected void onPageLongClick(BannerEntry entry, int index) {
        if (mEventListener != null) {
            mEventListener.onPageLongClick(entry, index);
        }
    }

    private class BannerScroller extends Scroller {

        private float mCardinal = 1;

        private BannerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        private void setCardinal(float cardinal) {
            mCardinal = cardinal;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int multiple) {
            int duration = (int) (multiple * mMultiple * mCardinal);
            super.startScroll(startX, startY, dx, dy, duration);
        }
    }

    private class BannerInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    }
}
