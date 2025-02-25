package club.ccit.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import club.ccit.view.R;

public class RoundedImageView extends AppCompatImageView {
    private float cornerRadius = 0f;
    private final Path path = new Path();
    private final RectF rect = new RectF();

    public RoundedImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try (TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView)) {
            cornerRadius = ta.getDimension(R.styleable.RoundedImageView_radius, 0);
            ta.recycle();
        }

        // 解决部分设备硬件加速不兼容问题
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 创建裁剪区域
        rect.set(0, 0, getWidth(), getHeight());
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

        // 先绘制背景（如果有）
        if (getBackground() != null) {
            canvas.clipPath(path);
            super.onDraw(canvas);
        }
        // 再绘制前景图片
        else if (getDrawable() != null) {
            canvas.clipPath(path);
            super.onDraw(canvas);
        }
    }
}