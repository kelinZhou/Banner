package com.kelin.recycleradapter.callback;

/**
 * 描述 条目拖拽结果的监听。
 * 创建人 kelin
 * 创建时间 2017/7/3  下午2:25
 * 版本 v 1.0.0
 */

public interface ItemDragResultListener<D> {

    void onItemMoved(int fromPosition, int toPosition, D d);

    void onItemDismissed(int position, D d);
}
