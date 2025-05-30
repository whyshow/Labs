package club.ccit.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import club.ccit.home.module.NewsModuleFragment;

/**
 * FileName: NewsFragmentCollectionAdapter
 * Version:
 */
public class NewsFragmentCollectionAdapter extends FragmentStateAdapter {
    private int size;
    private String[] titles;

    public NewsFragmentCollectionAdapter(@NonNull FragmentActivity fragmentActivity, String[] title) {
        super(fragmentActivity);
        size = title.length;
        titles = title;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new NewsModuleFragment(titles[position]);
    }

    @Override
    public int getItemCount() {
        return size;
    }

}
