package com.zpj.shouji.market.ui.fragment.backup;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.CloudBackupApi;
import com.zpj.shouji.market.model.CloudBackupItem;
import com.zpj.shouji.market.ui.fragment.base.StateSwipeBackFragment;
import com.zpj.shouji.market.utils.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CloudBackupFragment extends StateSwipeBackFragment
        implements IEasy.OnBindViewHolderListener<CloudBackupItem>,
        IEasy.OnItemClickListener<CloudBackupItem> {

    private final List<CloudBackupItem> backupItemList = new ArrayList<>();

    private ImageButton btnAdd;

    private TextView tvInfo;

    private EasyRecyclerView<CloudBackupItem> recyclerView;

    public static void start() {
        start(new CloudBackupFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_backup_cloud;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.onRefreshEvent(this, s -> getData());
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        super.toolbarRightImageButton(imageButton);
        btnAdd = imageButton;
        btnAdd.setVisibility(View.INVISIBLE);
        btnAdd.setOnClickListener(v -> CreateBackupFragment.start());
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        tvInfo = findViewById(R.id.tv_info);

        TextView tvCreate = findViewById(R.id.tv_create);
        tvCreate.setOnClickListener(v -> CreateBackupFragment.start());

        View header = findViewById(R.id.ll_header);

        View shadowView = findViewById(R.id.shadow_view);
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {
            float alpha = (float) Math.abs(i) / appBarLayout1.getTotalScrollRange();
            alpha = Math.min(1f, alpha);
            if (btnAdd != null) {
                btnAdd.setVisibility(alpha > 0 ? View.VISIBLE : View.INVISIBLE);
                btnAdd.setAlpha(alpha);
            }
            header.setAlpha(1 - alpha);
            header.setVisibility(alpha >= 1 ? View.INVISIBLE : View.VISIBLE);
            shadowView.setAlpha(alpha >= 1 ? 1f : 0);
        });

        recyclerView = new EasyRecyclerView<>(findViewById(R.id.recycler_view));
        recyclerView.setData(backupItemList)
                .setItemRes(R.layout.item_cloud_backup)
                .onBindViewHolder(this)
                .onItemClick(this)
                .build();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getData();
    }

    @Override
    protected void onRetry() {
        super.onRetry();
        getData();
    }

    @Override
    protected void initStatusBar() {
        if (isLazyInit()) {
            lightStatusBar();
        } else {
            super.initStatusBar();
        }
    }

    private void getData() {
        CloudBackupApi.backupListApi()
                .bindToLife(this)
                .onSuccess(data -> {
                    Log.d("CloudBackupFragment", "data=" + data);
                    backupItemList.clear();
                    for (Element item : data.select("item")) {
                        String viewType = item.selectFirst("viewtype").text();
                        if ("beifentitle".equals(viewType)) {
                            tvInfo.setText(item.selectFirst("createdate").text());
                        } else if ("beifen".equals(viewType)) {
                            backupItemList.add(CloudBackupItem.from(item));
                        }
                    }
                    if (TextUtils.isEmpty(tvInfo.getText()) && backupItemList.size() == 1) {
                        tvInfo.setText(backupItemList.get(0).getCreateDate());
                    }
                    postOnEnterAnimationEnd(() -> {
                        lightStatusBar();
                        recyclerView.notifyDataSetChanged();
                        showContent();
                    });
                })
                .onError(throwable -> {
                    showError(throwable.getMessage());
                })
                .subscribe();
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<CloudBackupItem> list, int position, List<Object> payloads) {
        CloudBackupItem item = list.get(position);
        holder.setText(R.id.tv_title, item.getTitle());
        holder.setText(R.id.tv_info, item.getComment());
        holder.setText(R.id.tv_time, "共" + item.getCount() + "个应用 | " + item.getCreateDate());
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, CloudBackupItem data) {
        BackupDetailFragment.start(data.getId());
    }

}
