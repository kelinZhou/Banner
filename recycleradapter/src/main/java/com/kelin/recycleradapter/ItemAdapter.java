package com.kelin.recycleradapter;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.RecyclerView;

import com.kelin.recycleradapter.holder.ItemViewHolder;
import com.kelin.recycleradapter.interfaces.AdapterEdit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 描述 {@link RecyclerView} 的适配器 {@link MultiTypeAdapter} 的子适配器。
 * 创建人 kelin
 * 创建时间 2017/1/19  下午4:22
 * 版本 v 1.0.0
 */

public class ItemAdapter<D> extends SuperItemAdapter<D> implements AdapterEdit<D> {

    /**
     * 用来记录条目的占屏比。
     */
    private int mItemSpanSize;

    public ItemAdapter(@NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        this(holderClass, null);
    }

    public ItemAdapter(@NonNull Class<? extends ItemViewHolder<D>> holderClass, D d) {
        this(SPAN_SIZE_FULL_SCREEN, holderClass, d);
    }

    public ItemAdapter(@Size(min = 1, max = 100) int spanSize, @NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        this(null, spanSize, holderClass);
    }

    public ItemAdapter(@Size(min = 1, max = 100) int spanSize, @NonNull Class<? extends ItemViewHolder<D>> holderClass, D d) {
        this(null, spanSize, holderClass);
        if (d != null) {
            addItem(getDataList().size(), d, false);
        }
    }

    public ItemAdapter(List<D> list, @NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        this(list, SPAN_SIZE_FULL_SCREEN, holderClass);
    }

    public ItemAdapter(List<D> list, @Size(min = 1, max = 100) int spanSize, @NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        super(list, holderClass);
        mItemSpanSize = spanSize <= 0 ? SPAN_SIZE_FULL_SCREEN : spanSize;
    }

    /**
     * 设置数据。
     *
     * @param list 数据集合。
     */
    public void setDataList(List<D> list) {
        mDataList = list != null ? list : new ArrayList<D>();
    }

    /**
     * 在列表的末尾处添加一个条目。
     *
     * @param object 要添加的对象。
     */
    @Override
    public void addItem(@NonNull D object) {
        addItem(object, true);
    }

    /**
     * 在列表的末尾处添加一个条目。
     *
     * @param object  要添加的对象。
     * @param refresh 是否刷新列表。
     */
    @Override
    public void addItem(@NonNull D object, boolean refresh) {
        addItem(getDataList().size(), object, refresh);
    }

    /**
     * 在列表的指定位置添加一个条目。
     *
     * @param position 要添加的位置。
     * @param object   要添加的对象。
     */
    @Override
    public void addItem(int position, @NonNull D object) {
        addItem(position, object, true);
    }

    /**
     * 在列表的指定位置添加一个条目。
     *
     * @param position 要添加的位置。
     * @param object   要添加的对象。
     * @param refresh  是否刷新列表。
     */
    @Override
    public void addItem(int position, @NonNull D object, boolean refresh) {
        getDataList().add(position, object);
        mAdapterDataObservable.add(position, object);
        if (refresh) {
            mapNotifyItemInserted(position);
        }
    }

    /**
     * 批量增加Item。
     *
     * @param datum 要增加Item。
     */
    @Override
    public void addAll(@NonNull Collection<D> datum) {
        addAll(datum, true);
    }

    /**
     * 批量增加Item。
     *
     * @param positionStart 批量增加的其实位置。
     * @param datum         要增加Item。
     */
    @Override
    public void addAll(@Size(min = 0) int positionStart, @NonNull Collection<D> datum) {
        addAll(positionStart, datum, true);
    }

    /**
     * 批量增加Item。
     *
     * @param datum   要增加Item。
     * @param refresh 是否在增加完成后刷新条目。
     */
    @Override
    public void addAll(@NonNull Collection<D> datum, boolean refresh) {
        addAll(-1, datum, refresh);
    }

    /**
     * 批量增加Item。
     *
     * @param positionStart 批量增加的起始位置。
     * @param datum         要增加Item。
     * @param refresh       是否在增加完成后刷新条目。
     */
    @Override
    public void addAll(@Size(min = 0) int positionStart, @NonNull Collection<D> datum, boolean refresh) {
        if (datum.isEmpty()) return;
        if (positionStart < 0) {
            positionStart = getDataList().size();
        }
        boolean addAll = getDataList().addAll(positionStart, datum);
        if (addAll) {
            mAdapterDataObservable.addAll(positionStart, datum);
            if (refresh) {
                mapNotifyItemRangeInserted(positionStart, datum.size());
            }
        }
    }

    /**
     * 移除指定位置的条目。
     *
     * @param position 要移除的条目的位置。
     * @return 返回被移除的对象。
     */
    @Override
    public D removeItem(@Size(min = 0) int position) {
        return removeItem(position, true);
    }

