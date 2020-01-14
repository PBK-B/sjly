package com.zpj.shouji.market.ui.fragment.main.homepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.felix.atoast.library.AToast;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.GroupViewHolder;
import com.sunfusheng.HeaderGroupRecyclerViewAdapter;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.bean.AppCollectionItem;
import com.zpj.shouji.market.bean.AppItem;
import com.zpj.shouji.market.ui.fragment.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.BlurBuilder;
import com.zpj.shouji.market.utils.ConnectUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.TransportUtil;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

public class RecommendFragment extends BaseFragment implements GroupRecyclerViewAdapter.OnItemClickListener<RecommendFragment.ItemWrapper> {

    private static final String DEFAULT_LIST_URL = "http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1&versioncode=187";

    private RecyclerView recyclerView;
    private List<AppItem> bannerItemList = new ArrayList<>();
    private List<ItemWrapper> topList = new ArrayList<>();
    private List<ItemWrapper> updateList = new ArrayList<>();
    private List<ItemWrapper> appCollectionList = new ArrayList<>();
    private List<ItemWrapper> recommendList = new ArrayList<>();
    private final List<List<ItemWrapper>> dataList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private String nextUrl = DEFAULT_LIST_URL;

    private MZBannerView<AppItem> mMZBanner;

    private RecommendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recomment3;
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view_recent_update);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            initData();
            nextUrl = DEFAULT_LIST_URL;
        }, 1000));

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new RecommendAdapter(getContext(), dataList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMZBanner != null) {
            mMZBanner.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMZBanner != null) {
            mMZBanner.pause();
        }
    }

    @Override
    public void onItemClick(GroupRecyclerViewAdapter adapter, ItemWrapper data, int groupPosition, int childPosition) {
        if (groupPosition == 0) {
            return;
        }
        if (data.getAppItem() != null) {
            TransportUtil.getInstance().setAppItem(data.getAppItem());
//            TransportUtil.getInstance().setIconBitmap(data.getIcon());
            if (getActivity() instanceof SupportActivity) {
                _mActivity.start(AppDetailFragment.newInstance(data.getAppItem()));
            }
        } else if (data.getCollectionItem() != null) {
            AToast.normal("TODO Collection");
        }
    }

    private void initData() {
        dataList.clear();
        topList.clear();
        updateList.clear();
        recommendList.clear();
        topList.add(new ItemWrapper());
        updateList.add(new ItemWrapper("最近更新"));
        appCollectionList.add(new ItemWrapper("应用集推荐"));
        recommendList.add(new ItemWrapper("应用推荐"));
        dataList.add(topList);
        dataList.add(updateList);
        dataList.add(appCollectionList);
        dataList.add(recommendList);
        adapter.notifyDataSetChanged();
        getRecentUpdates();
        getAppCollections();
        getRecommends();
    }

    private void getRecentUpdates() {
        ExecutorHelper.submit(() -> {
            try {
                Log.d("getCoolApkHtml", "nextUrl=" + nextUrl);
                Document doc = ConnectUtil.getDocument(nextUrl);
                nextUrl = doc.select("nextUrl").get(0).text();
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    Element item = elements.get(i);
                    AppItem appItem = new AppItem();
                    appItem.setAppIcon(item.select("icon").text());
                    appItem.setAppTitle(item.select("title").text());
                    appItem.setAppId(item.select("id").text());
                    appItem.setAppViewType(item.select("viewtype").text());
                    appItem.setAppType(item.select("apptype").text());
                    appItem.setAppPackage(item.select("package").text());
                    appItem.setAppArticleNum(item.select("articleNum").text());
                    appItem.setAppNum(item.select("appNum").text());
                    appItem.setAppMinSdk(item.select("msdk").text());
                    appItem.setAppSize(item.select("m").text());
                    appItem.setAppInfo(item.select("r").text());
                    appItem.setAppComment(item.select("comment").text());
                    updateList.add(new ItemWrapper(appItem));
                }
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        adapter.notifyDataSetChanged();
                        adapter.updateGroup(1, updateList);
                    }
                }, 1);
