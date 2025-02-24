package club.ccit.view.title;

/**
 * FileName: OnTitleBarListener
 *
 * @author: 张帅威
 * Date: 2022/2/17 9:40 上午
 * Description: 标题栏点击监听接口
 * Version:
 */
public interface OnTitleBarListener {
    /**
     * 左项被点击
     */
    default void onLeftClick(TitleBar titleBar) {}

    /**
     * 标题被点击
     */
    default void onTitleClick(TitleBar titleBar) {}

    /**
     * 右项被点击
     */
    default void onRightClick(TitleBar titleBar) {}
}
