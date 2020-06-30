package com.kelin.banner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 描述 Banner中的模型
 * 创建人 kelin
 * 创建时间 2017/7/21  下午4:01
 * 版本 v 1.0.0
 */

public interface BannerEntry<VALUE> {

    /**
     * 获取当前页面的布局视图。由于{@link com.kelin.banner.view.BannerHelper BannerHelper}对返回的View进行了
     * {@link View#setOnTouchListener(View.OnTouchListener)}监听触摸事件的操作，所以你不能再对返回的View进行此操作了。
     * 否者可能会出现手指在触摸时无法停止轮播的bug。
     * @param parent 当前的布局视图的父节点布局。
     * @return 返回当前页面所要显示的View。
     */
    @NonNull
    View onCreateView(ViewGroup parent);

    /**
     * 获取标题。
     * @return 返回当前条目的标题。
     */
    @Nullable
    CharSequence getTitle();

    /**
     * 获取子标题。
     * @return 返回当前条目的子标题。
     */
    @Nullable
    CharSequence getSubTitle();

    /**
     * 获取当前页面的数据。改方法为辅助方法，是为了方便使用者调用而提供的，Api本身并没有任何调用。如果你不需要该方法可以空实现。
     * @return 返回当前页面的数据。
     */
    @Nullable
    VALUE getValue();

    /**
     * 比较两个模型是否相同。这个方法类似于 {@link #equals(Object)} 方法，但是有可能你的 {@link #equals(Object)} 方法另有用途，
     * 且与此方法的意图不一致，如果一致的话你可以直接在实现中返回 {@link #equals(Object)} 方法的返回值。
     * <p>这个方法中的比较没有必要将所有的字段进行比较，只需要区分这个模型的变更会不会影响UI视图，例如标题、图片等改变了则需要返回false，
     * 假如是一些不需要展示在UI视图上的字段发生了改变而需要展示在UI视图上的字段没有发生改变的话则需要返回true。
     * <p>当然，你也可以直接返回Object的equals方法的实现方式(比较哈希地址)，这么做也是没有什么问题的，只是在有些时候会导致没有必要的刷新UI视图。例如你的Banner在点击后是要跳转到Web页面的，当你点击后获取到要跳转的url地址后打开WebView页面。但是要跳转的url地址是不需要展示在UI上的，你过你覆盖了该方法且没有对要跳转的url地址进行比较，那么即使在用户下拉刷新后你的某一页的目标url地址发生了改变也不会重新绘制UI，从而提高性能。
     * <p>该方法在 {@link com.kelin.banner.view.BannerView#setEntries(List) BannerView.setEntries(@NonNull List<? extends BannerEntry> items)}
     * 或者 {@link com.kelin.banner.view.BannerView#setEntries(List, boolean) BannerView.setEntries(@NonNull List<? extends BannerEntry> items, boolean start)}
     * 方法调用后执行，但也不一定就会执行，只有在不是第一次调用 setEntries 方法且上一次的数据源的长度与下一次数据源的长度相同时才会调用。
     * @param newEntry 要比较的对象。
     * @return 该返回值决定了参数中的对象是否与this中所有需要展示在UI视图上的字段一致，如果一致则返回true，否则返回false。如果返回了false则会在
     *  {@link com.kelin.banner.view.BannerView#setEntries(List) BannerView.setEntries(@NonNull List<? extends BannerEntry> items)}
     * 或者 {@link com.kelin.banner.view.BannerView#setEntries(List, boolean) BannerView.setEntries(@NonNull List<? extends BannerEntry> items, boolean start)}
     * 执行后进行刷新UI视图，如果setEntries方法参数中的所有对象的该方法都返回了true则setEntries方法无效（在该方法被执行的前提下）。
     *
     * @see com.kelin.banner.view.BannerView#setEntries(List) BannerView.setEntries(@NonNull List<? extends BannerEntry> items)
     * @see com.kelin.banner.view.BannerView#setEntries(List, boolean) BannerView.setEntries(@NonNull List<? extends BannerEntry> items, boolean start)
     */
    boolean theSame(BannerEntry newEntry);
}