//                recyclerView.postDelayed(() -> adapter.notifyDataSetChanged(), 1);
            } catch (Exception e) {
                e.printStackTrace();
                post(() -> AToast.error("加载失败！"));
            }
        });
    }

    private void getAppCollections() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = ConnectUtil.getDocument("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp");
                Elements elements = doc.select("item");
                int count = elements.size() > 9 ? 9 : elements.size();
                for (int i = 1; i < count; i++) {
                    Element item = elements.get(i);
                    AppCollectionItem collectionItem = new AppCollectionItem();
                    collectionItem.setId(item.select("id").text());
                    collectionItem.setParent(item.select("parent").text());
                    collectionItem.setContentType(item.select("contenttype").text());
                    collectionItem.setType(item.select("type").text());
                    collectionItem.setTitle(item.select("title").text());
                    collectionItem.setComment(item.select("comment").text());
                    collectionItem.setSize(Integer.valueOf(item.select("size").text()));
                    collectionItem.setMemberId(item.select("memberid").text());
                    collectionItem.setNickName(item.select("nickname").text());
                    collectionItem.setFavCount(Integer.valueOf(item.select("favcount").text()));
                    collectionItem.setAppSize(Integer.valueOf(item.select("appsize").text()));
                    collectionItem.setSupportCount(Integer.valueOf(item.selectFirst("supportcount").text()));
                    collectionItem.setViewCount(Integer.valueOf(item.select("viewcount").text()));
                    collectionItem.setReplyCount(Integer.valueOf(item.select("replycount").text()));
                    collectionItem.setTime(item.select("time").text());
//                    collectionItem.setSupportCount(Integer.valueOf(item.select("supportcount").text()));
//                    collectionItem.setSupportCount(Integer.valueOf(item.select("supportcount").text()));
                    Elements icons = item.select("icons").select("icon");
                    for (Element element : icons) {
                        collectionItem.addIcon(element.text());
                    }
                    appCollectionList.add(new ItemWrapper(collectionItem));
                }
                recyclerView.postDelayed(() -> adapter.updateGroup(2, appCollectionList), 1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("getAppCollections", "" + e.getMessage());
                post(() -> AToast.error("加载失败！"));
            }
        });
    }

    private void getRecommends() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = ConnectUtil.getDocument("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998");
                Elements elements = doc.select("item");
                int count = elements.size() > 8 ? 8 : elements.size();
                for (int i = 0; i < count; i++) {
                    Element item = elements.get(i);
                    AppItem appItem = new AppItem();
                    appItem.setAppIcon(item.select("icon").text());
                    appItem.setAppTitle(item.select("title").text());
                    appItem.setAppId(item.select("id").text());
                    appItem.setAppViewType(item.select("viewtype").text());
                    appItem.setAppType(item.select("apptype").text());
                    appItem.setAppPackage(item.select("package").text());
                    appItem.setAppArticleNum(item.select("articleNum").text());
                    appItem.setAppNum(item.select("appNum").text());
                    appItem.setAppMinSdk(item.select("msdk").text());
                    appItem.setAppSize(item.select("m").text());
                    appItem.setAppInfo(item.select("r").text());
                    appItem.setAppComment(item.select("comment").text());
                    recommendList.add(new ItemWrapper(appItem));
                }
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        adapter.notifyDataSetChanged();
                        adapter.updateGroup(3, recommendList);
                    }
                }, 1);
            } catch (Exception e) {
                e.printStackTrace();
                post(() -> AToast.error("加载失败！"));
            }
        });
    }

    private void getBanners() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = ConnectUtil.getDocument("http://tt.shouji.com.cn/androidv3/app_index_xml.jsp?index=1&versioncode=187");
                Elements elements = doc.select("item");
                for (int i = 1; i < elements.size(); i++) {
                    Element item = elements.get(i);
                    AppItem appItem = new AppItem();
                    appItem.setAppIcon(item.select("icon").text());
                    appItem.setAppTitle(item.select("title").text());
                    appItem.setAppId(item.select("id").text());
                    appItem.setAppViewType(item.select("viewtype").text());
                    appItem.setAppType(item.select("apptype").text());
                    appItem.setAppPackage(item.select("package").text());
                    appItem.setAppArticleNum(item.select("articleNum").text());
                    appItem.setAppNum(item.select("appNum").text());
                    appItem.setAppSize(item.select("m").text());
                    appItem.setAppInfo(item.select("r").text());
                    appItem.setAppComment(item.select("comment").text());
                    bannerItemList.add(appItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            post(() -> {
                mMZBanner.setPages(bannerItemList, (MZHolderCreator<BannerViewHolder>) BannerViewHolder::new);
                mMZBanner.start();
            });
        });
    }

    private static class BannerViewHolder implements MZViewHolder<AppItem> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
            mImageView = view.findViewById(R.id.img_view);
            return view;
        }

        @Override
        public void onBind(Context context, int position, AppItem item) {
            // 数据绑定
//            mImageView.setImageResource(data);
            Glide.with(context).load(item.getAppIcon()).into(mImageView);
        }
    }

    public class ItemWrapper {

        private AppItem appItem;
        private AppCollectionItem collectionItem;
        private String title;
        public Drawable icon;

        ItemWrapper() {

        }

        ItemWrapper(AppCollectionItem collectionItem) {
            this.collectionItem = collectionItem;
        }

        ItemWrapper(AppItem appItem) {
            this.appItem = appItem;
        }

        ItemWrapper(String title) {
            this.title = title;
        }

        public void setCollectionItem(AppCollectionItem collectionItem) {
            this.collectionItem = collectionItem;
        }

        public AppCollectionItem getCollectionItem() {
            return collectionItem;
        }

        public void setAppItem(AppItem appItem) {
            this.appItem = appItem;
        }

        public AppItem getAppItem() {
            return appItem;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public Drawable getIcon() {
            return icon;
        }
    }

    public class RecommendAdapter extends HeaderGroupRecyclerViewAdapter<ItemWrapper> {

        private final int[] RES_ICONS = {R.id.item_icon_1, R.id.item_icon_2, R.id.item_icon_3};

        public static final int TYPE_TOP_HEADER = 11;
        public static final int TYPE_SUB_HEADER = 22;
        public static final int TYPE_CHILD_UPDATE = 331;
        public static final int TYPE_CHILD_COLLECTION = 332;
        public static final int TYPE_CHILD_RECOMMEND = 333;

        public RecommendAdapter(Context context, List<List<ItemWrapper>> groups) {
            super(context, groups);
        }

        @Override
        public boolean showHeader() {
            return true;
        }

        @Override
        public boolean showFooter() {
            return false;
        }

        @Override
        public int getHeaderItemViewType(int groupPosition) {
            if (groupPosition == 0) {
                return TYPE_TOP_HEADER;
            }
            return TYPE_SUB_HEADER;
        }

        @Override
        public int getChildItemViewType(int groupPosition, int childPosition) {
            if (groupPosition == 1) {
                return TYPE_CHILD_UPDATE;
            } else if (groupPosition == 2) {
                return TYPE_CHILD_COLLECTION;
            } else if (groupPosition == 3) {
                return TYPE_CHILD_RECOMMEND;
            }
            return super.getChildItemViewType(groupPosition, childPosition);
        }

        @Override
        public int getHeaderLayoutId(int viewType) {
            if (viewType == TYPE_TOP_HEADER) {
                return R.layout.header_recommend;
            } else {
                return R.layout.item_recommend_header;
            }
        }

        @Override
        public int getChildLayoutId(int viewType) {
            if (viewType == TYPE_CHILD_COLLECTION) {
                return R.layout.item_app_collection;
            }
            return R.layout.item_app_grid;
        }

        @Override
        public int getFooterLayoutId(int viewType) {
            return 0;
        }

        @Override
        public void onBindHeaderViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {
            int viewType = getHeaderItemViewType(groupPosition);
            if (viewType == TYPE_TOP_HEADER) {
                mMZBanner = holder.get(R.id.banner);
                if (mMZBanner.getTag() == null) {
                    mMZBanner.setTag(true);
                    getBanners();
                }
            } else if (viewType == TYPE_SUB_HEADER) {
                holder.setText(R.id.text_title, item.getTitle());
            }
        }

        @Override
        public void onBindChildViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition, int childPosition) {
            int viewType = getChildItemViewType(groupPosition, childPosition);
            if (viewType == TYPE_CHILD_UPDATE | viewType == TYPE_CHILD_RECOMMEND) {
                final AppItem appItem = item.getAppItem();
                if (appItem == null) {
                    return;
                }
                holder.setText(R.id.item_title, appItem.getAppTitle());
                holder.setText(R.id.item_info, appItem.getAppSize());
                if (item.getIcon() != null) {
                    holder.setImageDrawable(R.id.item_icon, item.getIcon());
                } else {
                    Glide.with(context).asDrawable().load(appItem.getAppIcon()).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            item.setIcon(resource);
                            holder.setImageDrawable(R.id.item_icon, resource);
                        }
                    });
                }
            } else if (viewType == TYPE_CHILD_COLLECTION) {
                long time1 = System.currentTimeMillis();
                final AppCollectionItem appItem = item.getCollectionItem();
                holder.setText(R.id.item_title, appItem.getTitle());
                holder.setText(R.id.tv_view_count, appItem.getViewCount() + "");
                holder.setText(R.id.tv_favorite_count, appItem.getFavCount() + "");
                holder.setText(R.id.tv_support_count, appItem.getSupportCount() + "");
//                int size = appItem.getIcons().size() > 3 ? 3 : appItem.getIcons().size();

                if (item.getIcon() != null) {
                    holder.setImageDrawable(R.id.img_bg, item.getIcon());
                }
                for (int i = 0; i < RES_ICONS.length; i++) {
                    int res = RES_ICONS[i];
                    if (i == 0) {
                        if (item.getIcon() == null) {
                            Glide.with(context).asDrawable().load(appItem.getIcons().get(0)).into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    item.setIcon(BlurBuilder.blur(resource));
                                    holder.setImageDrawable(R.id.img_bg, item.getIcon());
                                    holder.setImageDrawable(res, resource);
                                }
                            });
                            continue;
                        } else {
                            holder.setImageDrawable(R.id.img_bg, item.getIcon());
                        }
                    }
                    Glide.with(context).load(appItem.getIcons().get(i)).into((ImageView) holder.get(res));
                }
                Log.d("onBindChildViewHolder", "deltaTime=" + (System.currentTimeMillis() - time1));
            }
        }

        @Override
        public void onBindFooterViewHolder(GroupViewHolder holder, ItemWrapper item, int groupPosition) {

        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (isGroupChild(2, position)) {
                            return 2;
                        }
                        int span = isChild(position)
                                ? 1 : gridManager.getSpanCount();
                        Log.d("RecommendAdapter", "onAttachedToRecyclerView position=" + position + " span=" + span);
                        return span;
                    }
                });
            }
        }

    }

}
