package com.zpj.shouji.market.ui.fragment.detail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppDetailInfo;
import com.zpj.shouji.market.ui.widget.popup.ImageViewer;
import com.zpj.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AppInfoFragment extends BaseFragment
        implements IEasy.OnBindViewHolderListener<String> {

    private EasyRecyclerView<String> recyclerView;
    private final List<String> imgUrlList = new ArrayList<>();
    private LinearLayout content;

    private float screenWidth;
    private float screenHeight;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_detail_info;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSupportInvisible() {
        getSupportDelegate().onSupportInvisible();
    }

    @Override
    public void onSupportVisible() {
        getSupportDelegate().onSupportVisible();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        this.screenHeight = ScreenUtils.getScreenHeight(context);
        this.screenWidth = ScreenUtils.getScreenWidth(context);
        content = view.findViewById(R.id.content);
        recyclerView = new EasyRecyclerView<>(view.findViewById(R.id.recycler_view));
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setItemViewCacheSize(100);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<String> list, int position, List<Object> payloads) {
        ImageView img = holder.getView(R.id.iv_img);
        Glide.with(context)
                .load(list.get(position))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) img.getLayoutParams();
                        int margin = ScreenUtils.dp2pxInt(context, 4);
//                        if (holder.getRealPosition() == 0) {
//                            params.leftMargin = margin * 5;
//                        } else {
//                            params.leftMargin = margin;
//                        }
//
//                        if (holder.getRealPosition() == list.size()) {
//                            params.rightMargin = margin * 5;
//                        } else {
//                            params.rightMargin = margin;
//                        }


                        params.height = (int) (screenHeight / 3);
                        if (width > height) {
                            params.width = (int) (screenWidth - margin * 10f);
//                            params.width = (int) ((screenHeight / screenWidth) * screenHeight / 3);
//                            params = new RecyclerView.LayoutParams((int) ((screenHeight / screenWidth) * screenHeight / 4), (int) (screenHeight / 4));
                        } else {
                            params.width = (int) (screenWidth / 3);

//                            params = new RecyclerView.LayoutParams((int) (screenWidth / 4), (int) (screenHeight / 4));
                        }
//                        img.setLayoutParams(params);
                        img.setImageDrawable(resource);
                    }
                });
        img.setTag(position);
        img.setOnClickListener(v -> {
            ImageViewer.with(context)
                    .setImageList(list)
                    .setNowIndex( (int)v.getTag())
                    .setSourceImageView(pos -> {
                        int layoutPos = recyclerView.getRecyclerView().indexOfChild(holder.getItemView());
                        View view = recyclerView.getRecyclerView().getChildAt(layoutPos + pos - position);
                        ImageView imageView;
                        if (view != null) {
                            imageView = view.findViewById(R.id.iv_img);
                        } else {
                            imageView = img;
                        }
                        return imageView;
                    })
                    .show();
        });
    }

    @Subscribe
    public void onGetAppDetailInfo(AppDetailInfo info) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setItemRes(R.layout.item_image)
                .setData(info.getImgUrlList())
                .addItemDecoration(new DividerItemDecoration(context, info.getImgUrlList().size()))
                .setLayoutManager(layoutManager)
                .onBindViewHolder(this)
                .build();
        recyclerView.notifyDataSetChanged();
        addItem("应用简介", info.getAppIntroduceContent());
        addItem("新版特性", info.getUpdateContent());
        addItem("详细信息", info.getAppInfo());
        addItem("权限信息", info.getPermissionContent());
    }

    private void addItem(String title, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.item_app_info_text, null, false);
        content.addView(view);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        tvTitle.setText(title);
        tvContent.setText(text);
    }


    private static class DividerItemDecoration extends Y_DividerItemDecoration {

        private final int total;

        private DividerItemDecoration(Context context, int total) {
            super(context);
            this.total = total;
        }

        @Override
        public Y_Divider getDivider(int itemPosition) {
            Y_DividerBuilder builder = null;
            if (itemPosition == 0) {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, Color.WHITE, 20, 0, 0)
                        .setRightSideLine(true, Color.WHITE, 4, 0, 0);
            } else if (itemPosition == total - 1) {
                builder = new Y_DividerBuilder()
                        .setRightSideLine(true, Color.WHITE, 20, 0, 0)
                        .setLeftSideLine(true, Color.WHITE, 4, 0, 0);
            } else {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, Color.WHITE, 4, 0, 0)
                        .setRightSideLine(true, Color.WHITE, 4, 0, 0);
            }
            return builder.setTopSideLine(true, Color.WHITE, 4, 0, 0)
                    .setBottomSideLine(true, Color.WHITE, 4, 0, 0)
                    .create();
        }
    }

}
