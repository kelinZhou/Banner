package com.kelin.recycleradapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kelin.recycleradapter.holder.CommonNoDataViewHolder;
import com.kelin.recycleradapter.holder.ItemViewHolder;
import com.kelin.recycleradapter.holder.ViewHelper;
import com.kelin.recycleradapter.interfaces.Orientation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述 多条目的 {@link RecyclerView} 的适配器。
 * 创建人 kelin
 * 创建时间 2016/12/6  下午6:30
 * 版本 v 1.0.0
 */

public class MultiTypeAdapter extends SuperAdapter<Object, ItemViewHolder<Object>> {

    /**
     * 用来存放不同数据模型的 {@link ItemViewHolder}。不同数据模型的 {@link ItemViewHolder} 只会被存储一份，且是最初创建的那个。
     */
    private Map<Class, ItemViewHolder> mItemViewHolderMap = new HashMap<>();
    /**
     * 子条目数据变化的观察者。
     */
    private ItemAdapterDataObserver mAdapterDataObserver = new ItemAdapterDataObserver();
    /**
     * 加载更多失败时，点击重试的监听。
     */
    private LoadMoreRetryClickListener mLoadMoreRetryClickListener;
    /**
     * 用来展示悬浮窗的布局容器。
     */
    private FloatLayout mFloatLayout;
    /**
     * 用来记录当前第一个可见的条目的位置。
     */
    private int mCurPosition;
    /**
     * 用来记录悬浮条的高度。
     */
    private int mFloatLayoutHeight;
    /**
     * 用来记录上一次被绑定数的悬浮条的位置。
     */
    private int mLastBindPosition;
    /**
     * 为悬浮条绑定数据的帮助者。
     */
    private ViewHelper mFloatViewHelper;
    /**
     * 子条目的对象池。
     */
    private ChildAdapterPool mPool = new ChildAdapterPool();
    private FloatLayout.OnSizeChangedListener mFloatLayoutSizeChangedListener;

    /**
     * 构造方法。
     * <P>初始化适配器并设置布局管理器，您不许要再对 {@link RecyclerView} 设置布局管理器。
     * <p>例如：{@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)} 方法不应该在被调用，否者可能会出现您不希望看到的效果。
     *
     * @param recyclerView 您要绑定的 {@link RecyclerView} 对象。
     */
    public MultiTypeAdapter(@NonNull RecyclerView recyclerView) {
        this(recyclerView, 1);
    }

    /**
     * 构造方法。
     * <P>初始化适配器并设置布局管理器，您不许要再对 {@link RecyclerView} 设置布局管理器。
     * <p>例如：{@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)} 方法不应该在被调用，否者可能会出现您不希望看到的效果。
     *
     * @param recyclerView  您要绑定的 {@link RecyclerView} 对象。
     * @param totalSpanSize 总的占屏比，通俗来讲就是 {@link RecyclerView} 的宽度被均分成了多少份。该值的范围是1~100之间的数(包含)。
     */
    public MultiTypeAdapter(@NonNull RecyclerView recyclerView, @Size(min = 1, max = 100) int totalSpanSize) {
        this(recyclerView, totalSpanSize, LinearLayout.VERTICAL);
    }

    /**
     * 构造方法。
     * <P>初始化适配器并设置布局管理器，您不许要再对 {@link RecyclerView} 设置布局管理器。
     * <p>例如：{@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)} 方法不应该在被调用，否者可能会出现您不希望看到的效果。
     *
     * @param recyclerView  您要绑定的 {@link RecyclerView} 对象。
     * @param totalSpanSize 总的占屏比，通俗来讲就是 {@link RecyclerView} 的宽度被均分成了多少份。该值的范围是1~100之间的数(包含)。
     * @param orientation   列表的方向，该参数的值只能是{@link LinearLayout#HORIZONTAL} or {@link LinearLayout#VERTICAL}的其中一个。
     */
    public MultiTypeAdapter(@NonNull RecyclerView recyclerView, @Size(min = 1, max = 100) int totalSpanSize, @Orientation int orientation) {
        super(recyclerView, totalSpanSize, orientation);
        ViewGroup parent = (ViewGroup) recyclerView.getParent();
        int childCount = parent.getChildCount();
        if (childCount > 1) {
            for (int i = 0; i < childCount; i++) {
                View childAt = parent.getChildAt(i);
                if (childAt instanceof FloatLayout) {
                    attachFloatLayout((FloatLayout) childAt);
                }
            }
        }
    }

