package club.ccit.basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用RecyclerView适配器基类，支持自动分页加载和多种底部状态显示
 *
 * @param <T> ViewBinding类型，用于数据项布局绑定
 * @param <D> 数据类型，适配器承载的数据对象类型
 */
public abstract class BasicAdapter<T extends ViewBinding, D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected T binding;
    // 视图类型常量
    private static final int TYPE_ITEM = 1;   // 普通数据项类型
    private static final int TYPE_FOOTER = 2; // 底部状态视图类型

    private int pageSize = 10; // 每页数据量，可配置
    // 加载状态常量
    public static final int STATE_LOADING = 0; // 加载中状态
    public static final int STATE_NO_MORE = 1; // 无更多数据状态
    public static final int STATE_ERROR = 2;   // 加载失败状态

    private List<D> dataList = new ArrayList<>(); // 数据集合
    private int currentState = STATE_LOADING;    // 当前底部状态
    private boolean isLoading = false;           // 加载锁，防止重复加载

    /**
     * 分页加载监听接口
     */
    public interface LoadMoreListener {
        void onLoadMore(int nextPage); // 触发加载下一页时调用

        void onRetry(int page);                // 点击重试按钮时调用
    }

    private LoadMoreListener loadMoreListener; // 分页加载监听实例

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据视图类型创建对应的ViewHolder
        if (viewType == TYPE_ITEM) {
            // 数据项ViewHolder，使用抽象方法获取绑定类实例
            return new DataViewHolder(getViewBinding(parent));
        } else {
            // 底部状态ViewHolder，加载预设布局文件
            View footer = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_load_more, parent, false);
            return new FooterViewHolder(footer);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            // 绑定数据到视图
            onBindData((DataViewHolder) holder, dataList.get(position));
            // 自动加载触发逻辑：当滚动到倒数第二项时开始加载
            if (position == getItemCount() - 2 && !isLoading) {
                startLoading();
            }
        } else {
            // 更新底部状态视图
            updateFooterUI((FooterViewHolder) holder);
            if (dataList.isEmpty()) {
                startLoading();
            }
        }
    }

    /**
     * 启动加载
     */
    public void startLoading() {
        if (loadMoreListener != null && !isLoading) {
            isLoading = true;
            currentState = STATE_LOADING;
            loadMoreListener.onLoadMore(dataList.size() / getPageSize() + 1); // 假设每页10条
        }
    }

    private void updateFooterUI(FooterViewHolder holder) {
        holder.loadingView.setVisibility(currentState == STATE_LOADING ? View.VISIBLE : View.GONE);
        holder.noMoreView.setVisibility(currentState == STATE_NO_MORE ? View.VISIBLE : View.GONE);
        holder.errorLayout.setVisibility(currentState == STATE_ERROR ? View.VISIBLE : View.GONE);

        holder.retryButton.setOnClickListener(v -> {
            if (loadMoreListener != null) {
                currentState = STATE_LOADING;
                notifyItemChanged(getItemCount() - 1);
                loadMoreListener.onRetry(dataList.size() / getPageSize() + 1);
            }
        });
    }

    /**
     * 追加新数据（用于分页加载）
     *
     * @param newData 新数据集合
     */
    public void appendData(List<D> newData) {
        if (newData == null || newData.isEmpty()) {
            currentState = STATE_NO_MORE;
            notifyItemChanged(getItemCount() - 1);
        } else {
            // 创建现有数据唯一标识集合
            Set<String> existingKeys = new HashSet<>();
            for (D item : dataList) {
                existingKeys.add(getUniqueId(item));
            }

            // 过滤重复数据
            List<D> filteredData = new ArrayList<>();
            for (D item : newData) {
                if (!existingKeys.contains(getUniqueId(item))) {
                    filteredData.add(item);
                }
            }

            boolean isFirstPage = dataList.isEmpty();
            int oldSize = dataList.size();
            dataList.addAll(filteredData);

            if (isFirstPage) {
                notifyDataSetChanged(); // 第一页使用全量刷新，保持在顶部
            } else {
                notifyItemRangeInserted(oldSize, filteredData.size());
            }

            isLoading = false;

            // 如果过滤后数据为空，显示无更多数据
            if (filteredData.isEmpty()) {
                currentState = STATE_NO_MORE;
                notifyItemChanged(getItemCount() - 1);
            }
        }
    }

    /**
     * 设置分页加载监听器
     *
     * @param listener 实现LoadMoreListener接口的实例
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    protected abstract String getUniqueId(D item);

    /**
     * 抽象方法：实现数据绑定逻辑
     *
     * @param holder ViewHolder实例
     * @param item   当前数据项
     */
    protected abstract void onBindData(DataViewHolder holder, D item);

    /**
     * 抽象方法：创建ViewBinding实例
     *
     * @param parent 父容器
     * @return ViewBinding实例
     */
    protected abstract T getViewBinding(ViewGroup parent);

    protected abstract int getPageSize();

    /**
     * 数据项ViewHolder，持有ViewBinding实例
     */
    public class DataViewHolder extends RecyclerView.ViewHolder {
        public final T binding; // 通过ViewBinding访问布局控件

        DataViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1; // 始终保留footer位置
    }

    // 修改视图类型判断逻辑
    @Override
    public int getItemViewType(int position) {
        return position >= dataList.size() ? TYPE_FOOTER : TYPE_ITEM;
    }

    /**
     * 底部状态ViewHolder，包含三种状态视图组件
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        // 状态视图组件
        View loadingView;   // 加载中进度条
        View noMoreView;    // 无更多数据提示
        View errorLayout;   // 错误提示布局
        Button retryButton; // 重试按钮

        FooterViewHolder(View itemView) {
            super(itemView);
            // 初始化视图组件引用
            loadingView = itemView.findViewById(R.id.progress_bar);
            noMoreView = itemView.findViewById(R.id.tv_no_more);
            errorLayout = itemView.findViewById(R.id.layout_error);
            retryButton = itemView.findViewById(R.id.btn_retry);
        }
    }
}