package club.ccit.home.fragment;

import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;

import club.ccit.basic.BaseViewDataFragment;
import club.ccit.home.adapter.NewsFragmentCollectionAdapter;
import club.ccit.home.databinding.FragmentNewsBinding;
import club.ccit.network.news.model.NewsType;

public class NewsFragment extends BaseViewDataFragment<FragmentNewsBinding> {
    private NewsFragmentCollectionAdapter fragmentCollectionAdapter;
    private TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate() {
        super.onCreate();
        initViewPager();
    }

    private void initViewPager() {
//        String[] title = new String[]{NewsType.TOP.getDesc(), NewsType.DOMESTIC.getDesc(), NewsType.INTERNATIONAL.getDesc(), NewsType.ENTERTAINMENT.getDesc(),
//                NewsType.SPORTS.getDesc(), NewsType.MILITARY.getDesc(), NewsType.TECHNOLOGY.getDesc(), NewsType.FINANCE.getDesc(),
//                NewsType.GAMES.getDesc(), NewsType.AUTO.getDesc(), NewsType.HEALTH.getDesc()};
        String[] title = new String[]{NewsType.TOP.getDesc()};
        fragmentCollectionAdapter = new NewsFragmentCollectionAdapter(requireActivity(), title);
        binding.viewPager2.setAdapter(fragmentCollectionAdapter);
        binding.viewPager2.setOffscreenPageLimit(title.length);
        tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> tab.setText(title[position]));
        tabLayoutMediator.attach();

        // 去除长按toast
        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            if (tab != null) {
                tab.view.setLongClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tab.view.setTooltipText(null);
                }
            }
        }

        reduceDragSensitivity(binding.viewPager2);
    }

    /**
     * 减少ViewPager2的滑动灵敏度
     *
     * @param viewPager2 ViewPager2实例
     */
    private void reduceDragSensitivity(ViewPager2 viewPager2) {
        try {
            Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(viewPager2);
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 4);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
