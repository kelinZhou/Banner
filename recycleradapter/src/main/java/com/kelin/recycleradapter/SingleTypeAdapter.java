package com.kelin.recycleradapter;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.kelin.recycleradapter.holder.ItemViewHolder;
import com.kelin.recycleradapter.interfaces.Orientation;

import java.util.List;

/**
 * 描述 {@link RecyclerView} 的单类型适配器。
 * 创建人 kelin
 * 创建时间 2016/11/28  上午10:09
 * 版本 v 1.0.0
 */

public class SingleTypeAdapter<D, H extends ItemViewHolder<D>> extends EditSuperAdapter<D, H> {

    /**
     * 当前的条目点击监听对象。
     */
    private OnItemEventListener<D, SingleTypeAdapter<D, H>> mItemEventListener;

    public SingleTypeAdapter(@NonNull RecyclerView recyclerView, Class<? extends H> holderClass) {
        this(recyclerView, null, holderClass);
    }

    public SingleTypeAdapter(@NonNull RecyclerView recyclerView, List<D> list, Class<? extends H> holderClass) {
        this(recyclerView, 1, 1, list, holderClass);
    }

    public SingleTypeAdapter(@NonNull RecyclerView recyclerView, @Size(min = 1, max = 100) int totalSpanSize, @Size(min = 1, max = 100) int spanSize, List<D> list, Class<? extends H> holderClass) {
        this(recyclerView, totalSpanSize, spanSize, LinearLayout.VERTICAL, list, holderClass);
    }

    public SingleTypeAdapter(@NonNull RecyclerView recyclerView, @Size(min = 1, max = 100) int totalSpanSize,  @Size(min = 1, max = 100) int spanSize, @Orientation int orientation, List<D> list, Class<? extends H> holderClass) {
        super(recyclerView, totalSpanSize, spanSize, orientation, list, holderClass);
    }

    /**
     * 获取指定对象在列表中的位置。
     *
     * @param object 要获取位置的对象。
     * @return 返回该对象在里列表中的位置。
     */
    public int getItemPosition(D object) {
        if (isEmptyList()) {
            return -1;
        }
        return getDataList().indexOf(object);
    }

    /**
     * 获取条目在Adapter中的位置。
     *
     * @param holder 当前的ViewHolder对象。
     */
    protected int getAdapterPosition(H holder) {
        return holder.getLayoutPosition();
    }

    /**
     * 设置条目的事件监听。
     *
     * @param listener {@link OnItemEventListener} 对象。
     */
    public void setItemEventListener(@NonNull OnItemEventListener<D, SingleTypeAdapter<D, H>> listener) {
        mItemEventListener = listener;
        mItemEventListener.adapter = SingleTypeAdapter.this;
    }

    @Override
    protected View.OnClickListener onGetClickListener(final H viewHolder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemEventListener == null) return;
                int position = viewHolder.getLayoutPosition();
                D object = getObject(viewHolder.getLayoutPosition());
                if (v.getId() == viewHolder.itemView.getId() || v.getId() == viewHolder.getItemClickViewId()) {
                    mItemEventListener.onItemClick(position, object);
                } else {
                    mItemEventListener.onItemChildClick(position, object, v);
                }
            }
        };
    }

    @Override
    protected View.OnLongClickListener onGetLongClickListener(final H viewHolder) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemEventListener != null) {
                    mItemEventListener.onItemLongClick(viewHolder.getLayoutPosition(), getObject(viewHolder.getLayoutPosition()));
                }
                return true;
            }
        };
    }

    public static abstract class OnItemEventListener<D, A extends SingleTypeAdapter> {

        private A adapter;

        /**
         * 获取当前的Adapter对象。
         */
        protected @NonNull A getAdapter() {
            return adapter;
        }

        /**
         * 当条目被点击的时候调用。
         *
         * @param position 当前被点击的条目在 {@link RecyclerView} 中的索引。
         * @param d        被点击的条目的条目信息对象。
         */
        public abstract void onItemClick(int position, D d);

        /**
         * 当条目被长时点击的时候调用。
         *
         * @param position 当前被点击的条目在 {@link RecyclerView} 中的索引。
         * @param d        被点击的条目的条目信息对象。
         */
        public void onItemLongClick(int position, D d){}

        /**
         * 当条目中的子控件被点击的时候调用。
         *
         * @param position 当前被点击的条目在 {@link RecyclerView} 中的索引。
         * @param d        被点击的条目的条目信息对象。
         * @param view 被点击的{@link View}。
         */
        public void onItemChildClick(int position, D d, View view){}
    }
}