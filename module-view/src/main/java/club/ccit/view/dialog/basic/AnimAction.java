package club.ccit.view.dialog.basic;


import club.ccit.view.R;

/**
 * @author swzhang3
 * name: Animations
 * date: 2024/6/24 14:29
 * description:
 **/
public interface AnimAction {
    /**
     * 默认动画效果
     */
    int ANIM_DEFAULT = -1;

    /**
     * 没有动画效果
     */
    int ANIM_EMPTY = 0;

    /**
     * 缩放动画
     */
    int ANIM_SCALE = R.style.ScaleAnimStyle;

    /**
     * 吐司动画
     */
    int ANIM_TOAST = android.R.style.Animation_Toast;

    /**
     * 顶部弹出动画
     */
//    int ANIM_TOP = R.style.TopAnimStyle;

    /**
     * 底部弹出动画
     */
    int ANIM_BOTTOM = R.style.BottomAnimStyle;
}
