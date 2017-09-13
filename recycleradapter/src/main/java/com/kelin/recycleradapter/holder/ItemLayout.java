package com.kelin.recycleradapter.holder;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述 用来获取跟布局资源文件 {@link LayoutRes} 的注解。
 * 创建人 kelin
 * 创建时间 2017/3/27  下午1:57
 * 版本 v 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ItemLayout {
    /**
     * 条目的布局文件。
     */
    @LayoutRes int value();
}
