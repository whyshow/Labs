package club.ccit.home.module;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import club.ccit.basic.BaseViewDataFragment;
import club.ccit.basic.BasicAdapter;
import club.ccit.home.adapter.NewsAdapter;
import club.ccit.home.data.MainData;
import club.ccit.home.data.NewsModuleData;
import club.ccit.home.databinding.FragmentNewsModuleBinding;
import club.ccit.network.news.model.NewsListRequestModel;
import club.ccit.network.news.model.NewsListResultData;
import club.ccit.network.news.model.NewsType;

// 新闻模块fragment
public class NewsModuleFragment extends BaseViewDataFragment<FragmentNewsModuleBinding> {
    private MainData mainData;// 持久新闻模块数据
    private NewsModuleData newsModuleData;// 新闻模块数据
    private String type = NewsType.TOP.getValue();// 新闻类型
    private NewsAdapter newsAdapter;
    private int pageSize = 10;
    private NewsListRequestModel newsListRequestModel;

    public NewsModuleFragment(String type) {
        super();
        // 设置新闻类型
        this.type = NewsType.getValueByDesc(type);

    }

    @Override
    protected void onCreate() {
        super.onCreate();
        // getActivity() 以父Activity绑定观察者，fragment关闭数据不消失，以this绑定观察者，fragment关闭数据消失
        mainData = new ViewModelProvider(requireActivity()).get(MainData.class);
        newsModuleData = new ViewModelProvider(this).get(NewsModuleData.class);
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright));
        // 设置观察者
        setObserver();
        newsModuleData.seSafetActivity((AppCompatActivity) requireActivity());
        newsAdapter = new NewsAdapter(pageSize);
        newsAdapter.setLoadMoreListener(new BasicAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore(int nextPage) {
                setRequestModel(nextPage);
            }

            @Override
            public void onRetry(int page) {
                // 点击重试
                setRequestModel(page);
            }
        });

        binding.newsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), GridLayoutManager.VERTICAL));
        binding.newsRecyclerView.setAdapter(newsAdapter);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // 刷新
            setRequestModel(1);
        });
    }

    private void setRequestModel(int page) {
        newsListRequestModel = new NewsListRequestModel();
        // 设置新闻列表请求参数
        newsListRequestModel.setPage(page);
        newsListRequestModel.setType(type);
        newsListRequestModel.setPage_size(pageSize);
        newsModuleData.getNewsList(newsListRequestModel);
    }

    private void setObserver() {

        newsModuleData.ok.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
        // 设置新闻列表数据观察者
        newsModuleData.newsListLiveData.observe(this, new Observer<List<NewsListResultData>>() {
            @Override
            public void onChanged(List<NewsListResultData> newsListResultData) {
                // 更新新闻列表适配器
                binding.newsRecyclerView.post(() -> {
                    // 更新新闻列表适配器
                    newsAdapter.appendData(newsListResultData);
                });
                // 将数据保存持久化中
                String category = newsListResultData.get(0).getCategory();
                Map<String, List<NewsListResultData>> currentData = mainData.saveNewsListResultData.getValue() == null ?
                        new HashMap<>() : mainData.saveNewsListResultData.getValue();
                List<NewsListResultData> listMap = currentData.get(category);
                if (listMap != null) {
                    // 合并新旧数据，去重处理
                    Set<String> existingKeys = new HashSet<>();
                    for (NewsListResultData item : listMap) {
                        existingKeys.add(item.getUniquekey());
                    }
                    List<NewsListResultData> newItems = new ArrayList<>();
                    for (NewsListResultData item : newsListResultData) {
                        if (!existingKeys.contains(item.getUniquekey())) {
                            newItems.add(item);
                        }
                    }
                    if (!newItems.isEmpty()) {
                        listMap.addAll(newItems);
                        currentData.put(category, listMap);
                        mainData.saveNewsListResultData.setValue(currentData);
                    }
                } else {
                    // 如果不存在则创建新列表
                    currentData.put(category, new ArrayList<>(newsListResultData));
                    mainData.saveNewsListResultData.setValue(currentData);
                }
            }
        });

        mainData.saveNewsListResultData.observe(this, new Observer<Map<String, List<NewsListResultData>>>() {
            @Override
            public void onChanged(Map<String, List<NewsListResultData>> stringListMap) {
                Log.d("MainData", "onChanged: " + stringListMap);
            }
        });
    }
}
