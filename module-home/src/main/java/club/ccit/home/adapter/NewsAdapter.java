package club.ccit.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import club.ccit.basic.BasicAdapter;
import club.ccit.home.databinding.ItemNewsBinding;
import club.ccit.home.ui.WebViewActivity;
import club.ccit.network.news.model.NewsListResultData;

public class NewsAdapter extends BasicAdapter<ItemNewsBinding, NewsListResultData> {
    private Context context;
    private int pageSize = 10;

    public NewsAdapter(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    protected void onBindData(DataViewHolder holder, NewsListResultData item) {
        // 绑定数据到视图
        holder.binding.textNewsTitle.setText(item.getTitle());
        holder.binding.textNewsCategory.setText(item.getCategory());
        holder.binding.textNewsDate.setText(item.getDate());
        holder.binding.textNewsAuthor.setText(item.getAuthor_name());

        holder.binding.imageNewsThumbnail1.setVisibility(item.getThumbnail_pic_s().isEmpty() ? View.GONE : View.VISIBLE);
        holder.binding.imageNewsThumbnail2.setVisibility(item.getThumbnail_pic_s02().isEmpty() ? View.GONE : View.VISIBLE);
        holder.binding.imageNewsThumbnail3.setVisibility(item.getThumbnail_pic_s03().isEmpty() ? View.GONE : View.VISIBLE);
        // 加载图片示例（使用Glide）
        if (!item.getThumbnail_pic_s().isEmpty()) {
            Glide.with(context)
                    .load(item.getThumbnail_pic_s())
                    .into(holder.binding.imageNewsThumbnail1);
        }
        if (!item.getThumbnail_pic_s02().isEmpty()) {
            Glide.with(context)
                    .load(item.getThumbnail_pic_s02())
                    .into(holder.binding.imageNewsThumbnail2);
        }
        if (!item.getThumbnail_pic_s03().isEmpty()) {
            Glide.with(context)
                    .load(item.getThumbnail_pic_s03())
                    .into(holder.binding.imageNewsThumbnail3);
        }

        holder.binding.detailNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", item.getUrl());
                context.startActivity(intent);
            }
        });

    }


    @Override
    protected ItemNewsBinding getViewBinding(ViewGroup parent) {
        // 创建ViewBinding实例
        context = parent.getContext();
        return ItemNewsBinding.inflate(LayoutInflater.from(parent.getContext()));
    }

    @Override
    protected int getPageSize() {
        return pageSize;
    }
}
