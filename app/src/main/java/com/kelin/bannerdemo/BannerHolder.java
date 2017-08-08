package com.kelin.bannerdemo;

import android.view.View;

import com.kelin.banner.view.BannerView;
import com.kelin.recycleradapter.holder.ItemLayout;
import com.kelin.recycleradapter.holder.ItemViewHolder;

import java.util.List;

/**
 * 创建人 kelin
 * 创建时间 2017/7/26  下午6:00
 * 版本 v 1.0.0
 */
@ItemLayout(R.layout.item_banner)
class BannerHolder extends ItemViewHolder<List<ImageBannerEntry>> {

    private final BannerView mBanner;

    protected BannerHolder(final View itemView) {
        super(itemView);
        mBanner = getView(R.id.vp_view_pager);
        mBanner.setShowLeftAndRightPage();
    }

    @Override
    public void onBindData(int i, List<ImageBannerEntry> bannerEntries) {
        mBanner.setEntries(bannerEntries);
        mBanner.start();
    }

    @Override
    public int[] onGetNeedListenerChildViewIds() {
        return new int[]{R.id.vp_view_pager};
    }
}
