package com.zpj.shouji.market.ui.fragment.wallpaper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.felix.atoast.library.AToast;
import com.github.zagum.expandicon.ExpandIconView;
import com.sunbinqiang.iconcountview.IconCountView;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerLayout;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.event.GetMainFragmentEvent;
import com.zpj.shouji.market.event.StatusBarEvent;
import com.zpj.shouji.market.glide.MyRequestOptions;
import com.zpj.shouji.market.model.WallpaperInfo;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.widget.popup.RecyclerPopup;
import com.zpj.shouji.market.ui.widget.popup.WallpaperViewerPopup;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class WallpaperListFragment extends NextUrlFragment<WallpaperInfo> {

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/bizhi_list.jsp";

    private String id;
    private String tag;
    @IntRange(from = 0, to = 2)
    private int sortPosition = 0;

    private int halfScreenWidth;

    private RecyclerPopup recyclerPopup;

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
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    @Override
    protected void buildRecyclerLayout(EasyRecyclerLayout<WallpaperInfo> recyclerLayout) {
        halfScreenWidth = ScreenUtils.getScreenWidth(context) / 2 - ScreenUtils.dp2pxInt(context, 12);
        if (getHeaderLayout() > 0) {
            recyclerLayout.setHeaderView(getHeaderLayout(), holder -> holder.setOnItemClickListener((this::showSortPupWindow)));
        }
        recyclerLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
                }
            }
        });
        int dp4 = ScreenUtils.dp2pxInt(context, 4);
        recyclerLayout.getRecyclerView().setPadding(dp4, 0, dp4, 0);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<WallpaperInfo> list, int position, List<Object> payloads) {
        WallpaperInfo info = list.get(position);
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);
        ViewGroup.LayoutParams layoutParams = wallpaper.getLayoutParams();
        float width = Float.parseFloat(info.getWidth());
        float height = Float.parseFloat(info.getHeight());
        float p = height / width;
        if (p > 2.5f) {
            p = 2.5f;
        }
        layoutParams.width = halfScreenWidth;
        layoutParams.height = (int) (p * layoutParams.width);

        wallpaper.setLayoutParams(layoutParams);
        wallpaper.setTag(position);
        Glide.with(context)
                .load(list.get(position).getSpic())
                .apply(MyRequestOptions.DEFAULT_OPTIONS).into(wallpaper);

        Glide.with(context).load(info.getMemberIcon()).into(holder.getImageView(R.id.iv_icon));
        holder.getTextView(R.id.tv_content).setText(info.getContent());
        holder.getTextView(R.id.tv_name).setText(info.getNickName());
        IconCountView countView = holder.getView(R.id.support_view);
        countView.setCount(info.getSupportCount());
        countView.setState(info.isLike());
        countView.setOnStateChangedListener(new IconCountView.OnSelectedStateChangedListener() {
            @Override
            public void select(boolean isSelected) {
                HttpApi.likeApi("wallpaper", info.getId())
                        .onSuccess(data -> {
                            String result = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
//                                AToast.success(result);
                                info.setSupportCount(info.getSupportCount() + (isSelected ? 1 : -1));
                                info.setLike(isSelected);
                            } else {
                                AToast.error(result);
                                countView.setState(!isSelected);
                            }
                        })
                        .onError(throwable -> {
                            AToast.error("点赞失败！" + throwable.getMessage());
                            countView.setState(!isSelected);
                        })
                        .subscribe();
            }
        });
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, WallpaperInfo data) {
        ImageView wallpaper = holder.getImageView(R.id.iv_wallpaper);

        GetMainFragmentEvent.post(mainFragment -> {
            if (mainFragment.getView() != null) {
                List<String> objects = new ArrayList<>();
                objects.add(data.getSpic());
                List<String> original = new ArrayList<>();
                original.add(data.getPic());
                WallpaperViewerPopup.with(context)
                        .setWallpaperInfo(data)
                        .setOriginalImageList(original)
                        .setDecorView((ViewGroup) mainFragment.getView())
                        .setImageUrls(objects)
                        .setSrcView(wallpaper, 0)
                        .setSrcViewUpdateListener((popup, position) -> popup.updateSrcView(wallpaper))
                        .setOnDismissListener(() -> StatusBarEvent.post(false))
                        .show();
            }

        });

    }

    @Override
    public WallpaperInfo createData(Element element) {
        if ("wallpaper".equals(element.selectFirst("type").text())) {
            return WallpaperInfo.create(element);
        }
        return null;
    }

    @Override
    public void onRefresh() {
        data.clear();
        initNextUrl();
        refresh = true;
        recyclerLayout.notifyDataSetChanged();
    }

    protected int getHeaderLayout() {
        return R.layout.item_image_header;
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
        ExpandIconView expandIconView = v.findViewById(R.id.expand_icon);
        if (recyclerPopup == null) {
            expandIconView.switchState();
            recyclerPopup = RecyclerPopup.with(context)
                    .addItems("默认排序", "时间排序", "人气排序")
                    .setSelectedItem(sortPosition)
                    .setOnItemClickListener((view, title, position) -> {
                        sortPosition = position;
                        TextView titleText = v.findViewById(R.id.tv_title);
                        titleText.setText(title);
                        onRefresh();
                    })
                    .setOnDismissListener(() -> {
                        expandIconView.switchState();
                        recyclerPopup = null;
                    })
                    .show(v);
        } else {
            recyclerPopup.dismiss();
            recyclerPopup = null;
        }

    }
}