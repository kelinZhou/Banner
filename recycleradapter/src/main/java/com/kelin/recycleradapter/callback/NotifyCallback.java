package com.kelin.recycleradapter.callback;

import android.os.Bundle;

/**
 * 描述 当需要刷新数据列表的时候的回调对象。用来描述数据源是否发生了变化。
 * 创建人 kelin
 * 创建时间 2017/3/27  下午6:16
 * 版本 v 1.0.0
 */

public interface NotifyCallback<D> {

    /**
     * 判断两个数据模型是否相像。如果指定模型有唯一标识应当以唯一标识去判断。
     * @param oldItemData 旧的数据模型。
     * @param newItemDate 新的数据模型。
     * @return 如果相同则应当返回 <code color="blue">true</code>,否则应当返回 <code color="blue">false</code>。
     */
    boolean areItemsTheSame(D oldItemData, D newItemDate);

    /**
     * 判断两个模型的内容是否相同。
     * <p>当 {@link #areItemsTheSame(Object, Object)} 方法返回 <code>true</code> 时，此方法才会被调用，这是因为如果两个对象
     * 的基本特征都是不同的或，就没有进行进一步比较的必要了。
     * <p>你不必将模型中的所有字段进行比较，只需要将需要展示到UI上的字段进行比较就可以了，你也可以将这个比较放到
     * {@link #equals(Object)} 方法中去做。
     * @param oldItemData 旧的数据模型。
     * @param newItemDate 新的数据模型。
     * @return 如果相同则返回 <code color="blue">true</code>, 否则应当返回 <code color="blue">false</code>。
     *
     * @see #areItemsTheSame(D, D);
     */
    boolean areContentsTheSame(D oldItemData, D newItemDate);

    /**
     * 获取两个对象不同的部分。
     * @param oldItemData 旧的数据模型。
     * @param newItemDate 新的数据模型。
     * @param bundle 比较两个对象不同的部分，将两个对象不同的部分存入该参数中。
     */
    void getChangePayload(D oldItemData, D newItemDate, Bundle bundle);
}
