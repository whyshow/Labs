package club.ccit.view.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import club.ccit.view.databinding.DialogLoadBinding;

import club.ccit.view.dialog.basic.BaseDialogFragment;


public class LoadDialog extends BaseDialogFragment<DialogLoadBinding> {
    private static LoadDialog loadDialog;

    public LoadDialog(AppCompatActivity activity) {
        super(activity);
        loadDialog = this;
    }

    public LoadDialog(Fragment fragment) {
        super(fragment);
        loadDialog = this;
    }

    /**
     * 获取单例
     *
     * @param compatActivity 兼容Activity
     * @return
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
     * @return
     */
    public static LoadDialog getInstance(Fragment fragment) {
        if (loadDialog != null) {
            return loadDialog;
        } else {
            new LoadDialog(fragment);
        }
        return loadDialog;
    }

    public void showDialog() {
        if (appCompatActivity != null) {
            show(appCompatActivity.getSupportFragmentManager(), "loadDialog");
        } else if (mFragment != null) {
            show(mFragment.getChildFragmentManager(), "loadDialog");
        }
    }

    @Override
    protected boolean setCancel() {
        return true;
    }

    @Override
    protected void onDialogDismiss() {
        super.onDialogDismiss();
        loadDialog = null;
    }
}
