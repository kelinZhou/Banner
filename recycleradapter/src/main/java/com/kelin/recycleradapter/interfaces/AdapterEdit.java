package com.kelin.recycleradapter.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.Collection;

/**
 * 描述 用来描述Adapter中的条目创建、编辑操作的类。
 * 创建人 kelin
 * 创建时间 2017/5/2  上午10:54
 * 版本 v 1.0.0
 */

public interface AdapterEdit<D> {

    /**
     * 在列表的末尾处添加一个条目。
     *
     * @param object 要添加的对象。
     */
    void addItem(@NonNull D object);

    /**
     * 在列表的末尾处添加一个条目。
     *
     * @param object 要添加的对象。
     * @param refresh  是否刷新列表。
     */
    void addItem(@NonNull D object, boolean refresh);

    /**
     * 在列表的指定位置添加一个条目。
     *
     * @param position 要添加的位置。
     * @param object   要添加的对象。
     */
    void addItem(int position, @NonNull D object);

    /**
     * 在列表的指定位置添加一个条目。
     *
     * @param position 要添加的位置。
     * @param object   要添加的对象。
     * @param refresh  是否刷新列表。
     */
    void addItem(int position, @NonNull D object, boolean refresh);

    /**
     * 批量增加Item。
     *
     * @param datum 要增加Item。
     */
    void addAll(@NonNull Collection<D> datum);

    /**
     * 批量增加Item。
     *
     * @param positionStart 批量增加的其实位置。
     * @param datum         要增加Item。
     */
    void addAll(@Size(min = 0) int positionStart, @NonNull Collection<D> datum);

    /**
     * 批量增加Item。
     *
     * @param datum   要增加Item。
     * @param refresh 是否在增加完成后刷新条目。
     */
    void addAll(@NonNull Collection<D> datum, boolean refresh);

    /**
     * 批量增加Item。
     *
     * @param positionStart 批量增加的其实位置。
     * @param datum         要增加Item。
     * @param refresh       是否在增加完成后刷新条目。
     */
    void addAll(@Size(min = 0) int positionStart, @NonNull Collection<D> datum, boolean refresh);

    /**
     * 移除指定位置的条目。
     *
     * @param position 要移除的条目的位置。
     * @return 返回被移除的对象。
     */
    D removeItem(@Size(min = 0) int position);

    /**
     * 移除指定位置的条目。
     *
     * @param position 要移除的条目的位置。
     * @param refresh  是否在移除完成后刷新列表。
     * @return 返回被移除的对象。
     */
    D removeItem(@Size(min = 0) int position, boolean refresh);

    /**
     * 将指定的对象从列表中移除。
     *
     * @param object 要移除的对象。
     * @return 移除成功返回改对象所在的位置，移除失败返回-1。
     */
    int removeItem(@NonNull D object);

    /**
     * 将指定的对象从列表中移除。
     *
     * @param object  要移除的对象。
     * @param refresh 是否在移除完成后刷新列表。
     * @return 移除成功返回改对象所在的位置，移除失败返回-1。
     */
    int removeItem(@NonNull D object, boolean refresh);

    /**
     * 批量移除条目。
     *
     * @param positionStart 开始移除的位置。
     * @param itemCount     要移除的条目数。
     */
    void removeAll(@Size(min = 0) int positionStart, int itemCount);

    /**
     * 批量移除条目。
     *
     * @param positionStart 开始移除的位置。
     * @param itemCount     要移除的条目数。
     * @param refresh       是否在移除成功后刷新列表。
     */
    void removeAll(@Size(min = 0) int positionStart, @Size(min = 0) int itemCount, boolean refresh);

    /**
     * 批量移除条目。
     *
     * @param datum 要移除的条目的数据模型对象。
     */
    void removeAll(@NonNull Collection<D> datum);

    /**
     * 批量移除条目。
     *
     * @param datum   要移除的条目的数据模型对象。
     * @param refresh 是否在移除成功后刷新列表。
     */
    void removeAll(@NonNull Collection<D> datum, boolean refresh);

    /**
     * 清空列表。
     */
    void clear();

    /**
     * 清空列表。
     *
     * @param refresh 在清空完成后是否刷新列表。
     */
    void clear(boolean refresh);
}
