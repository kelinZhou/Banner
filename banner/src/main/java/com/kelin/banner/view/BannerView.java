package com.kelin.banner.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.TextView;
import com.kelin.banner.BannerEntry;
import com.kelin.banner.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 描述 用来显示轮播图的控件。
 * 创建人 kelin
 * 创建时间 2017/7/27  下午3:22
 * 版本 v 1.0.0
 */

public class BannerView extends ViewPager {
    private BannerHelper mBH;
    private int mPointIndicatorId;
    private int mTitleViewId;
    private int mSubTitleViewId;

    /**
     * 可以轮播有指示器。
     */
    static final int PAGING_AND_INDICATOR = 0x0000_0001;
    /**
     * 不可以轮播没有指示器。
     */
    static final int CAN_NOT_PAGING_NO_INDICATOR = 0x0000_0002;
    /**
     * 可以轮播没有指示器。
     */
    static final int CAN_PAGING_NO_INDICATOR = 0x0000_0003;
    /**
     * 不可以轮播有指示器。
     */
    static final int CAN_NOT_PAGING_HAVE_INDICATOR = 0x0000_0004;


    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            mBH = new BannerHelper(this, PAGING_AND_INDICATOR);
        } else {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
            int interpolatorId = typedArray.getResourceId(R.styleable.BannerView_interpolator, NO_ID);
            Interpolator interpolator = null;
            if (interpolatorId != NO_ID) {
                interpolator = AnimationUtils.loadInterpolator(getContext(), interpolatorId);
            }

            mBH = new BannerHelper(this,
                    typedArray.getInt(R.styleable.BannerView_singlePageMode, PAGING_AND_INDICATOR),
                    interpolator,
                    typedArray.getInt(R.styleable.BannerView_pagingIntervalTime, 0),
                    typedArray.getInt(R.styleable.BannerView_decelerateMultiple, 0));

            mPointIndicatorId = typedArray.getResourceId(R.styleable.BannerView_bannerIndicator, NO_ID);
            mTitleViewId = typedArray.getResourceId(R.styleable.BannerView_titleView, NO_ID);
            mSubTitleViewId = typedArray.getResourceId(R.styleable.BannerView_subTitleView, NO_ID);
            typedArray.recycle();
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        //这句代码是解决View复用导致的问题。重新对view的布局参数进行初始化。
        reSetLayoutParams((LayoutParams) view.getLayoutParams());
    }

    private void reSetLayoutParams(ViewPager.LayoutParams lp) {
        try {
            Field positionField = getField(ViewPager.LayoutParams.class, "position");
            if (positionField != null) {
                positionField.setInt(lp, 0);
            }
            Field widthFactorField = getField(ViewPager.LayoutParams.class, "widthFactor");
            if (widthFactorField != null) {
                widthFactorField.setFloat(lp, 0.f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field getField(Class cls,String fieldName) {
        Field positionField = null;
        try {
            positionField =cls.getDeclaredField(fieldName);
            positionField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return positionField;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        boolean addSuccess = super.addViewInLayout(child, index, params);
        ViewGroup parent = (ViewGroup) getParent();
        View view;
        if (parent != null) {
            if (mPointIndicatorId != NO_ID) {
                view = findView(parent, mPointIndicatorId);
                if (view instanceof BannerIndicator) {
                    setIndicatorView((BannerIndicator) view);
                } else {
                    throw new ClassCastException("The bannerIndicator attribute in XML must be the resource id of the BannerIndicator！");
                }
                mPointIndicatorId = NO_ID;
            }
            if (mTitleViewId != NO_ID) {
                view = findView(parent, mTitleViewId);
                if (view instanceof TextView) {
                    setTitleView((TextView) view);
                } else {
                    throw new ClassCastException("The bannerIndicator attribute in XML must be the resource id of the TextView！");
                }
                mTitleViewId = NO_ID;
            }
            if (mSubTitleViewId != NO_ID) {
                view = findView(parent, mSubTitleViewId);
                if (view instanceof TextView) {
                    setSubTitleView((TextView) view);
                } else {
                    throw new ClassCastException("The bannerIndicator attribute in XML must be the resource id of the TextView！");
                }
                mSubTitleViewId = NO_ID;
            }
        }
        return addSuccess;
    }

    private View findView(ViewGroup view, int viewId) {
        View v = view.findViewById(viewId);
        if (v == null) {
            if (view.getParent() == null) {
                throw new Resources.NotFoundException("the pointIndicator view id is not found!");
            } else {
                return findView((ViewGroup)view.getParent(), viewId);
            }
        }
        return v;
    }

    /**
     * 设置条目数据并刷新页面。
     * @param items {@link BannerEntry} 集合。
     */
    public void setEntries(List<? extends BannerEntry> items) {
        setEntries(items, true);
    }

    /**
     * 设置条目数据。
     * @param items {@link BannerEntry} 集合。
     * @param refresh 是否刷新页面。
     */
    public void setEntries(@NonNull List<? extends BannerEntry> items, boolean refresh) {
        mBH.setEntries(items, refresh);
    }

    /**
     * 设置翻页的间隔时间，单位：毫秒。
     * @param pagingIntervalTime 要设置的时长。
     */
    public void setPagingIntervalTime(@Size(min = 1000) int pagingIntervalTime) {
        mBH.setPagingIntervalTime(pagingIntervalTime);
    }

    /**
     * 设置翻页动画减速倍数。
     * @param multiple 要减速的倍数。默认为ViewPage的6倍。
     */
    public void setDecelerateMultiple(@Size(min = 2) int multiple) {
        mBH.setMultiple(multiple);
    }

    /**
     * 设置事件监听。
     * @param eventListener Banner事件监听对象。
     */
    public void setOnBannerEventListener(@NonNull BannerView.OnBannerEventListener eventListener) {
        mBH.setOnBannerEventListener(eventListener);
    }

    /**
     * 设置页面指示器控件。
     * @param indicatorView {@link BannerIndicator} 对象。
     */
    public void setIndicatorView(@NonNull BannerIndicator indicatorView) {
        mBH.setIndicatorView(indicatorView);
    }

    /**
     * 设置标题显示控件。
     * @param titleView 用来显示标题的TextView。
     */
    public void setTitleView(TextView titleView) {
        mBH.setTitleView(titleView);
    }

    /**
     * 设置副标题显示控件。
     * @param subTitleView 用来显示副标题的TextView。
     */
    public void setSubTitleView(TextView subTitleView) {
        mBH.setSubTitleView(subTitleView);
    }

    /**
     * 开始轮播。
     */
    public void start() {
        mBH.start();
    }

    /**
     * 停止轮播。
     */
    public void stop() {
        mBH.stop();
    }

    /**
     * 是否已经启动轮播。
     *
     * @return 如果已经启动播返回true，否则返回false。
     */
    public boolean isStarted() {
        return mBH.isStarted();
    }

    /**
     * 选择中间页，如果你没有调用Start()方法启动轮播的话默认是选中第一页的，如果你想移动到中间则需要调用这个方法。
     */
    public void selectCenterPage() {
        selectCenterPage(0);
    }

    /**
     * 选择中间页，如果你没有调用Start()方法启动轮播的话默认是选中第一页的，如果你想移动到中间则需要调用这个方法。
     * @param offset 向右偏移的页数。
     */
    public void selectCenterPage(int offset) {
        mBH.selectCenterPage(offset);
    }

    /**
     * 设置显示左右两边的页面，调用该方法前你必须在你的布局文件中为 {@link BannerView} 包裹一层布局。而这个布局的触摸事件默认
     * 会传递给 {@link BannerView}。
     */
    public void setShowLeftAndRightPage() {
        setShowLeftAndRightPage(0);
    }

    /**
     * 设置显示左右两边的页面，调用该方法前你必须在你的布局文件中为 {@link BannerView} 包裹一层布局。而这个布局的触摸事件默认
     * 会传递给 {@link BannerView}。
     *
     * @param showWidthDp 两边页面的宽度。单位dp。
     */
    public void setShowLeftAndRightPage(int showWidthDp) {
        setShowLeftAndRightPage(showWidthDp, true, null);
    }

    /**
     * 设置显示左右两边的页面，调用该方法前你必须在你的布局文件中为 {@link BannerView} 包裹一层布局。而这个布局的触摸事件默认
     * 会传递给 {@link BannerView}。
     *
     * @param reverseDrawingOrder 是否翻转动画。
     * @param pageTransformer     {@link ViewPager.PageTransformer} 对象。
     * @see BannerView#setPageTransformer(boolean, ViewPager.PageTransformer)
     */
    public void setShowLeftAndRightPage(boolean reverseDrawingOrder, ViewPager.PageTransformer pageTransformer) {
        setShowLeftAndRightPage(0, reverseDrawingOrder, pageTransformer);
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
    public void setShowLeftAndRightPage(int showWidthDp, boolean reverseDrawingOrder, ViewPager.PageTransformer pageTransformer) {
        mBH.setShowLeftAndRightPage(showWidthDp, reverseDrawingOrder, pageTransformer);
    }

    int determineTargetPage(int currentPage, float pageOffset) {
        try {
            Field lastMotionX = getField(ViewPager.class, "mLastMotionX");
            Field initialMotionX = getField(ViewPager.class, "mInitialMotionX");
            int deltaX = (int) (lastMotionX.getFloat(this) - initialMotionX.getFloat(this));
            Method method = ViewPager.class.getDeclaredMethod("determineTargetPage", int.class, float.class, int.class, int.class);
            method.setAccessible(true);
            return (int) method.invoke(this, currentPage, pageOffset, 0, deltaX);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Method method = ViewPager.class.getDeclaredMethod("scrollToItem", int.class, boolean.class, int.class, boolean.class);
            method.setAccessible(true);
            method.invoke(this, getCurrentItem(), false, 0, false);
            Field mFirstLayout = getField(ViewPager.class, "mFirstLayout");
            mFirstLayout.setBoolean(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBH.reStart();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBH.pause();
    }

    /**
     * 替换原本的{@link Scroller}对象。
     * @param scroller 要替换的{@link Scroller}对象。
     */
    void replaceScroller(Scroller scroller) {
        try {
            Field scrollerField = getField(ViewPager.class, "mScroller");
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 轮播图的所有事件监听类。
     */
    public static abstract class OnBannerEventListener extends ViewBannerAdapter.OnPageClickListener {

        /**
         * 页面被点击的时候执行。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        @Override
        protected abstract void onPageClick(BannerEntry entry, int index);

        /**
         * 页面被长按的时候执行。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        @Override
        protected void onPageLongClick(BannerEntry entry, int index) {
        }

        /**
         * 当页面被选中的时候调用。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        protected void onPageSelected(BannerEntry entry, int index) {
        }

        /**
         * 当页面正在滚动中的时候执行。
         *
         * @param index                当前页面的索引。这个索引永远会在你的集合的size范围内。
         * @param positionOffset       值为(0,1)表示页面位置的偏移。
         * @param positionOffsetPixels 页面偏移的像素值。
         */
        protected void onPageScrolled(int index, float positionOffset, int positionOffsetPixels) {
        }

        /**
         * 当Banner中的页面的滚动状态改变的时候被执行。
         *
         * @param state 当前的滚动状态。
         * @see BannerView#SCROLL_STATE_IDLE
         * @see BannerView#SCROLL_STATE_DRAGGING
         * @see BannerView#SCROLL_STATE_SETTLING
         */
        protected void onPageScrollStateChanged(int state) {
        }
    }
}
