package club.ccit.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import club.ccit.view.R;

public class RoundedImageView extends AppCompatImageView {
    // 增加默认圆角值
    private final float DEFAULT_RADIUS = 0f;
    private float cornerRadius = DEFAULT_RADIUS;
    private final Path path = new Path();
    private final RectF rect = new RectF();

    public RoundedImageView(@NonNull Context context) {
        super(context);
    }

    public RoundedImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundedImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 修改初始化逻辑
    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = null;
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
            cornerRadius = ta.getDimension(R.styleable.RoundedImageView_imageView_radius, DEFAULT_RADIUS);
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 重置路径避免重复叠加
        path.reset();
        rect.set(0, 0, getWidth(), getHeight());
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);

        // 合并绘制逻辑
        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
    }
}