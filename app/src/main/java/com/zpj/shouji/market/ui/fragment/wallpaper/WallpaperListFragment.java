package com.zpj.shouji.market.ui.fragment.wallpaper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.ImageViewDrawableTarget;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.adapter.FooterViewHolder;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.dialog.RecyclerPartShadowDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.WallpaperViewerDialogFragment;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.ExpandIcon;
import com.zpj.shouji.market.ui.widget.count.IconCountView;
import com.zpj.shouji.market.ui.widget.emoji.EmojiExpandableTextView;
import com.zpj.toast.ZToast;
import com.zpj.utils.NetUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class WallpaperListFragment extends NextUrlFragment<WallpaperInfo> {

    private static final String DEFAULT_URL = "/appv3/bizhi_list.jsp";

    private String id;
    private String tag;
    @IntRange(from = 0, to = 2)
    private int sortPosition = 0;
    private int width;

    public static WallpaperListFragment newInstance(WallpaperTag tag) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, tag.getId());
        args.putString(Keys.TAG, tag.getName());
        WallpaperListFragment fragment = new WallpaperListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultUrl = DEFAULT_URL;
        nextUrl = DEFAULT_URL;
        width = (ScreenUtils.getScreenWidth() - ScreenUtils.dp2pxInt(8) * 3) / 2;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        id = arguments.getString(Keys.ID, "1");
        tag = arguments.getString(Keys.TAG, "全部");
        initNextUrl();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_wallpaper;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<WallpaperInfo> recyclerLayout) {
        if (getHeaderLayout() > 0) {
            recyclerLayout.setHeaderView(getHeaderLayout(), holder -> holder.setOnItemClickListener((this::showSortPupWindow)));
        }
        recyclerLayout.setFooterViewBinder(new FooterViewHolder(true));
//        recyclerLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                if (layoutManager instanceof StaggeredGridLayoutManager) {
//                    ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
//                }
//            }
//        });
        int dp4 = ScreenUtils.dp2pxInt(context, 4);
        recyclerLayout.getRecyclerView().setPadding(dp4, 0, dp4, 0);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<WallpaperInfo> list, int position, List<Object> payloads) {
        WallpaperInfo info = list.get(position);
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);
        wallpaper.setTag(position);

        Log.d("WallpaperListFragment", "wallpaper.getWidth()=" + wallpaper.getWidth());
        ViewGroup.LayoutParams params = wallpaper.getLayoutParams();
        float p = Float.parseFloat(info.getHeight())  / Float.parseFloat(info.getWidth());
        if (p > 2.5f) {
            p = 2.5f;
        }
        params.width = width;
        params.height = (int) (p * width);
        Log.d("WallpaperListFragment", "width=" + params.width + " height=" + params.height);
        wallpaper.setLayoutParams(params);

        Glide.with(wallpaper)
                .load(list.get(position).getSpic())
                .apply(GlideRequestOptions.getImageOption())
                .into(new ImageViewDrawableTarget(wallpaper) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        Log.d("onResourceReady", "\n------------------------------------------");
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();

                        Log.d("onResourceReady", "info=" + info);
                        Log.d("onResourceReady", "width=" + width + " height=" + height);
                    }
                });

