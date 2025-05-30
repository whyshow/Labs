package club.ccit.home.ui;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import club.ccit.basic.BaseViewDataActivity;
import club.ccit.home.R;
import club.ccit.home.databinding.ActivityMainBinding;

// 首页
public class MainActivity extends BaseViewDataActivity<ActivityMainBinding> {
    @Override
    protected void onCreate() {
        super.onCreate();
        new AppBarConfiguration.Builder(
                R.id.navigation_news, R.id.navigation_iot, R.id.navigation_me)
                .build();
        binding.navView.setItemIconTintList(null);

        cleanToast();
        NavController navController = Navigation.findNavController(this, R.id.navFragmentActivityMain);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

    }

    /**
     * 去除item长按显示 toast
     */
    private void cleanToast() {
        View bottomBarView = binding.navView.getChildAt(0);
        bottomBarView.findViewById(R.id.navigation_news).setOnLongClickListener(v -> true);
        bottomBarView.findViewById(R.id.navigation_iot).setOnLongClickListener(v -> true);
        bottomBarView.findViewById(R.id.navigation_me).setOnLongClickListener(v -> true);
    }

}
