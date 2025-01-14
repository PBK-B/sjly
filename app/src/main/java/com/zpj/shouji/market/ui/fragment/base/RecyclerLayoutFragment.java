package com.zpj.shouji.market.ui.fragment.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;

import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.IEasy;
import com.zpj.recyclerview.IEasy.OnBindViewHolderListener;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FooterViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerLayoutFragment<T> extends SkinFragment
        implements OnBindViewHolderListener<T>,
        IEasy.OnItemClickListener<T>,
        IEasy.OnItemLongClickListener<T>,
        IEasy.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        IEasy.OnLoadRetryListener {

    protected final List<T> data = new ArrayList<>();
    protected EasyRecyclerLayout<T> recyclerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_layout;
    }

    @LayoutRes
    protected abstract int getItemLayoutId();

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            handleArguments(getArguments());
        }
        recyclerLayout = findViewById(R.id.recycler_layout);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        recyclerLayout.setData(data)
//                .setFooterView(getFooterView(context))
                .setFooterViewBinder(new FooterViewHolder())
                .setItemRes(getItemLayoutId())
                .setLayoutManager(getLayoutManager(context))
                .setOnLoadRetryListener(this)
                .setEnableLoadMore(true)
                .setEnableSwipeRefresh(true)
                .setOnRefreshListener(this)
                .onBindViewHolder(this)
                .onItemClick(this)
                .onItemLongClick(this)
                .onLoadMore(this);
        buildRecyclerLayout(recyclerLayout);
        recyclerLayout.build();
    }

    @Override
    public void onRefresh() {
        data.clear();
        recyclerLayout.notifyDataSetChanged();
    }

    @Override
    public void onLoadRetry() {
        onRefresh();
    }

    protected void handleArguments(Bundle arguments) {

    }

    protected void buildRecyclerLayout(EasyRecyclerLayout<T> recyclerLayout) {

    }

//    protected View getFooterView(Context context) {
//        return LayoutInflater.from(context).inflate(R.layout.item_footer_normal, null, false);
//    }

    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

}
