package com.kelin.recycleradapter.interfaces;

/**
 * 描述 描述可以被拦截事件绑定的类。
 * 创建人 kelin
 * 创建时间 2017/6/28  下午5:07
 * 版本 v 1.0.0
 */

public interface EventInterceptor {

    /**
     * 设置事件绑定拦截器。
     * @param interceptor {@link EventBindInterceptor} 拦截器对象。
     */
    void setEventInterceptor(EventBindInterceptor interceptor);
}
