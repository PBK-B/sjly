package com.zpj.shouji.market.ui.fragment.dialog;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.ZDialog;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.shouji.market.utils.ProgressViewHolder;
import com.zpj.widget.toolbar.ZToolBar;

import java.util.List;

public class CommonImageViewerDialogFragment extends ImageViewerDialogFragment<String> {

    private List<String> originalImageList;
    private List<String> imageSizeList;

    private ZToolBar titleBar;
    protected TextView tvInfo;
    protected TextView tvIndicator;
    private ImageButton btnMore;

//    private ISupportFragment preFragment;

    public CommonImageViewerDialogFragment() {
        setProgressViewHolder(new ProgressViewHolder());
    }

    @Override
    protected int getCustomLayoutId() {
        return R.layout.dialog_fragment_theme_image_viewer;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
//        mSupportVisibleActionQueue.start();
//        mDelegate.onSupportVisible();
        lightStatusBar();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
//        preFragment = getPreFragment();

        titleBar = findViewById(R.id.tool_bar);
        tvIndicator = findViewById(R.id.tv_indicator);
        tvInfo = findViewById(R.id.tv_info);
        btnMore = findViewById(R.id.btn_more);
//        loadingView = findViewById(R.id.lv_loading);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTitle();
                tvIndicator.setText(urls.size() + "/" + (position + 1));
                setInfoText();
//                loadingView.setVisibility(View.GONE);
            }
        });

        btnMore.setOnClickListener(v -> {
            ZDialog.attach()
                    .addItems("分享图片", "保存图片", "设为壁纸")
                    .addItemIf(isOriginalImageAvailable(), "查看原图")
                    .setOnSelectListener((fragment, pos, text) -> {
                        switch (pos) {
                            case 0:
                                PictureUtil.shareWebImage(context, getOriginalImageUrl());
                                break;
                            case 1:
                                PictureUtil.saveImage(context, urls.get(position));
                                break;
                            case 2:
                                PictureUtil.setWallpaper(context, getOriginalImageUrl());
                                break;
                            case 3:
                                showOriginalImage();
                                break;
                        }
                        fragment.dismiss();
                    })
                    .setAttachView(btnMore)
                    .show(context);
        });

        updateTitle();
        titleBar.getLeftImageButton().setOnClickListener(v -> dismiss());
        titleBar.getCenterTextView().setShadowLayer(8, 4, 4, Color.BLACK);


        tvIndicator.setText(urls.size() + "/" + (position + 1));

        setInfoText();

    }

//    @Override
//    public void onDestroy() {
//        Log.d("CommonImageViewer", "preFragment=" + preFragment + " topFragment=" + getTopFragment());
//        if (preFragment != null && preFragment == getTopFragment()) {
//            preFragment.onSupportVisible();
//        }
//        preFragment = null;
//        super.onDestroy();
//    }

    private String getOriginalImageUrl() {
        String url;
        if (originalImageList != null) {
            url = originalImageList.get(position);
        } else {
            url = urls.get(position);
        }
        return url;
    }

    private void updateTitle() {
        String url = urls.get(position);
        titleBar.setCenterText(url.substring(url.lastIndexOf("/") + 1));
    }

    public CommonImageViewerDialogFragment setOriginalImageList(List<String> originalImageList) {
        this.originalImageList = originalImageList;
        return this;
    }

    public CommonImageViewerDialogFragment setImageSizeList(List<String> imageSizeList) {
        this.imageSizeList = imageSizeList;
        return this;
    }

    private void setInfoText() {
        if (imageSizeList != null) {
            if (isOriginalImageAvailable()) {
                tvInfo.setText(String.format("查看原图(%s)", imageSizeList.get(position)));
                tvInfo.setOnClickListener(v -> showOriginalImage());
            } else {
                tvInfo.setText(imageSizeList.get(position));
                tvInfo.setOnClickListener(null);
            }
        } else {
            tvInfo.setVisibility(View.GONE);
        }
    }


    private boolean isOriginalImageAvailable() {
        return originalImageList != null && !TextUtils.equals(urls.get(position), originalImageList.get(position));
    }

    private void showOriginalImage() {
        urls.set(position, originalImageList.get(position));
        loadNewUrl(position, originalImageList.get(position));
        updateTitle();
        setInfoText();
    }

}
