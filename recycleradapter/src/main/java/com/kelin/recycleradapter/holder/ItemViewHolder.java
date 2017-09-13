package com.kelin.recycleradapter.holder;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelin.recycleradapter.SuperItemAdapter;
import com.kelin.recycleradapter.callback.NotifyCallback;
import com.kelin.recycleradapter.interfaces.LayoutItem;
import com.kelin.recycleradapter.interfaces.ViewOperation;
import com.kelin.recycleradapter.FloatLayout;

import java.util.List;

/**
 * 描述 {@link RecyclerView} 中的条目的 {@link RecyclerView.ViewHolder} 对象。
 * 创建人 kelin
 * 创建时间 2017/1/19  下午12:15
 * 版本 v 1.0.0
 */
public abstract class ItemViewHolder<D> extends RecyclerView.ViewHolder implements NotifyCallback<D>, ViewOperation, LayoutItem {

    public SuperItemAdapter.OnItemEventListener<D> mEventListener;
    private final SparseArray<View> mViews;

    protected ItemViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    /**
     * 绑定数据的时候调用。
     *
     * @param position 当前的Item索引。
     * @param d        当前索引对应的数据对象。
     */
    public abstract void onBindData(int position, D d);

    /**
     * 绑定数据的时候调用。与 {@link #onBindData(int, Object)} 方法不用的是，这个方法可以用来局部绑定数据。
     * 就是说当一个条目中的数据模型发生变化以后但不是展示在UI上的每个字段都发生了变化的情况下就可以通过该方法只针对部分改动过的字段进行数据绑定。
     * <p>但是该方法也不是必然就会被执行的，而是在 {@link NotifyCallback#getChangePayload(Object, Object, Bundle)} 方法中获得了不同之处后才会被执行。
     * <p>该方法的默认实现是调用 {@link #onBindData(int, Object)} 方法，如果你希望通过局部刷新来提高效率则重写该方法。
     * @param position 当前的Item索引。
     * @param d        当前索引对应的数据对象。
     * @param payloads 数据模型被改变的内容。
     */
    public void onBindPartData(int position, D d, List<Object> payloads){
        onBindData(position, d);
    }

    /**
     * 如果你不希望通过点击{@link #itemView}触发条目点击事件，而是希望通过点击自己自定的控件触发条目点击事件，则需要重写该方法。
     * <p>如果你重写了该方法并返回的ViewId包含在 {@link #onGetNeedListenerChildViewIds()} 返回的数组里，则条目点击事件会被触发，子控件点击不会被触发。
     * @return 返回你希望触发条目点击事件的ViewId。
     */
    @IdRes
    public int getItemClickViewId() {
        return 0;
    }

    /**
     * 获取需要绑定事件子控件的ViewIds。
     * @return 返回需要绑定事件的子控件的ViewId数组。
     */
    @IdRes
    public int[] onGetNeedListenerChildViewIds(){
        return null;
    }

    /**
     * 判断两个数据模型是否为同一个数据模型。如果指定模型有唯一标识应当以唯一标识去判断。这里的默认实现是通过 {@link #equals(Object)} 方法去判断，
     * 你可以通过重写 {@link D#equals(Object)} 方法进行处理，也可以重写该方法进行处理。
     * <P>如果你没有重写该方法则最好重写 {@link D#equals(Object)} 方法进行唯一标识字段的比较，否则有可能会造成不必要刷新的item刷新。
     * @param oldItemData 旧的数据模型。
     * @param newItemDate 新的数据模型。
     * @return 如果相同则应当返回 <code color="blue">true</code>,否则应当返回 <code color="blue">false</code>。
     */
    @Override
    public boolean areItemsTheSame(D oldItemData, D newItemDate) {
        return oldItemData != null && oldItemData.equals(newItemDate);
    }

    /**
     * 判断两个模型的内容是否相同。
     * <p>当 {@link #areItemsTheSame(Object, Object)} 方法返回 <code>true</code> 时，此方法才会被调用，这是因为如果两个对象
     * 的基本特征都是不同的或，就没有进行进一步比较的必要了。
     * <p>你不必将模型中的所有字段进行比较，只需要将需要展示到UI上的字段进行比较就可以了，你也可以将这个比较放到
     * {@link #equals(Object)} 方法中去做。
     *
     * @param oldItemData 旧的数据模型。
     * @param newItemDate 新的数据模型。
     * @return 如果相同则返回 <code color="blue">true</code>, 否则应当返回 <code color="blue">false</code>。
     * @see #areItemsTheSame(D, D);
     */
    @Override
    public boolean areContentsTheSame(D oldItemData, D newItemDate) {
        return oldItemData != null && oldItemData.equals(newItemDate);
    }

    /**
     * 由{@link com.kelin.recycleradapter.SuperAdapter} 调用，用来获取两个对象不同的部分。这个方法只有在
     * {@link #areItemsTheSame(Object, Object)} 方法被调用并返回<code>true</code>后才会被调用。
     * <p>你并不需要将模型中的所有字段进行比较，只需要比较需要展示在界面上的字段就可以了。
     * @param oldItemData 旧的数据模型。
     * @param newItemDate 新的数据模型。
     * @param bundle 比较两个对象不同的部分，将两个对象不同的部分以键值对的形式存入该参数中。这个参数你会在
     * {@link #onBindPartData(int, Object, List)} 方法中获得。
     *
     * @see #onBindPartData(int, Object, List)
     */
    @Override
    public void getChangePayload(D oldItemData, D newItemDate, Bundle bundle) {}

