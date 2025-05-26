package club.ccit.view.dialog;


import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import club.ccit.view.databinding.DialogLoadBinding;

import club.ccit.view.dialog.basic.BaseDialogFragment;

public class LoadDialog extends BaseDialogFragment<DialogLoadBinding> {
    private static LoadDialog loadDialog;
    private AppCompatActivity appCompatActivity;
    private Fragment mFragment;
    private String message;

    public LoadDialog(AppCompatActivity activity) {
        super(activity);
        loadDialog = this;
        appCompatActivity = activity;
    }

    public LoadDialog(Fragment fragment) {
        super(fragment);
        loadDialog = this;
        mFragment = fragment;
    }

    /**
     * 获取单例
     *
     * @param compatActivity 兼容Activity
     * @return LoadDialog
     */
    public static LoadDialog getInstance(AppCompatActivity compatActivity) {
        if (loadDialog != null) {
            return loadDialog;
        } else {
            new LoadDialog(compatActivity);
        }
        return loadDialog;
    }

    /**
     * 获取单例
     *
     * @param fragment 兼容Activity
     * @return LoadDialog
     */
    public static LoadDialog getInstance(Fragment fragment) {
        if (loadDialog != null) {
            return loadDialog;
        } else {
            new LoadDialog(fragment);
        }
        return loadDialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setOnClickListener(binding.closeImageButton);
        binding.massage.setText(message);
        binding.massage.setVisibility(message == null || message.isEmpty() ? View.GONE : View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == binding.closeImageButton) {
            dismissedDialog();
        }
    }

    public void showDialog() {
        if (appCompatActivity != null) {
            show(appCompatActivity.getSupportFragmentManager(), "loadDialog");
        } else if (mFragment != null) {
            show(mFragment.getChildFragmentManager(), "loadDialog");
        }
    }

    public void showDialog(String message) {
        this.message = message == null ? "" : message;
        if (appCompatActivity != null) {
            show(appCompatActivity.getSupportFragmentManager(), "loadDialog");
        } else if (mFragment != null) {
            show(mFragment.getChildFragmentManager(), "loadDialog");
        }
    }

    public void dismissedDialog() {
        dismiss();
    }

    @Override
    protected boolean setCancel() {
        return true;
    }

    @Override
    protected void onDialogDismissed() {
        super.onDialogDismissed();
        loadDialog = null;
        appCompatActivity = null;
        mFragment = null;
    }

}
