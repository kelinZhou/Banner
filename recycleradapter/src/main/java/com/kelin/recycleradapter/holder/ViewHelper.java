package com.kelin.recycleradapter.holder;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelin.recycleradapter.interfaces.ViewOperation;

/**
 * 描述 一个View相关操作的帮助类。
 * 创建人 kelin
 * 创建时间 2017/5/26  下午4:00
 * 版本 v 1.0.0
 */

public class ViewHelper implements ViewOperation {
    /**
     * 当前帮助对象（View）的根View容器。
     */
    private View rootView;
    /**
     * 所有子View的容器。
     */
    private SparseArray<View> mViews = new SparseArray<>();

    public ViewHelper(View rootView) {
        this.rootView = rootView;
    }

    /**
     * 根据{@link View}的Id获取一个{@link View}。
     *
     * @param id 一个{@link View}的Id。
     * @return 返回一个 {@link View}对象。
     */
    @Override
    public <T extends View> T getView(int id) {
        View view = mViews.get(id);
        if (view != null) {
            return (T) view;
        }
        view = rootView.findViewById(id);
        mViews.put(id, view);
        return (T) view;
    }

    public View getRootView() {
        return rootView;
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
}
