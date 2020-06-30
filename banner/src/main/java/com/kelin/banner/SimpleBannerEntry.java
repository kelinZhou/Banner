package com.kelin.banner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

/**
 * <strong>描述: </strong> 简单的Banner页面数据模型。
 * <p><strong>创建人: </strong> kelin
 * <p><strong>创建时间: </strong> 2018/3/22  上午11:10
 * <p><strong>版本: </strong> v 1.0.0
 *
 * @param <D> 该BannerEntry容器中所要盛放的对象的类型。
 */

public abstract class SimpleBannerEntry<D> implements BannerEntry<D> {

    /**
     * 获取图片的Url地址。
     *
     * @return 返回图片的Url地址的全路径。
     */
    @NonNull
    public abstract String getImageUrl();

    /**
     * 获取标题。
     * @return 返回当前条目的标题。
     */
    @Nullable
    @Override
    public CharSequence getTitle() {
        return null;
    }

    /**
     * 获取子标题。
     * @return 返回当前条目的子标题。
     */
    @Nullable
    @Override
    public CharSequence getSubTitle() {
        return null;
    }

    @Nullable
    @Override
    public D getValue() {
        return null;
    }

    /**
     * 比较两个模型是否相同。这个方法类似于 {@link #equals(Object)} 方法，但是有可能你的 {@link #equals(Object)} 方法另有用途，
     * 且与此方法的意图不一致，如果一致的话你可以直接在实现中返回 {@link #equals(Object)} 方法的返回值。
     * <p>这个方法中的比较没有必要将所有的字段进行比较，只需要区分这个模型的变更会不会影响UI视图，例如标题、图片等改变了则需要返回false，
     * 假如是一些不需要展示在UI视图上的字段发生了改变而需要展示在UI视图上的字段没有发生改变的话则需要返回true。
     * <p>当然，你也可以直接返回Object的equals方法的实现方式(比较哈希地址)，这么做也是没有什么问题的，像这里的默认实现就是返回了equals。
     * 只是在有些时候会导致没有必要的刷新UI视图。例如你的Banner在点击后是要跳转到Web页面的，当你点击后获取到要跳转的url地址后打开WebView页面。但是要跳转的url地址是不需要展示在UI上的，你过你覆盖了该方法且没有对要跳转的url地址进行比较，那么即使在用户下拉刷新后你的某一页的目标url地址发生了改变也不会重新绘制UI，从而提高性能。
     * <p>该方法在 {@link com.kelin.banner.view.BannerView#setEntries(List) BannerView.setEntries(@NonNull List<? extends BannerEntry> items)}
     * 或者 {@link com.kelin.banner.view.BannerView#setEntries(List, boolean) BannerView.setEntries(@NonNull List<? extends BannerEntry> items, boolean start)}
     * 方法调用后执行，但也不一定就会执行，只有在不是第一次调用 setEntries 方法且上一次的数据源的长度与下一次数据源的长度相同时才会调用。
     *
     * @param newEntry 要比较的对象。
     * @return 该返回值决定了参数中的对象是否与this中所有需要展示在UI视图上的字段一致，如果一致则返回true，否则返回false。如果返回了false则会在
     * {@link com.kelin.banner.view.BannerView#setEntries(List) BannerView.setEntries(@NonNull List<? extends BannerEntry> items)}
     * 或者 {@link com.kelin.banner.view.BannerView#setEntries(List, boolean) BannerView.setEntries(@NonNull List<? extends BannerEntry> items, boolean start)}
     * 执行后进行刷新UI视图，如果setEntries方法参数中的所有对象的该方法都返回了true则setEntries方法无效（在该方法被执行的前提下）。
     * @see com.kelin.banner.view.BannerView#setEntries(List) BannerView.setEntries(@NonNull List<? extends BannerEntry> items)
     * @see com.kelin.banner.view.BannerView#setEntries(List, boolean) BannerView.setEntries(@NonNull List<? extends BannerEntry> items, boolean start)
     */
    @Override
    public boolean theSame(BannerEntry newEntry) {
        return newEntry != null && TextUtils.equals(newEntry.getClass().getSimpleName(), getClass().getSimpleName()) && TextUtils.equals(getTitle(), newEntry.getTitle()) && TextUtils.equals(getSubTitle(), newEntry.getSubTitle()) && TextUtils.equals(getImageUrl(), ((SimpleBannerEntry) newEntry).getImageUrl());
    }
}
