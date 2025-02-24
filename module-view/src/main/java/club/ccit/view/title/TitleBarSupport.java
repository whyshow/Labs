package club.ccit.view.title;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * FileName: TitleBarSupport
 *
 * @author: 张帅威
 * Date: 2022/2/17 9:36 上午
 * Description: 标题栏支持类
 * Version:
 */
public class TitleBarSupport {
    /** 无色值 */
    public static final int NO_COLOR = Color.TRANSPARENT;

    /**
     * 获取图片资源
     */
    public static Drawable getDrawable(Context context, int id) {
        return context.getResources().getDrawable(id, context.getTheme());
    }

    /**
     * 设置 View 背景
     */
    public static void setBackground(View view, Drawable drawable) {
        view.setBackground(drawable);
    }

    /**
     * 获取绝对重心
     */
    public static int getAbsoluteGravity(View view, int gravity) {
        // 适配布局反方向
        return Gravity.getAbsoluteGravity(gravity, view.getResources().getConfiguration().getLayoutDirection());
    }

    /**
     * 是否启用了布局反方向特性
     */
    public static boolean isLayoutRtl(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    /**
     * TextView 是否存在内容
     */
    public static boolean isContainContent(TextView textView) {
        CharSequence text = textView.getText();
        if (!TextUtils.isEmpty(text)) {
            return true;
        }
        Drawable[] drawables = textView.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 给图片设置着色器
     */
    public static void setDrawableTint(Drawable drawable, int color) {
        if (drawable == null) {
            return;
        }
        if (color == NO_COLOR) {
            return;
        }
        drawable.mutate();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    /**
     * 清除图片设置着色器
     */
    public static void clearDrawableTint(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        drawable.mutate();
        drawable.clearColorFilter();
    }

    /**
     * 根据给定的大小限制 Drawable 宽高
     */
    public static void setDrawableSize(Drawable drawable, int width, int height) {
        if (drawable == null ) {
            return;
        }

        if (width <= 0 && height <= 0) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return;
        }

        if (width > 0 && height > 0) {
            drawable.setBounds(0, 0, width, height);
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth <= 0) {
            drawableWidth = width;
        }
        if (drawableHeight <= 0) {
            drawableHeight = height;
        }

        // 将 Drawable 等比缩放
        if (width > 0) {
            drawable.setBounds(0, 0, width, width * drawableHeight / drawableWidth);
        } else {
            drawable.setBounds(0, 0, drawableWidth * height / drawableHeight, height);
        }
    }

    /**
     * 根据图片重心获取在 TextView 的 Drawable 对象
     */
    public static Drawable getTextCompoundDrawable(TextView textView, int gravity) {
        Drawable[] drawables = textView.getCompoundDrawables();
        return switch (getAbsoluteGravity(textView, gravity)) {
            case Gravity.LEFT -> drawables[0];
            case Gravity.TOP -> drawables[1];
            case Gravity.RIGHT -> drawables[2];
            case Gravity.BOTTOM -> drawables[3];
            default -> null;
        };
    }

    /**
     * 根据图标重心设置 TextView 某个位置的 Drawable
     */
    public static void setTextCompoundDrawable(TextView textView, Drawable drawable, int gravity) {
        switch (getAbsoluteGravity(textView, gravity)) {
            case Gravity.LEFT:
                textView.setCompoundDrawables(drawable, null, null, null);
                break;
            case Gravity.TOP:
                textView.setCompoundDrawables(null, drawable, null, null);
                break;
            case Gravity.RIGHT:
                textView.setCompoundDrawables(null, null, drawable, null);
                break;
            case Gravity.BOTTOM:
                textView.setCompoundDrawables(null, null, null, drawable);
                break;
            default:
                textView.setCompoundDrawables(null, null, null, null);
                break;
        }
    }

    /**
     * 根据文字样式返回不同的字体样式
     */
    public static Typeface getTextTypeface(int style) {
        switch (style) {
            case Typeface.BOLD:
                return Typeface.DEFAULT_BOLD;
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                return Typeface.MONOSPACE;
            case Typeface.NORMAL:
            default:
                return Typeface.DEFAULT;
        }
    }
}
