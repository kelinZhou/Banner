package com.kelin.banner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.kelin.banner.BannerEntry;
import com.kelin.banner.page.CenterBigTransformer;
import com.kelin.banner.page.Pageable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.kelin.banner.view.BannerView.MULTI_MODE_FROM_COVER_TO_COVER;
import static com.kelin.banner.view.BannerView.MULTI_MODE_FROM_COVER_TO_COVER_LOOP;
import static com.kelin.banner.view.BannerView.MULTI_MODE_INFINITE_LOOP;
import static com.kelin.banner.view.BannerView.SINGLE_MODE_CAN_NOT_PAGING;
import static com.kelin.banner.view.BannerView.SINGLE_MODE_NO_INDICATOR;

/**
 * 描述 Banner的帮助类。
 * 创建人 kelin
 * 创建时间 2017/7/27  下午5:02
 * 版本 v 1.0.0
 */

final class BannerHelper implements View.OnTouchListener, ViewPager.OnPageChangeListener {

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
     * 存放当前BannerView的适配器。
     */
    private ViewBannerAdapter mAdapter;
    /**
     * 用来执行翻页任务。
     */
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
    private Pageable mIndicatorView;
    /**
     * 用来记录指示器是否可用，不可用就不会显示在屏幕上。
     */
    private boolean mIndicatorEnable;
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
    private int mPageModeFlags;
    /**
     * 用来存放监听对象的容器。
     */
    private PageListenerInfo mListenerInfo;
    /**
     * 用来记录指示器控件的ID。
     */
    private int mIndicatorId;
    /**
     * 用来记录标题控件的ID。
     */
    private int mTitleViewId;
    /**
     * 用来记录子标题控件的ID。
     */
    private int mSubTitleViewId;
    /**
     * 触摸暂停轮播是否可用。
     */
    private boolean mTouchPauseEnable;
    /**
     * 自定义翻页动画。
     */
    private ViewPager.PageTransformer mTransformer;
    /**
     * 用来记录上一次的BannerView的Visibility状态。
     */
    private int lastVisibilityStatus = -1;
    /**
     * 用来翻页的任务。
     */
    private Runnable mPageDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mIsPaused) {
                if (isLastPage() && !isFirstPage()) {
                    if (getMultiPageMode() == MULTI_MODE_FROM_COVER_TO_COVER) {
                        stop();
                    } else if (getMultiPageMode() == MULTI_MODE_FROM_COVER_TO_COVER_LOOP) {
                        mBannerView.setCurrentItem(0, true);
                        mHandler.postDelayed(this, mPagingIntervalTime);
                    }
                } else {
                    mBannerView.setCurrentItem(mCurrentItem + 1, true);
                    mHandler.postDelayed(this, mPagingIntervalTime);
                }
            }
        }
    };

    /**
     * 创建Banner对象。
     *
     * @param viewPager viewPager对象。
     */
    BannerHelper(@NonNull BannerView viewPager, int pageModeFlags) {
        this(viewPager, pageModeFlags, null, 0, 0, View.NO_ID, View.NO_ID, View.NO_ID, true);
    }

    /**
     * 创建Banner对象。
     *
     * @param viewPager viewPager对象。
     */
    BannerHelper(@NonNull BannerView viewPager, int pageModeFlags, Interpolator interpolator, int pagingIntervalTime, int decelerateMultiple, int indicatorViewId, int titleViewId, int subTitleViewId, boolean touchPauseEnable) {
        mBannerView = viewPager;
        mIndicatorId = indicatorViewId;
        mTitleViewId = titleViewId;
        mSubTitleViewId = subTitleViewId;
        mPageModeFlags = pageModeFlags;
        mHandler = viewPager.getHandler() == null ? new Handler() : viewPager.getHandler();
        mScroller = new BannerScroller(viewPager.getContext(), interpolator == null ? new BannerInterpolator() : interpolator);
        replaceScroller(viewPager, mScroller);
        mAdapter = new ViewBannerAdapter();
        mTouchPauseEnable = touchPauseEnable;
        if (mTouchPauseEnable) {
            viewPager.listenerOnTouch(this);
        }
        viewPager.addPageChangeListener(this);
        setPagingIntervalTime(pagingIntervalTime);
        setMultiple(decelerateMultiple);
    }


    /**
     * 替换原本的{@link Scroller}对象。
     *
     * @param scroller 要替换的{@link Scroller}对象。
     */
    private void replaceScroller(@NonNull BannerView viewPager, Scroller scroller) {
        try {
            Field scrollerField = getField(ViewPager.class, "mScroller");
            scrollerField.set(viewPager, scroller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Field getField(Class cls, String fieldName) {
        Field positionField = null;
        try {
            positionField = cls.getDeclaredField(fieldName);
            positionField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return positionField;
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

    void setOnPageClickListener(BannerView.OnPageClickListener eventListener) {
        getPageListenerInfo().onClickListener = eventListener;
    }

    void setOnPageLongClickListener(BannerView.OnPageLongClickListener onPageLongClickListener) {
        getPageListenerInfo().onLongClickListener = onPageLongClickListener;
    }

    void setOnPageChangedListener(BannerView.OnPageChangeListener onPageChangedListener) {
        getPageListenerInfo().onChangedListener = onPageChangedListener;
    }

    /**
     * 设置页面指示器控件。
     *
     * @param indicatorView {@link Pageable} 对象。
     */
    void setIndicatorView(@NonNull View indicatorView) {
        if (indicatorView instanceof Pageable) {
            mIndicatorView = (Pageable) indicatorView;
            checkIndicatorEnable(mAdapter.getItems());
        } else {
            throw new IllegalArgumentException("the indicatorView must implements Pageable interface.");
        }
    }

    /**
     * 设置标题显示控件。
     *
     * @param titleView 用来显示标题的TextView。
     */
    void setTitleView(TextView titleView) {
        if (titleView != null) {
            titleView.setText(mAdapter.isEmpty() ? null : mAdapter.getItem(mCurrentItem).getTitle());
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
            subTitleView.setText(mAdapter.isEmpty() ? null : mAdapter.getItem(mCurrentItem).getSubTitle());
            mSubTitleView = subTitleView;
        }
    }

    /**
     * 设置条目数据。
     *
     * @param items {@link BannerEntry} 集合。
     * @param start 是否开始轮播。
     */
    void setEntries(List<? extends BannerEntry> items, boolean start) {
        if (items == null || items.isEmpty()) {
            return;
        }
        boolean update = mAdapter.setItems(items);
        if (update) {
            mBannerView.setAdapter(mAdapter.clearCache());
            findRelevantViews();
            checkIndicatorEnable(items);
            if (start) {
                if (isStarted()) {
                    stop();
                }
                start();
            } else {
                selectCenterPage(0);
            }
        }
    }

    List<? extends BannerEntry> getEntries() {
        return mAdapter.getItems();
    }

    private void checkIndicatorEnable(List<? extends BannerEntry> items) {
        if (mIndicatorView != null) {
            if (items != null && !items.isEmpty() && indicatorEnable(items)) {
                mIndicatorEnable = true;
                ((View) mIndicatorView).setVisibility(View.VISIBLE);
                mIndicatorView.setTotalPage(items.size());
            } else {
                mIndicatorEnable = false;
                ((View) mIndicatorView).setVisibility(View.GONE);
            }
        } else {
            mIndicatorEnable = false;
        }
    }

    /**
     * 判断当前是否是最后一页。
     *
     * @return 如果当前正在显示最后一页则返回true，否则返回false。
     */
    private boolean isLastPage() {
        return mAdapter.getCount() == mCurrentItem + mBannerView.getOffscreenPageLimit();
    }

    /**
     * 判断当前是否是第一页。
     *
     * @return 如果当前正在显示第一页则返回true，否则返回false。
     */
    private boolean isFirstPage() {
        return mCurrentItem % mAdapter.getItemSize() == 0;
    }

    /**
     * 判断Banner指示器是否可用。
     *
     * @param items 当前轮播图中的数据集。
     * @return 可用返回true，不可用返回false。
     */
    private boolean indicatorEnable(List<? extends BannerEntry> items) {
        return (items != null && items.size() > 1) || (getSinglePageModeFlags() & SINGLE_MODE_NO_INDICATOR) == 0;
    }

    /**
     * 判断是否可以循环翻页。
     *
     * @param items 当前轮播图中的数据集。
     * @return 可以循环翻页返回true，不可以循环翻页返回false。
     */
    private boolean canPaging(List<? extends BannerEntry> items) {
        return items != null && (items.size() > 1 || (getSinglePageModeFlags() & SINGLE_MODE_CAN_NOT_PAGING) == 0);
    }

    /**
     * 获取单页面的显示模式标识。
     */
    private int getSinglePageModeFlags() {
        return mPageModeFlags & 0x0f;
    }

    /**
     * 获取多页面的轮播模式标识。
     */
    private int getMultiPageMode() {
        return mPageModeFlags & 0xf0;
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
        List<? extends BannerEntry> items = mAdapter.getItems();
        if (items == null) {
            throw new NullPointerException("you must call setEntries method!");
        }
        if (mIsStarted && !mIsPaused) {
            return;
        }
        mIsStarted = true;
        mIsPaused = false;
        if (restoration && mIsNeverStarted) {
            mIsNeverStarted = false;
            selectCenterPage(0);
        }

        if (canPaging(items)) {
            mHandler.postDelayed(mPageDownRunnable, mPagingIntervalTime);
        }
    }

    private void reStart() {
        if (isStarted() && isPaused()) {
            start(false);
            if (isFirstPage()) {
                postInitFirstPageScrolled();
            }
        }
    }

    /**
     * 停止轮播。
     */
    void stop() {
        pause();
        mIsStarted = false;
        mCurrentItem = 0;
        mIsNeverStarted = true;
    }

    private void pause() {
        if (!isPaused() && !mIsNeverStarted) {
            mIsPaused = true;
            mHandler.removeCallbacks(mPageDownRunnable);
            if (isLastPage()) {
                if (getMultiPageMode() == MULTI_MODE_FROM_COVER_TO_COVER) {
                    mIsStarted = false;
                } else if (getMultiPageMode() == MULTI_MODE_INFINITE_LOOP) {
                    mBannerView.setCurrentItem(0, false);
                }
            }
        }
    }

    void selectCenterPage(int offset) {
        if (mAdapter.mItems != null) {
            if (mAdapter.getItemSize() == 1) {
                mBannerView.setCurrentItem(1, false);
            } else {
                int pageNum = mAdapter.getCenterPageNumber();
                if (pageNum + offset < 0 || pageNum + offset >= mAdapter.getCount()) {
                    offset = 0;
                }
                mBannerView.setCurrentItem(pageNum + offset, false);
            }
            if (mBannerView.isFirstLayout()) {
                postInitFirstPageScrolled();
            }
            setTitleView(mTitleView);
            setSubTitleView(mSubTitleView);
        } else {
            throw new RuntimeException("You need to call the 'setEntries' method before calling the 'selectCenterPage' method!");
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
     * 设置显示左右两边的页面，调用该方法前你必须在你的布局文件中为 {@link BannerView} 包裹一层布局。
     * 由于{@link BannerHelper BannerHelper}对包裹的这一层布局进行了
     * {@link View#setOnTouchListener(View.OnTouchListener)}监听触摸事件的操作，所以你不能再对其进行此操作了。
     * 否者可能会出现手指在触摸时无法停止轮播的bug。
     *
     * @param showWidthDp         两边页面的宽度。单位dp。
     * @param reverseDrawingOrder 是否翻转动画。
     * @param pageTransformer     {@link ViewPager.PageTransformer} 对象。
     * @see ViewPager#setPageTransformer(boolean, ViewPager.PageTransformer)
     */
    void setShowLeftAndRightPage(int showWidthDp, boolean reverseDrawingOrder, ViewPager.PageTransformer pageTransformer) {
        BannerView viewPager = mBannerView;
        ViewGroup pagerBox = (ViewGroup) viewPager.getParent();
        if (pagerBox == null) {
            throw new RuntimeException("BannerView cannot be a root layout!");
        }
        if (mTouchPauseEnable) {
            pagerBox.setOnTouchListener(this);
        }
        pagerBox.setClipChildren(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
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
    @SuppressLint("ClickableViewAccessibility")
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
            mIndicatorView.setCurrentPage(index);
        }
        if (mTitleView != null) {
            mTitleView.setText(mAdapter.getItem(position).getTitle());
        }
        if (mSubTitleView != null) {
            mSubTitleView.setText(mAdapter.getItem(position).getSubTitle());
        }
        if (getPageListenerInfo().onChangedListener != null) {
            getPageListenerInfo().onChangedListener.onPageSelected(mAdapter.getItem(position), index);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurPositionOffset = positionOffset;
        mCurrentTouchingPage = position;
        if (getPageListenerInfo().onChangedListener != null) {
            getPageListenerInfo().onChangedListener.onPageScrolled(mAdapter.getIndex(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING && mCurPositionOffset != NOTHING_INT && mCurrentTouchingPage != NOTHING_INT) {
            int nextPage = mBannerView.determineTargetPage(mCurrentTouchingPage, mCurPositionOffset);
            float cardinal = 1;
            if (nextPage > mCurrentTouchingPage) {
                cardinal = (1 - mCurPositionOffset) / 2;
            } else if (nextPage == mCurrentTouchingPage) {
                cardinal = mCurPositionOffset / 2;
            }
            mScroller.setCardinal(cardinal == 0 ? 1 : cardinal);
            mCurPositionOffset = NOTHING_INT;
            mCurrentTouchingPage = NOTHING_INT;
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
            mScroller.setCardinal(1);
            if ((isLastPage() || mCurrentItem == 0) && isInfiniteLoopMode()) {
                mBannerView.setCurrentItem(mAdapter.getItemSize(), false);
                if (mTransformer != null) {
                    int childCount = mBannerView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = mBannerView.getChildAt(i);
                        int layoutPosition = (int) child.getTag(ViewBannerAdapter.KEY_LAYOUT_POSITION);
                        int currentItem = mBannerView.getCurrentItem();
                        if (layoutPosition != currentItem) {
                            mTransformer.transformPage(child, layoutPosition - currentItem);
                        }
                    }
                }
            }
        }
        if (getPageListenerInfo().onChangedListener != null) {
            getPageListenerInfo().onChangedListener.onPageScrollStateChanged(state);
        }
    }


    /**
     * 页面被点击的时候执行。
     *
     * @param entry 当前页面的 {@link BannerEntry} 对象。
     * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
     */
    private void onPageClick(BannerEntry entry, int index) {
        if (getPageListenerInfo().onClickListener != null) {
            getPageListenerInfo().onClickListener.onPageClick(entry, index);
        }
    }

    /**
     * 页面被长按的时候执行。
     *
     * @param entry 当前页面的 {@link BannerEntry} 对象。
     * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
     */
    private void onPageLongClick(BannerEntry entry, int index) {
        if (getPageListenerInfo().onLongClickListener != null) {
            getPageListenerInfo().onLongClickListener.onPageLongClick(entry, index);
        }
    }

    void setSinglePageMode(int singlePageMode) {
        singlePageMode &= 0x0f;
        if ((singlePageMode == SINGLE_MODE_NO_INDICATOR
                || singlePageMode == SINGLE_MODE_CAN_NOT_PAGING
                || singlePageMode == (SINGLE_MODE_NO_INDICATOR | SINGLE_MODE_CAN_NOT_PAGING))
                && (getSinglePageModeFlags() & singlePageMode) != singlePageMode) {
            mPageModeFlags = getMultiPageMode() | singlePageMode;
        }
    }

    private PageListenerInfo getPageListenerInfo() {
        if (mListenerInfo == null) {
            mListenerInfo = new PageListenerInfo();
        }
        return mListenerInfo;
    }

    private void findRelevantViews() {
        if ((mIndicatorId & mTitleViewId & mSubTitleViewId) != View.NO_ID) {
            ViewGroup parent = (ViewGroup) mBannerView.getParent();
            if (parent != null) {
                if (mIndicatorId != View.NO_ID) {
                    setIndicatorView(findView(parent, mIndicatorId, "PointIndicator"));
                    mIndicatorId = View.NO_ID;
                }
                if (mTitleViewId != View.NO_ID) {
                    View titleView = findView(parent, mTitleViewId, "TitleView");
                    if (titleView instanceof TextView) {
                        setTitleView((TextView) titleView);
                    } else {
                        throw new ClassCastException("The bannerIndicator attribute in XML must be the resource id of the TextView！");
                    }
                    mTitleViewId = View.NO_ID;
                }
                if (mSubTitleViewId != View.NO_ID) {
                    View subTitleView = findView(parent, mSubTitleViewId, "SubTitleView");
                    if (subTitleView instanceof TextView) {
                        setSubTitleView((TextView) subTitleView);
                    } else {
                        throw new ClassCastException("The bannerIndicator attribute in XML must be the resource id of the TextView！");
                    }
                    mSubTitleViewId = View.NO_ID;
                }
            }
        }
    }

    /**
     * 是否为无限轮播模式。
     *
     * @return 如果是返回true，否则返回false。
     */
    private boolean isInfiniteLoopMode() {
        return mAdapter != null && getMultiPageMode() == MULTI_MODE_INFINITE_LOOP && canPaging(mAdapter.getItems());
    }

    private View findView(ViewGroup view, int viewId, String desc) {
        View v = view.findViewById(viewId);
        if (v == null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                return findView((ViewGroup) parent, viewId, desc);
            } else {
                throw new Resources.NotFoundException("the " + desc + " view id is not found!");
            }
        }
        return v;
    }

    void updatePageTransformer(ViewPager.PageTransformer transformer) {
        mTransformer = transformer;
    }

    void onWindowVisibilityChanged(int visibility) {
        if (lastVisibilityStatus != visibility) {
            lastVisibilityStatus = visibility;
            if (visibility == View.GONE) {
                pause();
            } else {
                findRelevantViews();
                reStart();
                try {
                    Method method = ViewPager.class.getDeclaredMethod("scrollToItem", int.class, boolean.class, int.class, boolean.class);
                    method.setAccessible(true);
                    method.invoke(mBannerView, mBannerView.getCurrentItem(), false, 0, false);
                    Field mFirstLayout = BannerHelper.getField(ViewPager.class, "mFirstLayout");
                    mFirstLayout.setBoolean(mBannerView, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void postInitFirstPageScrolled() {
        if (mTransformer != null) {
            mBannerView.post(new Runnable() {
                @Override
                public void run() {
                    final int scrollX = mBannerView.getScrollX();
                    final int childCount = mBannerView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        final View child = mBannerView.getChildAt(i);
                        final ViewPager.LayoutParams lp = (ViewPager.LayoutParams) child.getLayoutParams();

                        if (lp.isDecor) continue;
                        final float transformPos = (float) (child.getLeft() - scrollX) / (mBannerView.getMeasuredWidth() - mBannerView.getPaddingLeft() - mBannerView.getPaddingRight());
                        mTransformer.transformPage(child, transformPos);
                    }
                }
            });
        }
    }

    /**
     * Banner的页面滚动控制器。
     */
    private class BannerScroller extends Scroller {

        /**
         * 用来记录当前的基差。
         */
        private float mCardinal = 1;

        private BannerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        /**
         * 设置页面滚动的基差。
         *
         * @param cardinal 基差值。
         */
        private void setCardinal(float cardinal) {
            mCardinal = cardinal;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int multiple) {
            //根据设置的减速倍数和剩余需滚动的页面的基差重新计算出一个时长。
            int duration = (int) (multiple * (isStarted() ? mMultiple : 1) * mCardinal);
            super.startScroll(startX, startY, dx, dy, duration);
        }
    }

    private static class BannerInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    }

    /**
     * 描述 Banner的适配器。
     * 创建人 kelin
     * 创建时间 2017/7/21  下午4:14
     * 版本 v 1.0.0
     */

    private class ViewBannerAdapter extends PagerAdapter implements View.OnClickListener, View.OnLongClickListener {
        /**
         * 用来存放和获取索引的TAG。
         */
        private static final int KEY_INDEX_TAG = 0x1000_0000;
        /**
         * 用来存放和获取索引的TAG。
         */
        private static final int KEY_LAYOUT_POSITION = 0x1000_0001;
        /**
         * 用来存放所有页面的模型对象。
         */
        private List<? extends BannerEntry> mItems;
        /**
         * 用来放置可以复用的页面的View。
         */
        private SparseArray<View> itemViewCache = new SparseArray<>();
        /**
         * 缓存是否可用。
         */
        private boolean cacheAvailable = true;

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            if (!cacheAvailable) {
                itemViewCache.clear();
                cacheAvailable = true;
            }
            int index = getIndex(position);
            View entryView = itemViewCache.get(index);
            if (entryView == null) {
                BannerEntry bannerEntry = mItems.get(index);
                entryView = bannerEntry.onCreateView(container);
                if (entryView.getParent() != null) {
                    throw new IllegalStateException("The specified child already has a parent. You must call removeView() on the child's parent first.");
                }
                entryView.setTag(KEY_INDEX_TAG, index);
                if (mTouchPauseEnable) {
                    entryView.setOnTouchListener(BannerHelper.this);
                }
            } else {
                itemViewCache.remove(index);
            }
            if (getPageListenerInfo().onClickListener != null) {
                entryView.setOnClickListener(this);
            }
            if (getPageListenerInfo().onLongClickListener != null) {
                entryView.setOnLongClickListener(this);
            }
            entryView.setTag(KEY_LAYOUT_POSITION, position);
            container.addView(entryView);
            return entryView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (cacheAvailable) {
                View view = (View) object;
                container.removeView(view);
                int index = getIndex(position);
                if (itemViewCache.get(index) == null) {
                    itemViewCache.put(index, view);
                }
            }
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : isInfiniteLoopMode() ? mItems.size() * 2 + mBannerView.getOffscreenPageLimit() : mItems.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        /**
         * 获取最中间位置的第一页的位置。
         *
         * @return 返回中间的第一页的位置。
         */
        int getCenterPageNumber() {
            int count = getCount();
            if (count == 1) {
                return 0;
            } else {
                int half = count >>> 1;
                return half - half % mItems.size();
            }
        }

        /**
         * 根据position计算出真正的 index 值。
         *
         * @param position 当前的position。
         * @return 返回计算出的 index 值。
         */
        int getIndex(int position) {
            return position % mItems.size();
        }

        BannerEntry getItem(int position) {
            return mItems.get(getIndex(position));
        }

        boolean setItems(List<? extends BannerEntry> items) {
            boolean successful = true;
            if (mItems != null) {
                if (mItems == items) {
                    successful = false;
                } else if (items.size() == mItems.size()) {
                    successful = false;
                    BannerEntry entry;
                    BannerEntry newEntry;
                    for (int i = 0; i < items.size(); i++) {
                        entry = mItems.get(i);
                        newEntry = items.get(i);
                        if (!entry.theSame(newEntry)) {
                            successful = true;
                            break;
                        }
                    }
                }
            }
            if (successful) {
                mItems = items;
            }
            return successful;
        }

        @Nullable
        List<? extends BannerEntry> getItems() {
            return mItems;
        }

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag(KEY_INDEX_TAG);
            onPageClick(mItems.get(tag), tag);
        }

        @Override
        public boolean onLongClick(View v) {
            int tag = (int) v.getTag(KEY_INDEX_TAG);
            onPageLongClick(mItems.get(tag), tag);
            return true;
        }

        private boolean isEmpty() {
            return mItems == null || mItems.isEmpty();
        }

        int getItemSize() {
            return mItems == null ? 0 : mItems.size();
        }

        PagerAdapter clearCache() {
            itemViewCache.clear();
            cacheAvailable = false;
            return this;
        }
    }

    private static class PageListenerInfo {
        /**
         * Banner的页面点击事件监听。
         */
        private BannerView.OnPageClickListener onClickListener;
        /**
         * Banner的页面长按事件监听。
         */
        private BannerView.OnPageLongClickListener onLongClickListener;
        /**
         * Banner的页面改变事件监听。
         */
        private BannerView.OnPageChangeListener onChangedListener;
    }
}
