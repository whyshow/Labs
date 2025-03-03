package club.ccit.basic.action;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

/**
 * FileName: ClickAction
 *
 * @author: 张帅威
 * Date: 2022/2/11 2:33 下午
 * Description: 点击事件意图
 * Version:
 */

public interface ClickAction extends View.OnClickListener {
    /**
     * 寻找控件id
     *
     * @param id
     * @param <V>
     * @return
     */
    <V extends View> V findViewByIds(@IdRes int id);

    /**
     * 设置监听事件
     *
     * @param ids
     */
    default void setOnClickListener(@IdRes int... ids) {
        setOnClickListener(this, ids);
    }

    /**
     * 循环添加监听事件
     *
     * @param listener
     * @param ids
     */
    default void setOnClickListener(@Nullable View.OnClickListener listener, @IdRes int... ids) {
        for (int id : ids) {
            findViewByIds(id).setOnClickListener(listener);
        }
    }

    /**
     * 设置监听事件的view
     *
     * @param views
     */
    default void setOnClickListener(View... views) {
        setOnClickListener(this, views);
    }

    /**
     * 循环添加监听事件
     *
     * @param listener
     * @param views
     */
    default void setOnClickListener(@Nullable View.OnClickListener listener, View... views) {
        for (View view : views) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    default void onClick(View view) {
        // 默认不实现，让子类实现

    }
}
