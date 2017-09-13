package com.kelin.recycleradapter;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kelin.recycleradapter.callback.ItemDragResultListener;
import com.kelin.recycleradapter.holder.ItemViewHolder;
import com.kelin.recycleradapter.interfaces.Orientation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述 {@link android.support.v7.widget.RecyclerView} 的适配器的基类。
 * 创建人 kelin
 * 创建时间 2017/3/28  下午12:42
 * 版本 v 1.0.0
 */

public abstract class SuperAdapter<D, VH extends ItemViewHolder<D>> extends RecyclerView.Adapter<VH> {
    private static final String TAG = "SuperAdapter";
    /**
     * 表示当前的条目是占满屏幕的。
     */
    public static final int SPAN_SIZE_FULL_SCREEN = 0x0000_0010;
    /**
     * 列表为空时的条目类型。
     */
    static final int TYPE_EMPTY_ITEM = 0x0000_00f0;
    /**
     * 当前页面的数据集。
     */
    private List<D> mDataList;
    /**
     * 用来存页面数据集的副本。
     */
    private List<D> mTempList;
    /**
     * 当需要刷新列表时，用来比较两此数据不同的回调。
     */
    private DiffUtil.Callback mDiffUtilCallback;
    /**
     * 与当前适配器绑定的 {@link RecyclerView} 对象。
     */
    private RecyclerView mRecyclerView;
    /**
     * 当前 {@link RecyclerView} 的宽度被均分成的份数。
     */
    private int mTotalSpanSize;
    /**
     * 当前的布局管理器对象。
     */
    private LinearLayoutManager mLm;
    /**
     * 加载更多的回调。
     */
    private OnLoadMoreListener mLoadMoreListener;
    /**
     * 加载更多的布局信息对象。
     */
    LoadMoreLayoutManager mLoadMoreLayoutManager;
    /**
     * 列表为空时的布局。
     */
    private View mEmptyLayout;
    /**
     * 用来记录当前列表的方向。
     */
    private int mOrientation;
    /**
     * 记录侧滑删除是否可用。
     */
    boolean mSwipedEnable;
    /**
     * 记录条目拖拽是否可用。
     */
    boolean mDragEnable;
    /**
     * 条目拖拽完成后的监听。
     */
    private ItemDragResultListener<D> mItemDragResultListener;
    private ItemTouchHelper mItemTouchHelper;

    /**
     * 构造方法。
     * <P>初始化适配器并设置布局管理器，您不许要再对 {@link RecyclerView} 设置布局管理器。
     * <p>例如：{@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)} 方法不应该在被调用，否者可能会出现您不希望看到的效果。
     *
     * @param recyclerView 您要绑定的 {@link RecyclerView} 对象。
     */
    public SuperAdapter(@NonNull RecyclerView recyclerView) {
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
    public SuperAdapter(@NonNull RecyclerView recyclerView, @Size(min = 1, max = 100) int totalSpanSize) {
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
    public SuperAdapter(@NonNull RecyclerView recyclerView, @Size(min = 1, max = 100) int totalSpanSize, @Orientation int orientation) {
        if (totalSpanSize < 1 || totalSpanSize > 100) {
            throw new RuntimeException("the totalSpanSize argument must be an integer greater than zero and less than 1000");
        }
        mTotalSpanSize = totalSpanSize;
        mRecyclerView = recyclerView;
        mOrientation = orientation;
        initLayoutManager(recyclerView, orientation, mTotalSpanSize);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                SuperAdapter.this.onRecyclerViewScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                SuperAdapter.this.onRecyclerViewScrolled(recyclerView, dx, dy, mLm);
                //处理loadMore
                if (isLoadMoreUsable() && mLoadMoreLayoutManager.isLoadState()) {
                    if (mLoadMoreLayoutManager.isInTheLoadMore() || mLoadMoreLayoutManager.isNoMoreState())
                        return;
                    int lastVisibleItemPosition = mLm.findLastVisibleItemPosition();
                    int targetPosition = getDataList().size() - mLoadMoreLayoutManager.getLoadMoreOffset();
                    if (targetPosition == 0 || lastVisibleItemPosition == targetPosition) {
                        startLoadMore();
                    }
                }
            }
        });

