package club.ccit.labs;

import android.view.View;

import club.ccit.aop.click.SingleClick;
import club.ccit.basic.BaseActivity;

import club.ccit.labs.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    int num = 0;

    @Override
    protected void onCreate() {
        super.onCreate();

        setOnClickListener(binding.roundedImageView);
    }

    @SingleClick(2000)
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == binding.roundedImageView) {
            num++;
            showToast("num:" + num);
        }
    }

    @Override
    protected ScreenDirection setScreenOrientation() {
        return ScreenDirection.LANDSCAPE;
    }
}
