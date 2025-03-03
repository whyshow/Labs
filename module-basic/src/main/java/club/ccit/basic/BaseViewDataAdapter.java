package club.ccit.basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import club.ccit.basic.R;

import club.ccit.basic.action.AdapterAction;
import club.ccit.basic.action.ToastWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: BaseAdapter
 *
 * @author: 张帅威
 * Date: 2022/1/17 9:12 上午
 * Description:
 * Version: 1.0 版本 使用viewBinding绑定控件，自动增加footer,显示加载中、没有更多了、重新加载和失败文字提示灯
 */
public abstract class BaseViewDataAdapter<T extends ViewBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ToastWidget {
    public List list;
    private static final int TYPE_DATA = 1;
    private static final int TYPE_FOOTER = 2;
    private Integer FOOTER_TYPE = 0;
    public static Integer FOOTER_LOADING = 0;
    public static Integer FOOTER_NO_DATA = 1;
    public static Integer FOOTER_RETRY = 2;
    public AdapterAction action;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DATA) {
            return new ViewHolder(getViewBinding(parent));
        } else {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_state, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (list.get(list.size() - 1) instanceof Integer) {

        }else {
            list.add(FOOTER_TYPE);
        }
        if (getItemViewType(position) == TYPE_DATA) {
            onBindingViewData((ViewHolder) holder, position);
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.progressBar.setVisibility(View.GONE);
            footerViewHolder.noData.setVisibility(View.GONE);
            footerViewHolder.errorMsg.setVisibility(View.GONE);
            footerViewHolder.retryButton.setVisibility(View.GONE);
            switch (FOOTER_TYPE) {
                case 0:
                    footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                    action.onNext();
                    break;
                case 1:
                    footerViewHolder.noData.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    footerViewHolder.retryButton.setVisibility(View.VISIBLE);
                    footerViewHolder.retryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            action.onRefresh();
                            footerViewHolder.retryButton.setVisibility(View.GONE);
                            footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 添加item数据
     *
     * @param list 已经添加过数据列表
     * @param page
     */
    public void putData(List list, int page) {
        if (list != null && list.size() > 0) {
            if (page >= 0) {
                if (this.list == null) {
                    this.list = new ArrayList();
                }
                for (int i = 0; i < list.size(); i++) {
                    this.list.add(this.list.size() == 0 ? 0 : this.list.size() - 1, list.get(i));
                }
                notifyItemRangeInserted(this.list.size() - list.size(), list.size());
                FOOTER_TYPE = FOOTER_LOADING;
                notifyDataSetChanged();
            } else {
                onRefresh(list);
            }
        } else {
            setError(BaseViewDataAdapter.FOOTER_NO_DATA);
        }
    }

    public void setNoneData() {
        FOOTER_TYPE = FOOTER_NO_DATA;
        if (this.list == null) {
            initList();
        }
        list.clear();
        list.add(FOOTER_TYPE);
        notifyDataSetChanged();
    }

    /**
     * 重新加载
     */
    public void onRefresh(List list) {
        if (this.list == null) {
            initList();
        }
        this.list.clear();
        this.list.addAll(list);
        this.list.add(FOOTER_TYPE);
        FOOTER_TYPE = FOOTER_LOADING;
        notifyDataSetChanged();
    }

    /**
     * 错误状态
     */
    public void setError(int type) {
        FOOTER_TYPE = type;
        if (list == null) {
            initList();
        }
        // 判断最后一位是否是FOOTER
        if (list.get(list.size() - 1) instanceof Integer) {
            list.set(list.size() - 1, type);
        }
        notifyDataSetChanged();
    }

    private void initList() {
        if (list == null) {
            list = new ArrayList();
            list.add(FOOTER_TYPE);
        }
    }

    /**
     * 获取item 数量
     *
     * @return 返回item总数
     */
    @Override
    public int getItemCount() {
        if (list == null) {
            initList();
        }
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取item类型
     *
     * @param position item 游标
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Integer) {
            return TYPE_FOOTER;
        } else {
            return TYPE_DATA;
        }
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position 绑定
     */
    protected abstract void onBindingViewData(ViewHolder holder, int position);

    /**
     * 绑定视图
     * .inflate(LayoutInflater.from(parent.getContext()))
     *
     * @param parent。
     * @return 实现
     */
    protected abstract T getViewBinding(ViewGroup parent);

    /**
     * 绑定控件
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public T itemBinding;

        public ViewHolder(@NonNull T itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }
    }

    /**
     * 绑定 Footer控件id
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout noData, progressBar;
        TextView errorMsg, retryButton;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            noData = itemView.findViewById(R.id.noData);
            progressBar = itemView.findViewById(R.id.progressBar);
            errorMsg = itemView.findViewById(R.id.errorMsg);
            retryButton = itemView.findViewById(R.id.retryButton);
        }
    }
}


