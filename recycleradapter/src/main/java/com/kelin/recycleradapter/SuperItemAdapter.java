package com.kelin.recycleradapter;

import android.database.Observable;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.kelin.recycleradapter.holder.ItemLayout;
import com.kelin.recycleradapter.holder.ItemViewHolder;
import com.kelin.recycleradapter.holder.ViewHelper;
import com.kelin.recycleradapter.interfaces.EventBindInterceptor;
import com.kelin.recycleradapter.interfaces.EventInterceptor;
import com.kelin.recycleradapter.interfaces.LayoutItem;
import com.kelin.recycleradapter.interfaces.ViewOperation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * 描述 {@link RecyclerView} 的适配器 {@link MultiTypeAdapter} 的子适配器。
 * 创建人 kelin
 * 创建时间 2017/1/19  下午4:22
 * 版本 v 1.0.0
 */

public abstract class SuperItemAdapter<D> implements EventInterceptor {

    private static final String TAG = "SuperItemAdapter";
    /**
     * 表示当前的条目是占满屏幕的。
     */
    public static final int SPAN_SIZE_FULL_SCREEN = SuperAdapter.SPAN_SIZE_FULL_SCREEN;
    /**
     * {@link MultiTypeAdapter} 对象。也是当前Adapter的父级Adapter，是输入RecyclerView的Adapter。
     */
    private MultiTypeAdapter mParentAdapter;
    /**
     * 用来记录当前Adapter在RecyclerView列表中的起始位置。
     */
    int firstItemPosition;
    /**
     * 用来记录当前Adapter在RecyclerView列表中的结束位置。
     */
    int lastItemPosition;
    /**
     * 适配器数据的观察者对象。
     */
    AdapterDataObservable mAdapterDataObservable = new AdapterDataObservable();
    /**
     * 用来存放ViewHolder的字节码对象。
     */
    private Class<? extends ItemViewHolder<D>> mHolderClass;
    /**
     * 用来记录当前适配器中的布局资源ID。
     */
    @LayoutRes
    private int mItemLayoutId;
    /**
     * 条目事件监听对象。
     */
    private OnItemEventListener<D> mItemEventListener;
    /**
     * 当前页面的数据集。
     */
    List<D> mDataList;
    /**
     * 用来记录当前子Adapter是否在屏幕内。
     */
    private boolean isVisible;
    /**
     * 用来拦截事件绑定的拦截器。
     */
    private EventBindInterceptor mEventInterceptor;
    /**
     * 用来存放悬浮条目的点击事件监听。
     */
    private ClickListenerPool mClickListenerPool;
    /**
     * 用来记录是否已经被添加。
     */
    boolean isAdded;

    public SuperItemAdapter(@NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        this(holderClass, null);
    }

    public SuperItemAdapter(@NonNull Class<? extends ItemViewHolder<D>> holderClass, D d) {
        this(null, holderClass);
        if (d != null) {
            if (mDataList == null) {
                mDataList = new ArrayList<>();
            }
            mDataList.add(d);
        }
    }

    public SuperItemAdapter(List<D> list, @NonNull Class<? extends ItemViewHolder<D>> holderClass) {
        mHolderClass = holderClass;
        ItemLayout annotation = holderClass.getAnnotation(ItemLayout.class);
        if (annotation != null) {
            mItemLayoutId = annotation.value();
        } else {
            throw new RuntimeException("view holder's root layout is not found! You must use \"@ItemLayout(rootLayoutId = @LayoutRes int)\" notes on your ItemViewHolder class");
        }
        if (mDataList != null) {
            if (list != null) {
                mDataList.addAll(list);
            }
        } else {
            if (list != null) {
                mDataList = list;
            } else {
                mDataList = new ArrayList<D>();
            }
        }
    }

    /**
     * 是否已经被添加到 {@link MultiTypeAdapter} 中了。
     * @return 返回true表示当前依然在 {@link MultiTypeAdapter} 中，false表示已经从 {@link MultiTypeAdapter} 中移除了。
     * 通常情况下如果被移除了说明这个子Adapter中已经没有了数据。
     */
    public boolean isAdded() {
        return isAdded;
    }