    /**
     * 移除指定位置的条目。
     *
     * @param position 要移除的条目的位置。
     * @param refresh  是否在移除完成后刷新列表。
     * @return 返回被移除的对象。
     */
    @Override
    public D removeItem(@Size(min = 0) int position, boolean refresh) {
        if (position < 0) return null;
        if (!isEmptyList()) {
            D d = getDataList().remove(position);
            if (d != null) {
                mAdapterDataObservable.remove(d);
                if (refresh) {
                    mapNotifyItemRemoved(position);
                }
            }
            return d;
        } else {
            return null;
        }
    }

    /**
     * 将指定的对象从列表中移除。
     *
     * @param object 要移除的对象。
     * @return 移除成功返回改对象所在的位置，移除失败返回-1。
     */
    @Override
    public int removeItem(@NonNull D object) {
        return removeItem(object, true);
    }

    /**
     * 将指定的对象从列表中移除。
     *
     * @param object  要移除的对象。
     * @param refresh 是否在移除完成后刷新列表。
     * @return 移除成功返回改对象所在的位置，移除失败返回-1。
     */
    @Override
    public int removeItem(@NonNull D object, boolean refresh) {
        if (!isEmptyList()) {
            int position;
            List<D> dataList = getDataList();
            position = dataList.indexOf(object);
            if (position != -1) {
                boolean remove = dataList.remove(object);
                if (remove) {
                    mAdapterDataObservable.remove(object);
                    if (refresh) {
                        mapNotifyItemRemoved(position);
                    }
                }
            }
            return position;
        } else {
            return -1;
        }
    }

    /**
     * 批量移除条目。
     *
     * @param positionStart 开始移除的位置。
     * @param itemCount     要移除的条目数。
     */
    @Override
    public void removeAll(@Size(min = 0) int positionStart, int itemCount) {
        removeAll(positionStart, itemCount, true);
    }

    /**
     * 批量移除条目。
     *
     * @param positionStart 开始移除的位置。
     * @param itemCount     要移除的条目数。
     * @param refresh       是否在移除成功后刷新列表。
     */
    @Override
    public void removeAll(@Size(min = 0) int positionStart, @Size(min = 0) int itemCount, boolean refresh) {
        if (positionStart < 0 || itemCount < 0)
            throw new IllegalArgumentException("the positionStart Arguments or itemCount Arguments must is greater than 0 integer");
        if (!isEmptyList()) {
            List<D> dataList = getDataList();
            int positionEnd = positionStart + (itemCount = itemCount > dataList.size() ? dataList.size() : itemCount);
            List<D> temp = new ArrayList<>();
            for (int i = positionStart; i < positionEnd; i++) {
                temp.add(dataList.get(i));
            }
            boolean removeAll = dataList.removeAll(temp);
            if (removeAll) {
                mAdapterDataObservable.removeAll(temp);
                if (refresh) {
                    mapNotifyItemRangeRemoved(positionStart, itemCount);
                }
            }
        }
    }

    /**
     * 批量移除条目。
     *
     * @param datum 要移除的条目的数据模型对象。
     */
    @Override
    public void removeAll(@NonNull Collection<D> datum) {
        removeAll(datum, true);
    }

    /**
     * 批量移除条目。
     *
     * @param datum   要移除的条目的数据模型对象。
     * @param refresh 是否在移除成功后刷新列表。
     */
    @Override
    public void removeAll(@NonNull Collection<D> datum, boolean refresh) {
        if (!isEmptyList() && !datum.isEmpty()) {
            List<D> dataList = getDataList();
            Iterator<D> iterator = datum.iterator();
            D d = iterator.next();
            int positionStart = dataList.indexOf(d);
            boolean removeAll = dataList.removeAll(datum);
            if (removeAll) {
                mAdapterDataObservable.removeAll(datum);
                if (refresh) {
                    mapNotifyItemRangeRemoved(positionStart, datum.size());
                }
            }
        }
    }

    /**
     * 清空列表。
     */
    @Override
    public void clear() {
        clear(true);
    }

    /**
     * 清空列表。
     *
     * @param refresh 在清空完成后是否刷新列表。
     */
    @Override
    public void clear(boolean refresh) {
        if (!isEmptyList()) {
            List<D> dataList = getDataList();
            dataList.clear();
            mAdapterDataObservable.removeAll(dataList);
            if (refresh) {
                mapNotifyItemRangeRemoved(0, dataList.size());
            }
        }
    }

    /**
     * 获取条目的占屏比。这个方法的返回值是您在构造该对象时通过构造方法: {@link #ItemAdapter(int, Class)}、
     * {@link #ItemAdapter(int, Class, Object)}、{@link #ItemAdapter(List, int, Class)} 中的 spanSize参数设置进来的。
     * 如果您不是调用这几个构造方法构造的该对象，那么这个方法的返回值则为 {@link #SPAN_SIZE_FULL_SCREEN}。
     *
     * @return 返回当前条目的占屏比。
     */
    @Override
    public int getItemSpanSize() {
        return mItemSpanSize;
    }
}
