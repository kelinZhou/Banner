package com.kelin.banner.view;

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

class ViewBannerAdapter extends PagerAdapter implements View.OnClickListener, View.OnLongClickListener {
    /**
     * 用来存放和获取索引的TAG。
     */
    private static final int KEY_INDEX_TAG = 0x1000_0000;
    /**
     * 用来存放页面点击和长按的事件监听对象。
     */
    private final OnPageClickListener mClickListener;
    /**
     * 用来存放页面触摸事件的监听对象。
     */
    private final View.OnTouchListener mTouchListener;
    /**
     * 用来存放所有页面的模型对象。
     */
    private List<? extends BannerEntry> mItems;
    /**
     * 用来放置可以复用的页面的View。
     */
    private SparseArray<View> itemViewCache = new SparseArray<>();

    /**
     * 构造方法。
     * @param clickListener 页面点击事件的监听。
     * @param touchListener 页面触摸事件的监听。
     */
    ViewBannerAdapter(OnPageClickListener clickListener, View.OnTouchListener touchListener) {
        mClickListener = clickListener;
        mTouchListener = touchListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = getIndex(position);
        View entryView = itemViewCache.get(index);
        if (entryView == null) {
            BannerEntry bannerEntry = mItems.get(index);
            entryView = bannerEntry.onCreateView(container);
            entryView.setTag(KEY_INDEX_TAG, index);
            entryView.setOnClickListener(this);
            entryView.setOnLongClickListener(this);
            entryView.setOnTouchListener(mTouchListener);
        } else {
            itemViewCache.remove(index);
        }
        container.addView(entryView);
        return entryView;
    }

    @Override
    public void notifyDataSetChanged() {
        itemViewCache.clear();
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        int index = getIndex(position);
        if (itemViewCache.get(index) == null) {
            itemViewCache.put(index, view);
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
        if (mItems == items && items.size() == mItems.size()) {
            return false;
        }
        mItems = items;
        return true;
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag(KEY_INDEX_TAG);
        if (mClickListener != null) {
            mClickListener.onPageClick(mItems.get(tag), tag);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int tag = (int) v.getTag(KEY_INDEX_TAG);
        if (mClickListener != null) {
            mClickListener.onPageLongClick(mItems.get(tag), tag);
        }
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
