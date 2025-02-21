package com.zpj.shouji.market.ui.multidata;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.ImageViewDrawableTarget;
import com.zpj.shouji.market.ui.fragment.dialog.CommonImageViewerDialogFragment;
import com.zpj.utils.ScreenUtils;

import java.util.List;

public class ScreenShootMultiData extends RecyclerMultiData<String> {

    private final List<String> urls;

    public ScreenShootMultiData(String title, List<String> urls) {
        super(title, urls);
//        this.urls = urls;
//        list.addAll(urls);
//        hasMore = false;

        this.urls = urls;
    }

    @Override
    public boolean loadData() {
//        list.addAll(urls);
//        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public int getItemRes() {
        return R.layout.item_screen_shoot;
    }

    @Override
    public void buildRecyclerView(EasyRecyclerView<String> recyclerView) {
        Context context = recyclerView.getRecyclerView().getContext();
        float screenHeight = ScreenUtils.getScreenHeight(context);
        float screenWidth = ScreenUtils.getScreenWidth(context);
        float ratio = screenHeight / screenWidth;

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView
                .setLayoutManager(layoutManager)
                .addItemDecoration(new DividerItemDecoration(context, urls.size()))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    View itemView = holder.getItemView();
                    ImageView ivImg = holder.getView(R.id.iv_img);
                    ivImg.setTag(position);
                    Glide.with(ivImg)
                            .load(list.get(position))
                            .apply(GlideRequestOptions.getImageOption())
                            .into(new ImageViewDrawableTarget(ivImg) {

                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    int width = resource.getIntrinsicWidth();
                                    int height = resource.getIntrinsicHeight();

                                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                                    params.height = (int) (screenWidth / 2f);

                                    if (width > height) {
                                        params.width = (int) (params.height * ratio);
                                    } else {
                                        params.width = (int) (params.height / ratio);
                                    }
                                    super.onResourceReady(resource, transition);
                                }

                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {
                                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                                    params.height = (int) (screenWidth / 2f);
                                    params.width = (int) (params.height / ratio);
                                    super.onLoadStarted(placeholder);
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                                    params.height = (int) (screenWidth / 2f);
                                    params.width = (int) (params.height / ratio);
                                    super.onLoadFailed(errorDrawable);
                                }
                            });

                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageViewer(ivImg, position);
                        }
                    });
                });
    }

    @Override
    protected boolean showMoreButton() {
        return false;
    }

    private void showImageViewer(ImageView ivImg, int position) {
        new CommonImageViewerDialogFragment()
                .setImageUrls(list)
                .setSrcView(ivImg, position)
                .setSrcViewUpdateListener(new ImageViewerDialogFragment.OnSrcViewUpdateListener<String>() {
                    private boolean flag = true;
                    @Override
                    public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<String> popup, int position) {
                        if (flag) {
                            flag = false;
                        } else {
                            recyclerView.getRecyclerView().scrollToPosition(position);
                        }
                        Log.d("updateImageView", "updateImageViewupdateImageView");

                        ImageView imageView = recyclerView.getRecyclerView().findViewWithTag(position);
                        if (imageView == null) {
                            imageView = ivImg;
                        }
                        popup.updateSrcView(imageView);
//                        if (!flag) {
//                            RxHandler.post(() -> {
//                                ImageView imageView2 = recyclerView.getRecyclerView().findViewWithTag(position);
//                                if (imageView2 == null) {
//                                    imageView2 = ivImg;
//                                }
//                                popup.updateSrcView(imageView2);
//                            }, 150);
//                        }
                    }
                })
//                .setImageList(list)
//                .setNowIndex(position)
//                .setSourceImageView(new SourceImageViewGet<String>() {
//                    private boolean flag = true;
//                    @Override
//                    public void updateImageView(ImageItemView<String> imageItemView, int pos, boolean isCurrent) {
//                        if (flag) {
//                            flag = false;
//                        } else if (isCurrent){
//                            recyclerView.getRecyclerView().scrollToPosition(pos);
//                        }
//                        Log.d("updateImageView", "updateImageViewupdateImageView");
//
//                        ImageView imageView = recyclerView.getRecyclerView().findViewWithTag(pos);
//                        if (imageView == null) {
//                            imageView = ivImg;
//                        }
//                        imageItemView.update(imageView);
//                        if (!flag) {
//                            RxHandler.post(() -> {
//                                ImageView imageView2 = recyclerView.getRecyclerView().findViewWithTag(pos);
//                                if (imageView2 == null) {
//                                    imageView2 = ivImg;
//                                }
//                                imageItemView.update(imageView2);
//                            }, 150);
//                        }
//                    }
//                })
                .show(ivImg.getContext());
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
            // Color.WHITE
            int color = Color.TRANSPARENT;
            if (itemPosition == 0) {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, color, 14, 0, 0)
                        .setRightSideLine(true, color, 2, 0, 0);
            } else if (itemPosition == total - 1) {
                builder = new Y_DividerBuilder()
                        .setRightSideLine(true, color, 14, 0, 0)
                        .setLeftSideLine(true, color, 2, 0, 0);
            } else {
                builder = new Y_DividerBuilder()
                        .setLeftSideLine(true, color, 2, 0, 0)
                        .setRightSideLine(true, color, 2, 0, 0);
            }
            return builder.setTopSideLine(true, color, 2, 0, 0)
                    .setBottomSideLine(true, color, 2, 0, 0)
                    .create();
        }
    }

}
