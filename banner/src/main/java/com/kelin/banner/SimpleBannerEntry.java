package com.kelin.banner;

import android.support.annotation.NonNull;

/**
 * <strong>描述: </strong> 简单的Banner页面数据模型。
 * <p><strong>创建人: </strong> kelin
 * <p><strong>创建时间: </strong> 2018/3/22  上午11:10
 * <p><strong>版本: </strong> v 1.0.0
 */

public abstract class SimpleBannerEntry<D> implements BannerEntry<D> {

    private final D d;

    public SimpleBannerEntry(@NonNull D d) {
        this.d = d;
    }

    @Override
    public D getValue() {
        return d;
    }
}
