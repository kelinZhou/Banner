package com.kelin.recycleradapter.interfaces;

import android.view.View;

import com.kelin.recycleradapter.holder.ItemViewHolder;

/**
 * 描述 条目事件绑定的拦截器。
 * 创建人 kelin
 * 创建时间 2017/6/15  上午11:12
 * 版本 v 1.0.0
 */

public interface EventBindInterceptor {
    /**
     * 当需要给 {@link ItemViewHolder} 的 {@link ItemViewHolder#onGetNeedListenerChildViewIds()} 返回的View绑定事件时调用。
     * 如果你想对这个控件设置特殊的监听则可以通过该方法进行拦截，然后设置你所需要的监听，因为适配器默认只是实现点击监听。
     * <p>例如：当前要绑定事件的View你除了关心的它的点击事件外还想关心它的其他事件(比如滑动事件)则可以通过这个方法进行拦截，
     * 然后你依然可以返回<code>false</code>,这样你就既可以绑定你所需要的特殊事件监听也可以通过适配器帮你绑定点击事件。
     * 当然，如果你不希望适配器帮你绑定点击事件则可以返回<code>true</code>,告诉适配器你需要拦截这次绑定，这样适配器就不会再绑定点击事件了。
     * <p>
     * <h1><font color="#619BE5">注意：</font> </h1>
     * <p>  这个拦截只能拦截条目中的子View的点击事件，不能拦截条目点击事件。如果你不希望触发某个类型的条目点击事件则可以通过覆盖
     * {@link ItemViewHolder}的{@link ItemViewHolder#clickable()}方法进行拦截。
     *
     * @param v    要绑定的View。
     * @param item 当前的 {@link LayoutItem} 对象。
     * @return 是否拦截绑定，如果拦截则不会对其进行绑定点击事件，否则将会为其绑定点击事件。
     */
    boolean onInterceptor(View v, LayoutItem item);
}
