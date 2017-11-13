package com.kelin.banner.page;

/**
 * <strong>描述: </strong> 描述可以翻页的类。
 * <p><strong>创建人: </strong> kelin
 * <p><strong>创建时间: </strong> 2017/11/13  上午11:21
 * <p><strong>版本: </strong> v 1.0.0
 */

public interface Pageable {
    /**
     * 设置总页数。
     * @param totalPage 总页数。
     */
    void setTotalPage(int totalPage);

    /**
     * 设置当前页。
     * @param curPage 当前页码。
     */
    void setCurrentPage(int curPage);
}
