package com.kelin.banner.view;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.kelin.banner.BannerEntry;

import java.util.List;

/**
 * 描述 Banner的适配器。
 * 创建人 kelin
 * 创建时间 2017/7/21  下午4:14
 * 版本 v 1.0.0
 */

class ViewBannerAdapter extends PagerAdapter {

    private final OnPageClickListener mClickListener;
    private final View.OnTouchListener mTouchListener;
    private List<? extends BannerEntry> mItems;
    private SparseArray<View> itemViews;

    ViewBannerAdapter(@NonNull OnPageClickListener clickListener, View.OnTouchListener touchListener) {
        itemViews = new SparseArray<>();
        mClickListener = clickListener;
        mTouchListener = touchListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int index = getIndex(position);
        View entryView = itemViews.get(index);
        if (entryView == null) {
            final BannerEntry bannerEntry = mItems.get(index);
            entryView = bannerEntry.onCreateView(container);
            entryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onPageClick(bannerEntry, index);
                }
            });
            entryView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onPageLongClick(bannerEntry, index);
                    return true;
                }
            });
            entryView.setOnTouchListener(mTouchListener);
        } else {
            itemViews.remove(index);
        }
        container.addView(entryView);
        return entryView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        int index = getIndex(position);
        if (itemViews.get(index) == null) {
            itemViews.put(index, view);
        }
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 获取最中间位置的第一页的位置。
     * @return 返回中间的第一页的位置。
     */
    int getCenterPageNumber() {
        int half = getCount() >>> 1;
        return half - half % mItems.size();
    }

    /**
     * 根据position计算出真正的 index 值。
     * @param position 当前的position。
     * @return 返回计算出的 index 值。
     */
    int getIndex(int position) {
        return position % mItems.size();
    }

    BannerEntry getItem(int position) {
        return mItems.get(getIndex(position));
    }

    int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    boolean setItems(List<? extends BannerEntry> items) {
        if (mItems == items) {
            return false;
        }
        mItems = items;
        return true;
    }

    static abstract class OnPageClickListener {
        /**
         * 页面被点击的时候执行。
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        protected abstract void onPageClick(BannerEntry entry, int index);

        /**
         * 页面被长按的时候执行。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        protected abstract void onPageLongClick(BannerEntry entry, int index);
    }
}