        mDiffUtilCallback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mTempList.size();
            }

            @Override
            public int getNewListSize() {
                return mDataList.size();
            }

            // 判断是否是同一个 item
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                D oldObject = getOldObject(oldItemPosition);
                D newObject = getObject(newItemPosition);
                return oldObject == newObject || SuperAdapter.this.areItemsTheSame(oldObject, newObject);
            }

            // 如果是同一个 item 判断内容是否相同
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                D oldObject = getOldObject(oldItemPosition);
                D newObject = getObject(newItemPosition);
                return oldObject == newObject || SuperAdapter.this.areContentsTheSame(oldObject, newObject);
            }

            @Nullable
            @Override
            public Bundle getChangePayload(int oldItemPosition, int newItemPosition) {
                D oldObject = getOldObject(oldItemPosition);
                D newObject = getObject(newItemPosition);
                if (oldObject == newObject) return null;
                Bundle bundle = new Bundle();
                SuperAdapter.this.getChangePayload(oldObject, newObject, bundle);
                return bundle.size() == 0 ? null : bundle;
            }
        };
    }

    /**
     * 设置条目拖拽是否可用。
     *
     * @param moveEnable   拖动条目是否可用。true表示可以通过拖拽改变条目位置，false表示不可以。
     * @param swipedEnable 滑动删除是否可用。true表示可以通过滑动删除条目，false表示不可以。如果你设置了true则一般情况下
     *                     是可以滑动删除的，除非你滑动的 {@link ItemViewHolder} 复写了 {@link ItemViewHolder#getSwipedEnable()}
     *                     方法并返回了false。
     */
    public void setItemDragEnable(boolean moveEnable, boolean swipedEnable) {
        setItemDragEnable(moveEnable, swipedEnable, null);
    }

    /**
     * 设置条目拖拽是否可用。
     *
     * @param moveEnable   拖动条目是否可用。true表示可以通过拖拽改变条目位置，false表示不可以。
     * @param swipedEnable 滑动删除是否可用。true表示可以通过滑动删除条目，false表示不可以。如果你设置了true则一般情况下
     *                     是可以滑动删除的，除非你滑动的 {@link ItemViewHolder} 复写了 {@link ItemViewHolder#getSwipedEnable()}
     *                     方法并返回了false。
     * @param listener     条目拖拽结果的监听。
     */
    public void setItemDragEnable(boolean moveEnable, boolean swipedEnable, ItemDragResultListener<D> listener) {
        if (mItemTouchHelper == null && (moveEnable || swipedEnable)) {
            ItemTouchCallback itemTouchCallback = new ItemTouchCallback();
            mItemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
        mDragEnable = moveEnable;
        mSwipedEnable = swipedEnable;
        mItemDragResultListener = listener;
    }

    protected int getItemMovementFlags(RecyclerView recyclerView, ItemViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;

        int swipeFlags = 0;
        if (viewHolder.getSwipedEnable()) {
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags);
    }

    protected boolean onItemMove(int fromPosition, int toPosition) {
        if (mDragEnable) {
            if (getTotalSpanSize() > 1) {
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mDataList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mDataList, i, i - 1);
                    }
                }
            } else {
                Collections.swap(mDataList, fromPosition, toPosition);
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        } else {
            return false;
        }
    }

    protected void onItemDismiss(int position) {
        if (mSwipedEnable) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Orientation
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * 设置加载更多是否可用。如果有时候你的页面虽然是支持分页加载的，但是特殊情况下你并不希望展示加载中的条目，这时就可以通过
     * 调用此方法禁止加载中条目的显示。
     * <p>例如：当你的总条目不足以显示一页的时候（假如你每页数据是20条，但是你总数据一共才5条），这时候你就可以通过调用这个方法
     * 禁止加载中条目的显示。
     *
     * @param usable true表示可用，false表示不可用。
     */
    public void setLoadMoreUsable(boolean usable) {
        if (mLoadMoreLayoutManager != null) {
            mLoadMoreLayoutManager.setLoadMoreUsable(usable);
        }
    }

    /**
     * 加载更多是否可用。
     */
    private boolean isLoadMoreUsable() {
        return mLoadMoreLayoutManager != null && mLoadMoreLayoutManager.isUsable();
    }

    /**
     * 初始化布局管理器。并设置给 {@link RecyclerView}。
     */
    private void initLayoutManager(@NonNull RecyclerView recyclerView, @Orientation int orientation, int totalSpanSize) {
        if (totalSpanSize > 1) {
            GridLayoutManager lm = new GridLayoutManager(recyclerView.getContext(), totalSpanSize, orientation, false) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    try {
                        super.onLayoutChildren(recycler, state);
                    } catch (Exception ignored) {
                    }
                }
            };
            lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemSpan(position);
                }
            });
            mLm = lm;
        } else {
            mLm = new LinearLayoutManager(recyclerView.getContext(), orientation, false) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    try {
                        super.onLayoutChildren(recycler, state);
                    } catch (Exception ignored) {
                    }
                }
            };
        }
        recyclerView.setLayoutManager(mLm);
    }

    /**
     * 获取总的占屏比，通俗来讲就是获取 {@link RecyclerView} 的宽度被均分成了多少份。
     *
     * @return 总的份数。
     */
    int getTotalSpanSize() {
        return mTotalSpanSize;
    }

    /**
     * 根据位置获取条目的占屏值。
     *
     * @param position 当前的位置。
     * @return 返回当前条目的占屏值。
     */
    protected int getItemSpan(int position) {
        if (isLoadMoreItem(position)) {
            return getTotalSpanSize();
        } else {
            return getItemSpanSize(position);
        }
    }

    protected abstract int getItemSpanSize(int position);

    /**
     * 设置加载更多时显示的布局。
     *
     * @param loadMoreLayoutId 加载更多时显示的布局的资源ID。
     * @param retryLayoutId    加载更多失败时显示的布局。
     * @param listener         加载更多被触发的监听。
     */
    public void setLoadMoreView(@LayoutRes int loadMoreLayoutId, @LayoutRes int retryLayoutId, @NonNull OnLoadMoreListener listener) {
        setLoadMoreView(loadMoreLayoutId, retryLayoutId, 0, listener);
    }

    /**
     * 设置加载更多时显示的布局。
     *
     * @param loadMoreLayoutId   加载更多时显示的布局的资源ID。
     * @param retryLayoutId      加载更多失败时显示的布局。
     * @param noMoreDataLayoutId 没有更多数据时显示的布局。
     * @param listener           加载更多被触发的监听。
     */
    public void setLoadMoreView(@LayoutRes int loadMoreLayoutId, @LayoutRes int retryLayoutId, @LayoutRes int noMoreDataLayoutId, @NonNull OnLoadMoreListener listener) {
        setLoadMoreView(loadMoreLayoutId, retryLayoutId, noMoreDataLayoutId, 0, listener);
    }

    /**
     * 设置加载更多时显示的布局。
     *
     * @param loadMoreLayoutId   加载更多时显示的布局的资源ID。
     * @param retryLayoutId      加载更多失败时显示的布局。
     * @param noMoreDataLayoutId 没有更多数据时显示的布局。
     * @param offset             加载更多触发位置的偏移值。偏移范围只能是1-10之间的数值。正常情况下是loadMoreLayout显示的时候就开始触发，
     *                           但如果设置了该值，例如：2，那么就是在loadMoreLayout之前的两个位置的时候开始触发。
     * @param listener           加载更多被触发的监听。
     */
    public void setLoadMoreView(@LayoutRes int loadMoreLayoutId, @LayoutRes int retryLayoutId, @LayoutRes int noMoreDataLayoutId, @Size(min = 1, max = 10) int offset, @NonNull OnLoadMoreListener listener) {
        setLoadMoreView(new LoadMoreLayoutManager(loadMoreLayoutId, retryLayoutId, noMoreDataLayoutId, offset), listener);
    }

    /**
     * 设置加载更多时显示的布局。
     *
     * @param layoutInfo LoadMore布局信息对象。
     * @param listener   加载更多被触发的监听。
     */
    public void setLoadMoreView(@NonNull LoadMoreLayoutManager layoutInfo, @NonNull OnLoadMoreListener listener) {
        mLoadMoreLayoutManager = layoutInfo;
        mLoadMoreListener = listener;
    }

    /**
     * 设置列表为空时显示的布局。如果你使用了该方法，则必须为你的ViewHolder提供 {@link ItemViewHolder#ItemViewHolder(View)} 构造方法，
     * 否则会出错。
     *
     * @param emptyLayout 列表为空时的布局ID。
     * @return 返回这个布局ID被Inflater后的View。
     * @see ItemViewHolder#ItemViewHolder(View)
     */
    public View setEmptyView(@LayoutRes int emptyLayout) {
        View emptyView = LayoutInflater.from(mRecyclerView.getContext()).inflate(emptyLayout, mRecyclerView, false);
        setEmptyView(emptyView);
        return emptyView;
    }

    /**
     * 设置列表为空时显示的布局。如果你使用了该方法，则必须为你的ViewHolder提供 {@link ItemViewHolder#ItemViewHolder(View)} 构造方法，
     * 否则会出错。
     *
     * @param emptyLayout 列表为空时的布局。
     * @see ItemViewHolder#ItemViewHolder(View)
     */
    public void setEmptyView(View emptyLayout) {
        mEmptyLayout = emptyLayout;
    }

    /**
     * 获取当前的列表为空时的布局。
     */
    public View getEmptyView() {
        return mEmptyLayout;
    }

    /**
     * 当列表的滚动状态被改变的时候执行的回调方法。
     *
     * @param recyclerView 当前被滚动的 {@link RecyclerView} 对象。
     * @param newState     当前的滚动状态。
     */
    protected void onRecyclerViewScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    /**
     * 当列表被滚的时候执行的回调方法。
     *
     * @param recyclerView 当前正在滚动中的 {@link RecyclerView} 对象。
     * @param dx           x轴的偏移值。
     * @param dy           y轴的偏移值。
     * @param lm           当前 {@link RecyclerView} 的布局管理器LayoutManager。
     */
    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy, LinearLayoutManager lm) {
    }

    /**
     * 开始加载更多。
     */
    private void startLoadMore() {
        if (mLoadMoreListener != null) {
            Log.i("MultiTypeAdapter", "开始加载更多");
            mLoadMoreLayoutManager.setInTheLoadMore(true);
            mLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 开始加载更多。
     */
    private void reloadMore() {
        if (mLoadMoreListener != null) {
            Log.i("MultiTypeAdapter", "开始加载更多");
            mLoadMoreLayoutManager.setInTheLoadMore(true);
            mLoadMoreListener.onReloadMore();
        }
    }

    /**
     * 当加载更多完成后要调用此方法，否则不会触发下一次LoadMore事件。
     */
    public void setLoadMoreFinished() {
        mLoadMoreLayoutManager.setInTheLoadMore(false);
        Log.i("MultiTypeAdapter", "加载完成");
    }

    /**
     * 当加载更多失败后要调用此方法，否则没有办法点击重试加载更多。
     */
    public void setLoadMoreFailed() {
        checkLoadMoreAvailable();
        int position = getItemCount() - 1;
        mLoadMoreLayoutManager.setRetryState();
        notifyItemChanged(position);
        Log.i("MultiTypeAdapter", "加载完成");
    }

    /**
     * 如果你的页面已经没有更多数据可以加载了的话，应当调用此方法。调用了此方法后就不会再触发LoadMore事件，否则还会触发。
     */
    public void setNoMoreData() {
        checkLoadMoreAvailable();
        int position = getItemCount() - 1;
        mLoadMoreLayoutManager.setNoMoreState();
        notifyItemChanged(position);
    }

    private void checkLoadMoreAvailable() {
        if (mLoadMoreLayoutManager == null) {
            throw new RuntimeException("You are not set to load more View, you can call the setLoadMoreView() method.");
        }
    }

    @Override
    public int getItemCount() {
        int size = getDataList().size();
        return size == 0 ? mEmptyLayout == null ? 0 : 1 : size + (!isLoadMoreUsable() || mLoadMoreLayoutManager.noCurStateLayoutId() ? 0 : 1);
    }

    @Override
    public final int getItemViewType(int position) {
        if (isEmptyItem(position)) return TYPE_EMPTY_ITEM;
        if (isLoadMoreItem(position)) return mLoadMoreLayoutManager.getCurStateLayoutId();
        return getItemType(position);
    }

    protected boolean isEmptyItem(int position) {
        return position == 0 && mEmptyLayout != null && getItemCount() == 1;
    }

    boolean isLoadMoreItem(int position) {
        return isLoadMoreUsable() && !mLoadMoreLayoutManager.noCurStateLayoutId() && position == getItemCount() - 1;
    }

    protected abstract int getItemType(int position);

    @Override
    public final void onBindViewHolder(VH holder, int position) {
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (isEmptyItem(position)) return;
        if (isLoadMoreItem(position)) return; //如果当前条目是LoadMoreItem则不绑定数据。
        holder.onBindPartData(position, getObject(position), payloads);
    }

    @Override
    public void onViewRecycled(VH holder) {
        holder.onViewRecycled();
    }

    /**
     * 判断两个位置的Item是否相同。
     *
     * @param oldItemData 旧的Item数据。
     * @param newItemData 新的Item数据。
     * @return 相同返回true，不同返回false。
     */
    protected abstract boolean areItemsTheSame(D oldItemData, D newItemData);

    /**
     * 判断两个位置的Item的内容是否相同。
     *
     * @param oldItemData 旧的Item数据。
     * @param newItemData 新的Item数据。
     * @return 相同返回true，不同返回false。
     */
    protected abstract boolean areContentsTheSame(D oldItemData, D newItemData);

    /**
     * 获取两个位置的Item的内容不同之处。
     *
     * @param oldItemData 旧的Item数据。
     * @param newItemData 新的Item数据。
     * @param bundle      将不同的内容存放到该参数中。
     */
    protected abstract void getChangePayload(D oldItemData, D newItemData, Bundle bundle);

    /**
     * 刷新RecyclerView。如果你需要刷新列表最好调用该方法，而不应该调用 {@link #notifyDataSetChanged()} 方法，该方法是
     * 通过Google提供的 {@link DiffUtil} 工具类进行数据比较然后映射到对应的 notifyItem***() 方法的。
     */
    public void notifyRefresh() {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mDiffUtilCallback);
        diffResult.dispatchUpdatesTo(this);
        // 通知刷新了之后，要更新副本数据到最新
        mTempList.clear();
        mTempList.addAll(mDataList);
    }

    /**
     * 设置数据。
     *
     * @param list 数据集合。
     */
    public void setDataList(@NonNull List<D> list) {
        setDataList(list, false);
    }

    /**
     * 设置数据。
     *
     * @param list    数据集合。
     * @param refresh 是否刷新列表。
     */
    public void setDataList(@NonNull List<D> list, boolean refresh) {
        mDataList = list;
        if (mTempList == null) {
            mTempList = new ArrayList<>();
        } else {
            mTempList.clear();
        }
        if (refresh) {
            notifyRefresh();
        }
    }

    /**
     * 设置数据。
     *
     * @param list 数据集合。
     */
    void addDataList(List<D> list) {
        getDataList().addAll(list);
        if (mLoadMoreLayoutManager == null || !mLoadMoreLayoutManager.isInTheLoadMore()) {
            mTempList.addAll(list);
        }
    }

    /**
     * 设置数据。
     *
     * @param d 数据集合。
     */
    void addData(D d) {
        getDataList().add(d);
        if (mLoadMoreLayoutManager == null || !mLoadMoreLayoutManager.isInTheLoadMore()) {
            mTempList.add(d);
        }
    }

    /**
     * 获取当前的数据集合。
     */
    List<D> getDataList() {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
            mTempList = new ArrayList<>();
        }
        return mDataList;
    }

    /**
     * 获取临时数据集合。
     */
    List<D> getOldDataList() {
        return mTempList;
    }

    /**
     * 判断当前列表是否为空列表。
     *
     * @return <code color="blue">true</code> 表示为空列表，<code color="blue">false</code> 表示为非空列表。
     */
    public boolean isEmptyList() {
        return mDataList == null || mDataList.isEmpty();
    }

    /**
     * 获取指定位置的对象。
     *
     * @param position 要获取对象对应的条目索引。
     * @return 返回 {@link D} 对象。
     */
    public D getObject(int position) {
        List<D> dataList = getDataList();
        if (dataList.size() > position && position >= 0) {
            return dataList.get(position);
        }
        return null;
    }

    /**
     * 获取指定位置的对象。
     *
     * @param position 要获取对象对应的条目索引。
     * @return 返回 {@link D} 对象。
     */
    private D getOldObject(int position) {
        if (mTempList.size() > position && position >= 0) {
            return mTempList.get(position);
        }
        return null;
    }

    final void startDrag(ItemViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * 加载更多的回调对象。
     */
    public abstract static class OnLoadMoreListener {

        /**
         * 加载更多时的回调。
         */
        public abstract void onLoadMore();

        /**
         * 重新加载更多时的回调。当上一次加载更多失败点击重试后会执行此方法，而不会执行 {@link #onLoadMore()} 方法。
         * 这里是将该方法重新指向了 {@link #onLoadMore()} 方法，因为有可能你不需要关心这个方法。如果你需要关心这个方法，
         * 则可以通过重写该方法将默认实现覆盖。
         */
        public void onReloadMore() {
            onLoadMore();
        }
    }

    class LoadMoreRetryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mLoadMoreLayoutManager.setLoadState();
            notifyItemChanged(getItemCount() - 1);
            reloadMore();
        }
    }

    private class ItemTouchCallback extends ItemTouchHelper.Callback {
        private D mD;
        private int mLastActionPosition;
        private int mLastActionState;

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return getItemMovementFlags(recyclerView, (ItemViewHolder) viewHolder);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return onItemMove(viewHolder.getLayoutPosition(), target.getLayoutPosition());
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            onItemDismiss(viewHolder.getLayoutPosition());
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            //因为ACTION_STATE_IDLE这个状态时ViewHolder为null所以下面用switch，避免空指针。
            // 而且ACTION_STATE_IDLE状态是也不需要记录。否则clearView方法中就拿不到这个状态，因为这个方法是先于clearView方法执行的。
            switch (actionState) {
                case ItemTouchHelper.ACTION_STATE_SWIPE://侧滑，将要删除条目。
                    mD = getObject(mLastActionPosition);
                case ItemTouchHelper.ACTION_STATE_DRAG://拖拽，将要移动条目。
                    mLastActionPosition = viewHolder.getLayoutPosition();
                    mLastActionState = actionState;
                    break;
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (mItemDragResultListener != null && mLastActionPosition != viewHolder.getLayoutPosition()) {
                switch (mLastActionState) {
                    case ItemTouchHelper.ACTION_STATE_DRAG://拖拽，将要移动条目。
                        mItemDragResultListener.onItemMoved(mLastActionPosition, viewHolder.getLayoutPosition(), getObject(viewHolder.getLayoutPosition()));
                        break;
                    case ItemTouchHelper.ACTION_STATE_SWIPE://侧滑，将要删除条目。
                        //这个用mLastActionPosition这个参数是应为在这里获取的viewHolder.getLayoutPosition()跟原来的position不一样，有偏差，偏差为1。
                        mItemDragResultListener.onItemDismissed(mLastActionPosition, mD);
                        break;
                }
            }
        }
    }
}