    /**
     * 设置当前是否可见。
     *
     * @param visible true表示可见，false表示不可见。
     */
    private void setVisibleState(boolean visible) {
        isVisible = visible;
    }

    /**
     * 判断当前适配器是否可见。
     *
     * @return 可见返回true, 不可见返回false。
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 获取当前的数据集合。
     */
    List<D> getDataList() {
        return mDataList;
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
     * 获取条目类型。
     *
     * @return 返回跟布局的资源ID。
     */
    @LayoutRes
    int getItemViewType() {
        return mItemLayoutId;
    }

    /**
     * 当需要创建ViewHolder的时候调用。
     *
     * @param parent   当前的parent对象，也就是RecyclerView对象。
     * @param viewType 当前的条目类型，也是当前要创建的ViewHolder的布局文件ID。
     * @return 需要返回一个 {@link ItemViewHolder<D>} 对象。
     */
    ItemViewHolder<D> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder<D> viewHolder;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            Constructor<? extends ItemViewHolder<D>> constructor = mHolderClass.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            viewHolder = constructor.newInstance(itemView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (viewHolder != null) {
            bindItemClickEvent(null, viewHolder);
            return viewHolder;

        } else {
            throw new RuntimeException("view holder's root layout is not found! You must use \"@ItemLayout(rootLayoutId = @LayoutRes int)\" notes on your ItemViewHolder class");
        }
    }


    /**
     * 设置条目的事件监听。
     *
     * @param listener {@link SingleTypeAdapter.OnItemEventListener} 对象。
     */
    public void setItemEventListener(@NonNull OnItemEventListener<D> listener) {
        mItemEventListener = listener;
    }

    public void notifyRefresh() {
        //因为ItemAdapter不是RecyclerView的Adapter，所以这里是调用父级Adapter的刷新。
        if (mParentAdapter != null) {
            mParentAdapter.notifyRefresh();
        }
    }

    public void onBindViewHolder(ItemViewHolder<D> holder, int position, List<Object> payloads) {
        if (position == 0) {
            setVisibleState(true);
            Log.i(TAG, "onViewRecycled: 条目被显示并绑定数据。AdapterPosition=" + position);
        }
        holder.mEventListener = mItemEventListener;
        holder.onBindPartData(position, getObject(position), payloads);
    }

    /**
     * 获取当前条目的总数量。
     */
    public int getItemCount() {
        return getDataList().size();
    }

    public abstract int getItemSpanSize();

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

    @SuppressWarnings("unchecked")
    private D getItemObject(int layoutPosition) {
        return (D) mParentAdapter.getObject(layoutPosition);
    }

    @SuppressWarnings("unchecked")
    private View.OnClickListener onGetClickListener(final LayoutItem item, final ItemViewHolder viewHolder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.mEventListener != null){
                    int position = item.getLayoutPosition();
                    SuperItemAdapter<D> itemAdapter = mParentAdapter.getChildAdapterByPosition(position);
                    int adapterPosition = position - itemAdapter.firstItemPosition;
                    D object = itemAdapter.getObject(adapterPosition);
                    viewHolder.mEventListener.adapter = itemAdapter;

                    if (v.getId() == item.getItemView().getId() || v.getId() == viewHolder.getItemClickViewId()) {
                        viewHolder.mEventListener.onItemClick(position, object, adapterPosition);
                    } else {
                        viewHolder.mEventListener.onItemChildClick(position, object, v, adapterPosition);
                    }
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    private View.OnLongClickListener onGetLongClickListener(final LayoutItem item) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                OnItemEventListener<D> listener = item instanceof ItemViewHolder ? ((ItemViewHolder) item).mEventListener : mItemEventListener;
                if (listener != null) {
                    int layoutPosition = item.getLayoutPosition();
                    listener.onItemLongClick(layoutPosition, getItemObject(layoutPosition), getAdapterPosition(layoutPosition));
                    return true;
                }
                return false;
            }
        };
    }

    private int getAdapterPosition(int layoutPosition) {
        return mParentAdapter.getItemAdapterPosition(layoutPosition);
    }

    final void setParent(MultiTypeAdapter parent) {
        mParentAdapter = parent;
    }

    final Class<?> getItemModelClass() {
        return isEmptyList() ? null : getObject(0).getClass();
    }

    final void bindItemClickEvent(@Nullable ViewHelper viewHelper, @NonNull final ItemViewHolder<D> viewHolder) {
        LayoutItem item;
        ViewOperation operation;
        boolean isFloatBind;
        if (viewHelper == null) {
            item = viewHolder;
            operation = viewHolder;
            isFloatBind = false;
        } else {
            item = (LayoutItem) viewHelper.getRootView();
            operation = viewHelper;
            isFloatBind = true;
        }
        View.OnClickListener onClickListener;
        View.OnLongClickListener onLongClickListener;
        if (isFloatBind) {
            if (mClickListenerPool == null || (onClickListener = mClickListenerPool.acquireClick(item.getLayoutPosition())) == null || (onLongClickListener = mClickListenerPool.acquireLongClick(item.getLayoutPosition())) == null) {
                onClickListener = onGetClickListener(item, viewHolder);
                onLongClickListener = onGetLongClickListener(item);
                if (mClickListenerPool == null) {
                    mClickListenerPool = new ClickListenerPool();
                }
                mClickListenerPool.releaseClickInstance(item.getLayoutPosition(), onClickListener);
                mClickListenerPool.releaseLongClickInstance(item.getLayoutPosition(), onLongClickListener);
            }
        } else {
            onClickListener = onGetClickListener(item, viewHolder);
            onLongClickListener = onGetLongClickListener(item);
        }
        if (viewHolder.clickable()) {
            View clickView;
            if (viewHolder.getItemClickViewId() == 0 || (clickView = operation.getView(viewHolder.getItemClickViewId())) == null) {
                clickView = item.getItemView();
            }
            clickView.setOnClickListener(onClickListener);
            clickView.setOnLongClickListener(onLongClickListener);
        }
        int[] childViewIds = viewHolder.onGetNeedListenerChildViewIds();
        if (childViewIds != null && childViewIds.length > 0) {
            View v;
            for (int viewId : childViewIds) {
                v = operation.getView(viewId);
                if (v != null && (mEventInterceptor == null || !mEventInterceptor.onInterceptor(v, item))) {
                    v.setOnClickListener(onClickListener);
                }
            }
        }

        //悬浮条目不允许拖拽，只有item==viewHolder才不是悬浮条的监听。
        if (viewHelper == null && viewHolder.getDragHandleViewId() != 0) {
            viewHolder.getView(viewHolder.getDragHandleViewId()).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getId() == viewHolder.getDragHandleViewId()) {
                        mParentAdapter.startDrag(viewHolder);
                    }
                    return false;
                }
            });
        }
    }

    final void mapNotifyItemInserted(int position) {
        if (mParentAdapter != null) mParentAdapter.notifyItemInserted(position + firstItemPosition);
    }

    final void mapNotifyItemChanged(int position) {
        if (mParentAdapter != null) mParentAdapter.notifyItemChanged(position + firstItemPosition);
    }

    final void mapNotifyItemRangeInserted(int positionStart, int itemCount) {
        if (mParentAdapter != null)
            mParentAdapter.notifyItemRangeInserted(positionStart + firstItemPosition, itemCount);
    }

    private void mapNotifyItemMove(int fromPosition, int toPosition) {
        if (mParentAdapter != null)
            mParentAdapter.notifyItemMoved(fromPosition + firstItemPosition, toPosition + firstItemPosition);
    }

    final void mapNotifyItemRemoved(int position) {
        if (mParentAdapter != null) {
            mParentAdapter.notifyItemRemoved(position + firstItemPosition);
        }
    }

    final void mapNotifyItemRangeRemoved(int positionStart, int itemCount) {
        if (isEmptyList()) {
            positionStart = firstItemPosition;

        } else {
            positionStart += firstItemPosition;
        }
        if (mParentAdapter != null) mParentAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    /**
     * 注册数据观察者。
     *
     * @param observer 观察者对象。
     */
    final void registerObserver(AdapterDataObserver observer) {
        mAdapterDataObservable.registerObserver(observer);
    }

    /**
     * 取消注册数据观察者。
     *
     * @param observer 观察者对象。
     */
    final void unRegisterObserver(AdapterDataObserver observer) {
        mAdapterDataObservable.unregisterObserver(observer);
    }

    /**
     * 取消注册所有数据观察者。
     */
    final void unregisterAll() {
        mAdapterDataObservable.unregisterAll();
    }

    /**
     * 当当前Adapter中视图中消失的时候调用。
     *
     * @param holder   当前的ViewHolder对象。
     * @param position 当前的索引。
     */
    @CallSuper
    void onViewRecycled(ItemViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            setVisibleState(false);
            Log.i(TAG, "onViewRecycled: 条目被释放。AdapterPosition=" + position);
        }
    }

    /**
     * 设置事件绑定拦截器。
     *
     * @param interceptor {@link EventBindInterceptor} 拦截器对象。
     */
    @Override
    public final void setEventInterceptor(EventBindInterceptor interceptor) {
        mEventInterceptor = interceptor;
    }

    boolean onItemMove(int fromPosition, int toPosition) {
        if (getItemSpanSize() == mParentAdapter.getTotalSpanSize()) {
            Collections.swap(mDataList, fromPosition, toPosition);
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDataList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDataList, i, i - 1);
                }
            }
        }
        mAdapterDataObservable.move(fromPosition, toPosition);
        mapNotifyItemMove(fromPosition, toPosition);
        return true;
    }

    void onItemDismiss(int position) {
        boolean remove = mDataList.size() > position;
        if (remove) {
            mAdapterDataObservable.remove(mDataList.remove(position));
            mapNotifyItemRemoved(position);
        }
    }

    /**
     * 当前适配器的数据改变被观察者。
     */
    protected class AdapterDataObservable extends Observable<AdapterDataObserver> {

        void add(int position, Object object) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).add(position, object, SuperItemAdapter.this);
            }
        }

        void addAll(int firstPosition, Collection<D> dataList) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).addAll(firstPosition, dataList, SuperItemAdapter.this);
            }
        }

        void move(int fromPosition, int toPosition) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).move(fromPosition, toPosition, SuperItemAdapter.this);
            }
        }

        void remove(Object d) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).remove(d, SuperItemAdapter.this);
            }
        }

        void removeAll(Collection<D> dataList) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).removeAll(dataList, SuperItemAdapter.this);
            }
        }
    }

    /**
     * 当前适配器的数据观察者对象。
     */
    interface AdapterDataObserver {
        /**
         * 列表中新增了数据。
         *
         * @param position 被新增的位置。
         * @param object   新增的数据。
         * @param adapter  当前被观察的Adapter对象。
         */
        void add(int position, Object object, SuperItemAdapter adapter);

        /**
         * 列表中批量新增了数据。
         *
         * @param firstPosition 新增的起始位置。
         * @param dataList      新增的数据集合。
         * @param adapter       当前被观察的Adapter对象。
         */
        void addAll(int firstPosition, Collection dataList, SuperItemAdapter adapter);

        /**
         * 删除了列表中的数据。
         *
         * @param object  被删除的数据。
         * @param adapter 当前被观察的Adapter对象。
         */
        void remove(Object object, SuperItemAdapter adapter);

        /**
         * 批量删除了列表中的数据。
         *
         * @param dataList 被删除的数据集合。
         * @param adapter  当前被观察的Adapter对象。
         */
        void removeAll(Collection dataList, SuperItemAdapter adapter);

        /**
         * 列表中的数据位置被移动。
         * @param fromPosition 移动前的位置。
         * @param toPosition 移动后的位置。
         * @param adapter  当前被观察的Adapter对象。
         */
        void move(int fromPosition, int toPosition, SuperItemAdapter adapter);
    }

    /**
     * 描述 {@link RecyclerView} 的条目点击事件监听。
     * 创建人 kelin
     * 创建时间 2017/1/19  下午12:21
     * 版本 v 1.0.0
     *
     * @param <D> 数据模型的泛型限定。
     */
    public static abstract class OnItemEventListener<D> {
        private SuperItemAdapter<D> adapter;

        protected final SuperItemAdapter<D> getAdapter() {
            return adapter;
        }

        /**
         * 当条目被点击的时候调用。
         *
         * @param position        当前被点击的条目在 {@link RecyclerView} 中的索引。
         * @param d               被点击的条目的条目信息对象。
         * @param adapterPosition 当前被点击的条目在 {@link RecyclerView.Adapter} 中的索引,
         *                        一般情况下该参数的值会和 position 参数的值一致。只有当使用 {@link SuperItemAdapter}
         *                        作为 {@link MultiTypeAdapter} 的子条目并使用该监听时这个值才有意义，
         *                        这时该参数的值将会与 position 参数的值不同，该参数的值将表示当前点击的条目在当前子 Adapter 中的位置。
         */
        public abstract void onItemClick(int position, D d, int adapterPosition);

        /**
         * 当条目被长时点击的时候调用。
         *
         * @param position        当前被点击的条目在 {@link RecyclerView} 中的索引。
         * @param d               被点击的条目的条目信息对象。
         * @param adapterPosition 当前被点击的条目在 {@link RecyclerView.Adapter} 中的索引,
         *                        一般情况下该参数的值会和 position 参数的值一致。只有当使用 {@link SuperItemAdapter}
         *                        作为 {@link MultiTypeAdapter} 的子条目并使用该监听时这个值才有意义，
         *                        这时该参数的值将会与 position 参数的值不同，该参数的值将表示当前点击的条目在当前子 Adapter 中的位置。
         */
        public void onItemLongClick(int position, D d, int adapterPosition) {
        }

        /**
         * 当条目中的子控件被点击的时候调用。
         *       * @param position 当前被点击的条目在 {@link RecyclerView} 中的索引。
         *
         * @param d               被点击的条目的条目信息对象。
         * @param view            被点击的{@link View}。
         * @param adapterPosition 当前被点击的条目在 {@link RecyclerView.Adapter} 中的索引,
         *                        一般情况下该参数的值会和 position 参数的值一致。只有当使用 {@link SuperItemAdapter}
         *                        作为 {@link MultiTypeAdapter} 的子条目并使用该监听时这个值才有意义，
         *                        这时该参数的值将会与 position 参数的值不同，该参数的值将表示当前点击的条目在当前子 Adapter 中的位置。
         */
        public abstract void onItemChildClick(int position, D d, View view, int adapterPosition);
    }

    private class ClickListenerPool {

        SparseArray<View.OnClickListener> clickListeners = new SparseArray<>();
        SparseArray<View.OnLongClickListener> longClickListeners = new SparseArray<>();

        /**
         * 根据key获取一个点击监听。
         *
         * @param key 要获取的对象的key。
         */
        View.OnClickListener acquireClick(int key) {
            return clickListeners.get(key);
        }

        /**
         * 根据key获取一个长按监听。
         *
         * @param key 要获取的对象的key。
         */
        View.OnLongClickListener acquireLongClick(int key) {
            return longClickListeners.get(key);
        }

        /**
         * 发布一个点击监听到池子中。
         *
         * @param instance 要发布的对象。
         */
        void releaseClickInstance(int key, View.OnClickListener instance) {
            clickListeners.put(key, instance);
        }

        /**
         * 发布一个长按监听到池子中。
         *
         * @param instance 要发布的对象。
         */
        void releaseLongClickInstance(int key, View.OnLongClickListener instance) {
            longClickListeners.put(key, instance);
        }
    }
}