    @Override
    public <T extends View> T getView(int id) {
        View view = mViews.get(id);
        if (view != null) {
            return (T) view;
        }
        view = itemView.findViewById(id);
        mViews.put(id, view);
        return (T) view;
    }

    @Override
    public void setBackGround(Drawable drawable, @IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setBackground(drawable);
            }
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color, @IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setBackgroundColor(color);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint, @IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setBackgroundTintList(tint);
            }
        }
    }

    @Override
    public void setBackgroundResource(@DrawableRes int drawable, @IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setBackgroundResource(drawable);
            }
        }
    }

    @Override
    public void setText(@IdRes int textViewId, CharSequence text) {
        TextView view = getView(textViewId);
        view.setText(text);
    }

    @Override
    public void setText(@IdRes int textViewId, @StringRes int textRes) {
        TextView view = getView(textViewId);
        String s = view.getContext().getString(textRes);
        view.setText(s);
    }

    @Override
    public void setHint(@IdRes int textViewId, CharSequence text) {
        TextView view = getView(textViewId);
        view.setHint(text);
    }

    @Override
    public void setHint(@IdRes int textViewId, @StringRes int textRes) {
        TextView view = getView(textViewId);
        String s = view.getContext().getString(textRes);
        view.setHint(s);
    }

    @Override
    public void setGone(@IdRes int...viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setInvisible(@IdRes int...viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void setVisible(@IdRes int...viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                getView(viewId).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setTextColor(@NonNull ColorStateList color, @IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                ((TextView)getView(viewId)).setTextColor(color);
            }
        }
    }

    @Override
    public void setTextColor(@ColorInt int color, @IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            for (int viewId : viewIds) {
                ((TextView)getView(viewId)).setTextColor(color);
            }
        }
    }

    /**
     * 给imageView设置图片。
     *
     * @param viewId 要设置的 {@link ImageView} 的ID。
     * @param img    图片资源。
     */
    @Override
    public void setImageResource(@IdRes int viewId, @DrawableRes int img) {
        ((ImageView)getView(viewId)).setImageResource(img);
    }

    /**
     * 当需要绑定悬浮控件的数据的时候调用。
     * <p>
     * 你要绑定数据的控件的 {@link IdRes} 资源Id名称都是和你ViewHolder中的资源Id名称相同的。
     * <p>例如:你ViewHolder中通过 <code>setText(R.id.tv_title, user.getUserName())</code> 这样的方式给一个TextView设置Text,
     * 那么就可以通过参数中的viewHelper用同样的方式设置Text： <code>viewHelper.setText(R.id.tv_title, user.getUserName())</code>。
     *
     * <h1><font color="#619BE5">注意：</font> </h1>
     *      {@link ViewHelper} 类只是封装了一些常用的与一些常用View相关的操作，例如：setText()、setTextColor()、setBackground()等方法，
     *      并没有把全部的与View相关的操作都封装进去，这是没有必要的也是不可能的，因为我并不知道你以后会写出什么样的花哨的自定义控件。
     *      <p>如果你没有找到你所希望提供的方法操作View该怎么办呢？放心，你仍然可以通过 {@link ViewHelper#getView(int)} 方法获取到你所关心的
     *      View然后对他们进行你所需要的操作。
     * @param viewHelper {@link ViewHelper} View相关操作的帮助对象。
     * @param d 需要绑定的数据模型。
     *
     * @see FloatLayout
     */
    public void onBindFloatLayoutData(ViewHelper viewHelper, D d) {}

    /**
     * 由 {@link RecyclerView.Adapter} 调用，当该方法被调用的时候说明当前的ViewHolder已经可以被复用，
     * 也就是时候当前的ViewHolder已经不在屏幕区域内了。
     * @see RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)。
     */
    public void onViewRecycled() {}

    /**
     * 当前条目可以否被点击。默认所有条目都是可以被点击的，都会触发条目点击事件和长按事件。
     * 子类可以通过覆盖该方法来改变条目是否可以被点击。
     * <P>如果子类覆盖了该方法并返回了<code>false</code>那么适配器则不会对这个条目绑定任何事件，但是子View的事件绑定并不受影响。
     * @return <code>true</code>表示可以点击，<code>false</code>表示不可点击。
     */
    public boolean clickable() {
        return true;
    }

    @Override
    public View getItemView() {
        return itemView;
    }

    /**
     * 如果你通过 {@link com.kelin.recycleradapter.SuperAdapter#setItemDragEnable(boolean, boolean)} 方法设置了RecyclerView
     * 可以侧滑删除的话，那么表示所有的条目都是可以被删除的。如果你有些条目不希望被删除，则可以从写该方法并返回false。
     * @return 是否允许侧滑删除。
     */
    public boolean getSwipedEnable() {
        return true;
    }

    /**
     * 如果你通过 {@link com.kelin.recycleradapter.SuperAdapter#setItemDragEnable(boolean, boolean)} 方法设置了RecyclerView
     * 可以拖拽的话，那么表示所有的条目都是可以被拖拽的。如果你有些条目不希望被拖拽，则可以从写该方法并返回false。
     * @return 是否允许侧滑删除。
     */
    public boolean getDragEnable() {
        return true;
    }

    /**
     * 返回触发拖拽移动条目位置的控件Id。如果你只希望通过长按触发拖拽则不需要从写该方法，另外如果你重写了 {@link #getDragEnable()}
     * 方法并返回的false，那么也不需要该方法。
     * @return 返回触发拖拽的view的Id。
     */
    @IdRes
    public int getDragHandleViewId() {
        return 0;
    }
}
