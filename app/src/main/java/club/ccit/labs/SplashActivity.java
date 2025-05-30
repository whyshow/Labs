package club.ccit.labs;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.Objects;

import club.ccit.basic.BaseViewDataActivity;
import club.ccit.home.ui.MainActivity;
import club.ccit.labs.databinding.ActivitySplashBinding;

public class SplashActivity extends BaseViewDataActivity<ActivitySplashBinding> {


    @Override
    protected void onCreate() {
        super.onCreate();
        if (!this.isTaskRoot()) {
            // 如果有活动就关闭当前页面
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Objects.equals(action, Intent.ACTION_MAIN)) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 100);
            }
        } else {
            // 否则启动主页面
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }
}