//        Glide.with(context)
//                .load(list.get(position).getSpic())
//                .apply(GlideRequestOptions.getImageOption())
//                .into(new ImageViewDrawableTarget(wallpaper) {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        if (position != (int) imageView.getTag()) {
//                            return;
//                        }
////                        super.onResourceReady(resource, transition);
//                        imageView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (position != (int) imageView.getTag()) {
//                                    return;
//                                }
//                                Log.d("onResourceReady", "\n------------------------------------------");
//                                int width = resource.getIntrinsicWidth();
//                                int height = resource.getIntrinsicHeight();
//
//                                Log.d("onResourceReady", "info=" + info);
//                                Log.d("onResourceReady", "width=" + width + " height=" + height);
//                                float p = (float) height / width;
//                                if (p > 2.5f) {
//                                    p = 2.5f;
//                                }
//                                height = (int) (imageView.getMeasuredWidth() * p);
//                                Log.d("onResourceReady", "height=" + height + " wallpaper.getMeasuredWidth()=" + imageView.getMeasuredWidth() + " wallpaper.getWidth()=" + imageView.getWidth());
//                                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//                                layoutParams.height = height;
//                                layoutParams.width = imageView.getMeasuredWidth();
//                                Log.d("onResourceReady", "------------------------------------------\n");
//                                imageView.setImageDrawable(resource);
//                            }
//                        });
//
//                    }
//                });

        ImageView ivIcon = holder.getView(R.id.iv_icon);
        TextView tvName = holder.getView(R.id.tv_name);
        View.OnClickListener listener = view -> ProfileFragment.start(info.getNickName());
        ivIcon.setOnClickListener(listener);
        tvName.setOnClickListener(listener);
        Glide.with(context).load(info.getMemberIcon())
                .apply(RequestOptions.bitmapTransform(new CircleWithBorderTransformation(0.5f, Color.LTGRAY)))
                .into(ivIcon);
        EmojiExpandableTextView tvContent = holder.getView(R.id.tv_content);
        tvContent.setContent(info.getContent());
        tvName.setText(info.getNickName());
        IconCountView countView = holder.getView(R.id.support_view);
        countView.setCount(info.getSupportCount());
        countView.setState(info.isLike());
        countView.setOnStateChangedListener(new IconCountView.OnSelectedStateChangedListener() {
            @Override
            public void select(boolean isSelected) {
                HttpApi.likeApi("wallpaper", info.getId())
                        .bindToLife(WallpaperListFragment.this)
                        .onSuccess(data -> {
                            String result = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
//                                ZToast.success(result);
                                info.setSupportCount(info.getSupportCount() + (isSelected ? 1 : -1));
                                info.setLike(isSelected);
                            } else {
                                ZToast.error(result);
                                countView.setState(!isSelected);
                            }
                        })
                        .onError(throwable -> {
                            ZToast.error("点赞失败！" + throwable.getMessage());
                            countView.setState(!isSelected);
                        })
                        .subscribe();
            }
        });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, WallpaperInfo data) {
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);

        List<String> objects = new ArrayList<>();
        objects.add(data.getSpic());
        List<String> original = new ArrayList<>();
        original.add(data.getPic());

        new WallpaperViewerDialogFragment()
                .setWallpaperInfo(data)
                .setOriginalImageList(original)
                .setImageUrls(AppConfig.isShowOriginalImage() && NetUtils.isWiFi(context) ? original : objects)
                .setSrcView(wallpaper, 0)
                .setSrcViewUpdateListener(new ImageViewerDialogFragment.OnSrcViewUpdateListener<String>() {
                    @Override
                    public void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<String> popup, int position) {
                        popup.updateSrcView(wallpaper);
                    }
                })
                .show(context);
    }

    @Override
    public WallpaperInfo createData(Element element) {
        if ("wallpaper".equals(element.selectFirst("type").text())) {
            return WallpaperInfo.create(element);
        }
        return null;
    }

    @Override
    public void onSuccess(Integer start) throws Exception {
//        Log.d("getData", "doc=" + doc);
//        nextUrl = doc.selectFirst("nextUrl").text();
//        if (refresh) {
//            data.clear();
//        }
//        int start = data.size();
//        for (Element element : doc.select("item")) {
//            WallpaperInfo item = createData(element);
//            if (item == null) {
//                continue;
//            }
//            data.add(item);
//        }
        int end = data.size();
        if (data.size() == 0) {
            recyclerLayout.showEmpty();
        } else {
            recyclerLayout.showContent();
        }
        if (start < end) {
            // 这里加1是因为我们给RecyclerView添加了一个header View
            recyclerLayout.notifyItemRangeChanged(start + 1, end - start);
        }
        refresh = false;
    }

    @Override
    public void onRefresh() {
//        data.clear();
        initNextUrl();
//        refresh = true;
//        recyclerLayout.notifyDataSetChanged();

        if (data.isEmpty()) {
//            refresh = false;
            recyclerLayout.showLoading();
//            recyclerLayout.showEmpty();
        } else {
//            refresh = true;
//            getData();
        }
        refresh = true;
        getData();
    }

    protected int getHeaderLayout() {
        return R.layout.item_header_wallpaper;
    }

    private void initNextUrl() {
        nextUrl = DEFAULT_URL;
        if (!TextUtils.isEmpty(tag) && !"全部".equals(tag)) {
            nextUrl = nextUrl + "?tag=" + tag;
        }
        nextUrl += nextUrl.equals(DEFAULT_URL) ? "?" : "&";
        if (sortPosition == 1) {
            nextUrl = nextUrl + "sort=time";
        } else if (sortPosition == 2) {
            nextUrl = nextUrl + "sort=user";
        }
    }

    private void showSortPupWindow(View v) {
        ExpandIcon expandIconView = v.findViewById(R.id.expand_icon);

        expandIconView.switchState();
        new RecyclerPartShadowDialogFragment()
                .addItems("默认排序", "时间排序", "人气排序")
                .setSelectedItem(sortPosition)
                .setOnItemClickListener((view, title, position) -> {
                    sortPosition = position;
                    TextView titleText = v.findViewById(R.id.tv_title);
                    titleText.setText(title);
                    onRefresh();
                })
                .setAttachView(v)
                .setOnDismissListener(expandIconView::switchState)
                .show(context);
    }
}
