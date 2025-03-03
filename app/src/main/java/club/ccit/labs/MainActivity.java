package club.ccit.labs;


import android.view.View;


import club.ccit.basic.BaseActivity;

import club.ccit.labs.databinding.ActivityMainBinding;
import club.ccit.view.dialog.LoadDialog;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    int num = 0;

    @Override
    protected void onCreate() {
        super.onCreate();

        setOnClickListener(binding.roundedImageView, binding.button);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == binding.roundedImageView) {
            num++;
            showToast("num:" + num);
        }
        if (view == binding.button) {
            LoadDialog.getInstance(this).showDialog();
            LoadDialog.getInstance(MainActivity.this).showDialog();
        }
    }

    @Override
    protected ScreenDirection setScreenOrientation() {
        return ScreenDirection.LANDSCAPE;
    }
}