    /**
     * 关联用来显示悬浮条目的布局。
     * <p>
     * <h1><font color="#619BE5">注意：</font> </h1> {@link FloatLayout} 在xml文件中的的摆放必须满足以下特点：
     * <p>1.必须和 {@link RecyclerView} 是可重叠关系，也就是说用来承载 {@link RecyclerView} 和 {@link FloatLayout} 的布局容器必须是
     * 可以允许子View可以重叠的Layout，例如：{@link android.widget.RelativeLayout} 或 {@link android.widget.FrameLayout} 等。
     * <p>2.由于Android并不是任何时候都允许修改View的层级，所以在xml布局文件中 {@link FloatLayout} 的层级必须在 {@link RecyclerView} 之上。
     *
     * @param floatLayout 一个 {@link FloatLayout} 对象。
     */
    private void attachFloatLayout(@NonNull FloatLayout floatLayout) {
        Drawable background = getRecyclerView().getBackground();
        mFloatLayout = floatLayout;
        ColorDrawable bg = (ColorDrawable) (background == null ? ((ViewGroup) getRecyclerView().getParent()).getBackground() : background);
        int color = bg.getColor();
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color, Color.alpha(0)});
        mFloatLayout.setBackground(drawable);
        mFloatViewHelper = new ViewHelper(mFloatLayout);
        setFloatLayoutVisibility(false);
    }

    /**
     * 设置条目拖拽是否可用。
     *
     * @param moveEnable   拖动条目是否可用。true表示可以通过拖拽改变条目位置，false表示不可以。即使你将该参数设置为true
     *                     也并不代表你任何时候都可以进行拖拽改变条目位置。例如你想要拖拽的子Adapter中只有一条数据，即使整
     *                     个列表有很多数据也是不可以的。不允许将一个子Adapter中的数据拖拽到另一个子Adapter中去，更不允许
     *                     两个不同类型的子Adapter中的条目进行拖拽。
     * @param swipedEnable 滑动删除是否可用。true表示可以通过滑动删除条目，false表示不可以。
     */
    @Override
    public void setItemDragEnable(boolean moveEnable, boolean swipedEnable) {
        super.setItemDragEnable(moveEnable, swipedEnable);
    }

    @Override
    protected int getItemMovementFlags(RecyclerView recyclerView, ItemViewHolder viewHolder) {
        int layoutPosition = viewHolder.getLayoutPosition();
        SuperItemAdapter itemAdapter = getChildAdapterByPosition(layoutPosition);
        if (itemAdapter.getItemCount() == 1) {
            return ItemTouchHelper.Callback.makeMovementFlags(0, viewHolder.getSwipedEnable() ? ItemTouchHelper.START | ItemTouchHelper.END : 0);
        }
        int dragFlags = 0;
        int swipeFlags = 0;
        int itemSpanSize = itemAdapter.getItemSpanSize();
        int totalSpanSize = getTotalSpanSize();
        if (mDragEnable && viewHolder.getDragEnable()) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            if (itemSpanSize < totalSpanSize) {
                dragFlags |= ItemTouchHelper.START | ItemTouchHelper.END;
            }
        }
        if (mSwipedEnable && viewHolder.getSwipedEnable() && itemSpanSize >= totalSpanSize) {
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (mDragEnable) {
            SuperItemAdapter itemAdapter = getChildAdapterByPosition(fromPosition);
            return toPosition >= itemAdapter.firstItemPosition && toPosition <= itemAdapter.lastItemPosition && itemAdapter.onItemMove(fromPosition - itemAdapter.firstItemPosition, toPosition - itemAdapter.firstItemPosition);
        } else {
            return false;
        }
    }

    @Override
    public void onItemDismiss(int position) {
        if (mSwipedEnable) {
            SuperItemAdapter itemAdapter = getChildAdapterByPosition(position);
            itemAdapter.onItemDismiss(position - itemAdapter.firstItemPosition);
        }
    }

    /**
     * 获取子适配器 {@link SuperItemAdapter} 的数量。
     *
     * @return 返回已经被Add进来的所有的 {@link SuperItemAdapter} 的数量。
     * @see #addAdapter(SuperItemAdapter[])
     */
    public int getChildCount() {
        return mPool.size();
    }

    /**
     * 根据索引获取 {@link SuperItemAdapter}。
     *
     * @param index 要获取的索引。
     * @return 返回已经被Add进来的指定索引的 {@link SuperItemAdapter}。
     * @see #addAdapter(SuperItemAdapter[])
     */
    public SuperItemAdapter getChildAt(int index) {
        return mPool.acquire(index);
    }

    /**
     * 获取所有的 {@link SuperItemAdapter}。
     *
     * @return 返回已经被Add进来的所有的 {@link SuperItemAdapter}。
     * @see #addAdapter(SuperItemAdapter[])
     */
    public List<SuperItemAdapter> getAllChild() {
        return mPool.acquireAll();
    }

    private void setFloatLayoutVisibility(boolean visible) {
        mFloatLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isFloatLayoutShowing() {
        return mFloatLayout != null && mFloatLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * 添加条目适配器 {@link SuperItemAdapter}。
     *
     * @param adapters {@link SuperItemAdapter} 对象。
     * @see SuperItemAdapter#SuperItemAdapter(Class)
     * @see SuperItemAdapter#SuperItemAdapter(Class, Object)
     * @see SuperItemAdapter#SuperItemAdapter(List, Class)
     */
    @SuppressWarnings("unchecked")
    public MultiTypeAdapter addAdapter(@NonNull SuperItemAdapter... adapters) {
        for (SuperItemAdapter adapter : adapters) {
            adapter.registerObserver(mAdapterDataObserver);
            mPool.release(adapter);
            adapter.isAdded = true;
            adapter.firstItemPosition = getDataList().size();
            addDataList(adapter.getDataList());
            adapter.lastItemPosition = getDataList().size() - 1;
            adapter.setParent(this);
        }
        return this;
    }

    /**
     * 移除一个已经被添加的Adapter。子Adapter一旦被移除里面的数据也会跟着移除。移除成功后会刷新列表，如果不想刷新列表则调用
     * {@link #removeAdapter(SuperItemAdapter, boolean)} 方法。
     *
     * @param child 要移除的子Adapter。
     * @return 移除成功返回true，否则返回false。
     */
    public boolean removeAdapter(@NonNull SuperItemAdapter child) {
        return removeAdapter(child, true);
    }

    /**
     * 移除一个已经被添加的Adapter。子Adapter一旦被移除里面的数据也会跟着移除。
     *
     * @param child   要移除的子Adapter。
     * @param refresh 移除成功后是否刷新页面。
     * @return 移除成功返回true，否则返回false。
     */
    public boolean removeAdapter(@NonNull SuperItemAdapter child, boolean refresh) {
        child.isAdded = false;
        getDataList().removeAll(child.getDataList());
        getOldDataList().removeAll(child.getDataList());
        boolean remove = mPool.remove(child);
        if (refresh && remove) {
            notifyItemRangeChanged(child.firstItemPosition, child.getDataList().size());
        }
        return remove;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ItemViewHolder<Object> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY_ITEM) {
            return new CommonNoDataViewHolder(getEmptyView());
        }
        View itemView;
        if (mLoadMoreLayoutManager != null && (itemView = mLoadMoreLayoutManager.getLayoutView(viewType, parent)) != null) {
            CommonNoDataViewHolder loadMoreViewHolder = new CommonNoDataViewHolder(itemView);
            if (mLoadMoreLayoutManager.isRetryState()) {
                if (mLoadMoreRetryClickListener == null) {
                    mLoadMoreRetryClickListener = new LoadMoreRetryClickListener();
                }
                View clickView = loadMoreViewHolder.getView(loadMoreViewHolder.getItemClickViewId());
                if (clickView == null) {
                    clickView = loadMoreViewHolder.itemView;
                }
                clickView.setOnClickListener(mLoadMoreRetryClickListener);
            }
            return loadMoreViewHolder;
        }
        SuperItemAdapter itemAdapter = mPool.acquireByType(viewType);
        if (itemAdapter != null) {
            ItemViewHolder holder = itemAdapter.onCreateViewHolder(parent, viewType);
            if (mFloatLayout != null && isFloatAdapter(itemAdapter)) {
                if (mFloatLayoutSizeChangedListener == null) {
                    mFloatLayoutSizeChangedListener = new FloatLayout.OnSizeChangedListener() {
                        @Override
                        public void onSizeChanged(int width, int height) {
                            mFloatLayoutHeight = height;
                        }
                    };
                    mFloatLayout.setOnSizeChangedListener(mFloatLayoutSizeChangedListener);
                }
                mFloatLayout.setFloatContent(viewType);
            }
            Class itemModelClass = itemAdapter.getItemModelClass();
            if (!mItemViewHolderMap.containsKey(itemModelClass)) {
                mItemViewHolderMap.put(itemModelClass, holder);
            }
            return holder;
        }
        throw new RuntimeException("the viewType: " + viewType + " not found !");
    }

    private boolean isFloatAdapter(SuperItemAdapter adapter) {
        return adapter instanceof FloatItemAdapter;
    }

    @Override
    public void onViewRecycled(ItemViewHolder<Object> holder) {
        if (holder instanceof CommonNoDataViewHolder) {
            return;
        }
        int position = holder.getLayoutPosition();
        if (position < 0) {
            return;
        }
        SuperItemAdapter itemAdapter = mPool.acquireFromLayoutPosition(position);
        itemAdapter.onViewRecycled(holder, position - itemAdapter.firstItemPosition);
    }

    @Override
    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy, LinearLayoutManager lm) {
        //如果没有设置悬浮控件就不获取子适配器，这这里的逻辑就不会执行。
        SuperItemAdapter itemAdapter = mFloatLayout == null ? null : getAdjacentChildAdapterByPosition(mCurPosition, true, false);
        if (itemAdapter != null && isFloatAdapter(itemAdapter) && dy != 0) {
            View view = lm.findViewByPosition(itemAdapter.firstItemPosition);
            if (view != null) {
                if (isFirstFloatAbleChildAdapter(mCurPosition + 1) && isFloatLayoutShowing()) {
                    setFloatLayoutVisibility(false);
                } else {
                    if (view.getTop() <= (isFloatLayoutShowing() ? mFloatLayoutHeight : 0)) {
                        setFloatLayoutVisibility(true);
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        int marginTop = 0;
                        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                            marginTop = ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
                        }
                        if (view.getTop() - marginTop <= 0) {
                            mFloatLayout.setY(0);
                        } else {
                            mFloatLayout.setY(-(mFloatLayoutHeight - view.getTop()));
                        }
                    } else {
                        mFloatLayout.setY(0);
                    }
                }
            } else if (isFloatLayoutShowing()) {
                mFloatLayout.setY(0);
            }
        }

        //当速度快到一定程度的时候这个position可能会有间隔。而间隔的条目在极端情况下可能是最后一个需要绑定悬浮数据的条目。
        int first = lm.findFirstVisibleItemPosition();
        if (mCurPosition != first) {
            if (mFloatLayout != null) {
                int max = Math.max(mCurPosition, first);
                int min = Math.min(mCurPosition, first);
                //如果本次的位置和上一次的位置不是相邻的，那么就循环将跳过的位置进行更新。否则直接更新。
                if (max - min > 1) {
                    if (dy < 0) {
                        for (int i = max - 1; i >= min; i--) {
                            updateFloatLayout(dy, i);
                        }
                    } else {
                        for (int i = min + 1; i <= max; i++) {
                            updateFloatLayout(dy, i);
                        }
                    }
                } else {
                    updateFloatLayout(dy, first);
                }
            }
            mCurPosition = first;
        }
    }

    @SuppressWarnings("unchecked")
    private void updateFloatLayout(int dy, int position) {
        SuperItemAdapter itemAdapter;
        if (dy < 0) {
            itemAdapter = getAdjacentChildAdapterByPosition(position + 1, false, true);
        } else {
            itemAdapter = getChildAdapterByPosition(position);
        }
        if (itemAdapter != null && isFloatAdapter(itemAdapter) && mLastBindPosition != itemAdapter.firstItemPosition) {
            if (dy < 0) {
                mFloatLayout.setY(-mFloatLayoutHeight);
            }
            mLastBindPosition = itemAdapter.firstItemPosition;
            mFloatLayout.upDateContentView(itemAdapter.getItemViewType()); //更新悬浮布局。
            mFloatLayout.setLayoutPosition(mLastBindPosition);
            ((FloatItemAdapter) itemAdapter).onBindFloatViewData(mFloatViewHelper, getObject(mLastBindPosition));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(ItemViewHolder<Object> holder, int position, List<Object> payloads) {
        //如果当前条目是LoadMoreItem或者是EmptyItem则不绑定数据。
        if (isEmptyItem(position) || isLoadMoreItem(position)) return;
        SuperItemAdapter itemAdapter = mPool.acquireFromLayoutPosition(position);
        itemAdapter.onBindViewHolder(holder, position - itemAdapter.firstItemPosition, payloads);
        if (mFloatLayout != null && position == 0 && isFloatAdapter(itemAdapter)) {
            mFloatLayout.setLayoutPosition(position);
            setFloatLayoutVisibility(true);
            ((FloatItemAdapter) itemAdapter).onBindFloatViewData(mFloatViewHelper, getObject(itemAdapter.firstItemPosition));
        }
    }

    @Override
    public int getItemType(int position) {
        return mPool.acquireFromLayoutPosition(position).getItemViewType();
    }

    @Override
    protected int getItemSpanSize(int position) {
        SuperItemAdapter adapter = getChildAdapterByPosition(position);

        if (adapter == null) return getTotalSpanSize();

        int itemSpanSize = adapter.getItemSpanSize();
        return itemSpanSize == SuperItemAdapter.SPAN_SIZE_FULL_SCREEN || itemSpanSize > getTotalSpanSize() ? getTotalSpanSize() : itemSpanSize;
    }

    /**
     * 根据索引获取对应的子适配器。
     *
     * @param position 当前的索引位置。
     * @return 返回对应的适配器。
     */
    SuperItemAdapter getChildAdapterByPosition(int position) {
        return mPool.acquireFromLayoutPosition(position);
    }

    /**
     * 通过{@link RecyclerView}的Adapter的position获取{@link ItemAdapter}中对应的position。
     *
     * @param position 当前 {@link RecyclerView} 的position。
     */
    int getItemAdapterPosition(int position) {
        return position - mPool.acquireFromLayoutPosition(position).firstItemPosition;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean areItemsTheSame(Object oldItemData, Object newItemData) {
        if (oldItemData.getClass() != newItemData.getClass()) {
            return false;
        }
        ItemViewHolder viewHolder = getViewHolder(oldItemData.getClass());
        return viewHolder == null || viewHolder.areItemsTheSame(oldItemData, newItemData);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean areContentsTheSame(Object oldItemData, Object newItemData) {
        if (oldItemData.getClass() != newItemData.getClass()) {
            return false;
        }
        ItemViewHolder viewHolder = getViewHolder(oldItemData.getClass());
        return viewHolder == null || viewHolder.areContentsTheSame(oldItemData, newItemData);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void getChangePayload(Object oldItemData, Object newItemData, Bundle bundle) {
        if (oldItemData.getClass() != newItemData.getClass()) {
            return;
        }
        ItemViewHolder viewHolder = getViewHolder(oldItemData.getClass());
        if (viewHolder != null) {
            viewHolder.getChangePayload(oldItemData, newItemData, bundle);
        }
    }

    /**
     * 根据Holder的数据模型类型获取 {@link ItemViewHolder} 对象。
     *
     * @param holderModelClazz {@link ItemViewHolder} 中泛型指定的数据模型的字节码对象。
     * @return 返回 {@link ItemViewHolder} 对象。
     */
    private ItemViewHolder getViewHolder(Class<?> holderModelClazz) {
        //因为我省略了Adapter是不需要自己创建Adapter子类的，所以只能讲所有方法都封装到ViewHolder中。这里存ViewHolder也是迫不得已
        //暂时没有想到更好的方法去比较两个模型的相同与不同。
        return mItemViewHolderMap.get(holderModelClazz);
    }

    /**
     * 根据position获取相邻的子条目的Adapter。具体是获取前一个还是后一个由next参数决定。是否只获取可悬浮的由floatAble参数决定。
     *
     * @param position  当前的position索引。
     * @param next      是否是获取后一个，true表示获取后一个，false表示获取前一个。
     * @param floatAble 是否获取可悬浮的。
     */
    @CheckResult
    private SuperItemAdapter getAdjacentChildAdapterByPosition(int position, boolean next, boolean floatAble) {
        SuperItemAdapter adapter;
        SuperItemAdapter lastFloatAbleAdapter = null;
        boolean isContinue = false;
        for (int i = 0; i < mPool.size(); i++) {
            adapter = mPool.acquire(i);
            if (isContinue && isFloatAdapter(adapter)) {
                return adapter;
            }
            if (adapter.firstItemPosition <= position && adapter.lastItemPosition >= position) {
                if (floatAble) {
                    if (next) {
                        isContinue = true;
                        continue;
                    } else {
                        return lastFloatAbleAdapter;
                    }
                }
                if (next) {
                    if (i == mPool.size() - 1) {
                        return null;
                    } else {
                        return mPool.acquire(i + 1);
                    }
                } else {
                    if (i == 0) {
                        return null;
                    } else {
                        return mPool.acquire(i - 1);
                    }
                }
            } else {
                if (isFloatAdapter(adapter)) {
                    lastFloatAbleAdapter = adapter;
                }
            }
        }
        return null;
    }

    /**
     * 根据布局位置判断是否是第一个可悬浮的子Adapter。
     *
     * @param position 要判断的位置。
     */
    private boolean isFirstFloatAbleChildAdapter(int position) {
        SuperItemAdapter itemAdapter = mPool.acquireFirstFloatAbleChild();
        return itemAdapter != null && itemAdapter.firstItemPosition <= position && itemAdapter.lastItemPosition >= position;
    }

    private class ItemAdapterDataObserver implements SuperItemAdapter.AdapterDataObserver {

        @Override
        public void add(int position, Object o, SuperItemAdapter adapter) {
            getDataList().add(position + adapter.firstItemPosition, o);
            getOldDataList().add(position + adapter.firstItemPosition, o);
            updateFirstAndLastPosition(adapter, 1, true);
        }

        @Override
        public void addAll(int firstPosition, Collection dataList, SuperItemAdapter adapter) {
            boolean addAll = getDataList().addAll(firstPosition + adapter.firstItemPosition, dataList);
            boolean oldAddAll = getOldDataList().addAll(firstPosition + adapter.firstItemPosition, dataList);
            if (addAll && oldAddAll) {
                updateFirstAndLastPosition(adapter, dataList.size(), true);
            }
        }

        @Override
        public void remove(Object o, SuperItemAdapter adapter) {
            boolean remove = getDataList().remove(o);
            boolean oldRemove = getOldDataList().remove(o);
            if (remove && oldRemove) {
                updateFirstAndLastPosition(adapter, 1, false);
            }
        }

        @Override
        public void removeAll(Collection dataList, SuperItemAdapter adapter) {
            boolean removeAll = getDataList().removeAll(dataList);
            boolean oldRemoveAll = getOldDataList().removeAll(dataList);
            if (removeAll && oldRemoveAll) {
                updateFirstAndLastPosition(adapter, dataList.size(), false);
            }
        }

        @Override
        public void move(int fromPosition, int toPosition, SuperItemAdapter adapter) {
            fromPosition += adapter.firstItemPosition;
            toPosition += adapter.firstItemPosition;
            if (getTotalSpanSize() > 1) {
                List<Object> dataList = getDataList();
                List<Object> oldDataList = getOldDataList();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(dataList, i, i + 1);
                        Collections.swap(oldDataList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(dataList, i, i - 1);
                        Collections.swap(oldDataList, i, i - 1);
                    }
                }
            } else {
                Collections.swap(getDataList(), fromPosition, toPosition);
                Collections.swap(getOldDataList(), fromPosition, toPosition);
            }
        }

        private void updateFirstAndLastPosition(SuperItemAdapter current, int updateSize, boolean isAdd) {
            if (isAdd) {
                int index = mPool.indexOf(current) + 1;
                current.lastItemPosition += updateSize;
                for (int i = index; i < mPool.size(); i++) {
                    current = mPool.acquire(i);
                    current.firstItemPosition += updateSize;
                    current.lastItemPosition += updateSize;
                }
            } else {
                int index = mPool.indexOf(current);
                current.lastItemPosition -= updateSize;
                if (current.isEmptyList()) {
                    current.unregisterAll();
                    removeAdapter(current, false);
                    current.isAdded = false;
                } else {
                    index += 1;
                }
                for (int i = index; i < mPool.size(); i++) {
                    current = mPool.acquire(i);
                    current.firstItemPosition -= updateSize;
                    current.lastItemPosition -= updateSize;
                }
            }
        }
    }

    /**
     * 子Adapter的对象池。
     */
    private class ChildAdapterPool {

        /**
         * 用来存放所有的子条目对象。
         */
        private List<SuperItemAdapter> adapters = new ArrayList<>();


        /**
         * 通过位置获取对象。
         *
         * @param position 要获取的对象的位置。
         */
        SuperItemAdapter acquire(int position) {
            return adapters.get(position);
        }

        /**
         * 通过位置获取对象。
         *
         * @param type 要获取的对象的位置。
         */
        SuperItemAdapter acquireByType(int type) {
            for (SuperItemAdapter adapter : adapters) {
                if (adapter.getItemViewType() == type) {
                    return adapter;
                }
            }
            return null;
        }

        /**
         * 获取第一个可悬浮的子Adapter。
         */
        SuperItemAdapter acquireFirstFloatAbleChild() {
            for (SuperItemAdapter adapter : adapters) {
                if (isFloatAdapter(adapter)) {
                    return adapter;
                }
            }
            return null;
        }

        /**
         * 根据布局位置获取对象。
         *
         * @param layoutPosition 要获取的对象的布局位置。
         */
        SuperItemAdapter acquireFromLayoutPosition(int layoutPosition) {
            for (SuperItemAdapter adapter : adapters) {
                if (adapter.firstItemPosition <= layoutPosition && adapter.lastItemPosition >= layoutPosition) {
                    return adapter;
                }
            }
            throw new RuntimeException("SuperItemAdapter not found from layout position: " + layoutPosition);
        }

        /**
         * 获取全部对象。
         */
        List<SuperItemAdapter> acquireAll() {
            return adapters;
        }

        void release(SuperItemAdapter instance) {
            adapters.add(instance);
        }

        /**
         * 获取对象的个数。
         */
        public int size() {
            return adapters.size();
        }

        /**
         * 获取一个对象的位置。
         *
         * @param instance 要获取位置的对象。
         */
        int indexOf(SuperItemAdapter instance) {
            return adapters.indexOf(instance);
        }

        /**
         * 移除一个对象。
         *
         * @param instance 要移除的对象。
         */
        boolean remove(SuperItemAdapter instance) {
            return adapters.remove(instance);
        }
    }
}
