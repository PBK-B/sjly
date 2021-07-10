package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.collection.CollectionListFragment;
import com.zpj.shouji.market.ui.fragment.dialog.AppCollectionMoreDialogFragment;
import com.zpj.shouji.market.ui.fragment.dialog.ThemeMoreDialogFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.fragment.wallpaper.WallpaperListFragment;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDynamicFragment extends ExpandablePagerFragment {

    private static final String[] TAB_TITLES = {"全部", "发现", "评论", "应用集", "乐图", "下载", "赞"};

    public static MyDynamicFragment newInstance(String id, boolean showToolbar) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putBoolean(Keys.SHOW_TOOLBAR, showToolbar);
        MyDynamicFragment fragment = new MyDynamicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start() {
        start(newInstance(UserManager.getInstance().getUserId(), true));
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "我的动态";
    }

    @Override
    protected void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        AllFragment allFragment = findChildFragment(AllFragment.class);
        if (allFragment == null) {
            allFragment = AllFragment.newInstance(userId);
        }

        DiscoverFragment discoverFragment = findChildFragment(DiscoverFragment.class);
        if (discoverFragment == null) {
            discoverFragment = DiscoverFragment.newInstance(userId);
        }

        CommentFragment commentFragment = findChildFragment(CommentFragment.class);
        if (commentFragment == null) {
            commentFragment = CommentFragment.newInstance(userId);
        }

        CollectionsFragment collectionsFragment = findChildFragment(CollectionsFragment.class);
        if (collectionsFragment == null) {
            collectionsFragment = CollectionsFragment.newInstance(userId);
        }

        WallpaperFragment wallpaperFragment = findChildFragment(WallpaperFragment.class);
        if (wallpaperFragment == null) {
            wallpaperFragment = WallpaperFragment.newInstance(userId);
        }

        UserDownloadedFragment userDownloadedFragment = findChildFragment(UserDownloadedFragment.class);
        if (userDownloadedFragment == null) {
            userDownloadedFragment = UserDownloadedFragment.newInstance(userId);
        }

        fragments.add(allFragment);
        fragments.add(discoverFragment);
        fragments.add(commentFragment);
        fragments.add(collectionsFragment);
        fragments.add(wallpaperFragment);
        fragments.add(userDownloadedFragment);

        String[] tabTitles;
        if (TextUtils.equals(UserManager.getInstance().getUserId(), userId)) {
            tabTitles = TAB_TITLES;
            GiveLikeFragment giveLikeFragment = findChildFragment(GiveLikeFragment.class);
            if (giveLikeFragment == null) {
                giveLikeFragment = GiveLikeFragment.newInstance();
            }
            fragments.add(giveLikeFragment);
        } else {
            tabTitles = Arrays.copyOfRange(TAB_TITLES, 0, TAB_TITLES.length - 1);
        }

        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, tabTitles));
        viewPager.setOffscreenPageLimit(fragments.size());

        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, tabTitles);
    }

    public static class AllFragment extends ThemeListFragment {

        public static AllFragment newInstance(String userId) {
            String url;
            if (TextUtils.equals(userId, UserManager.getInstance().getUserId())) {
                url = "/app/user_content_list_xml_v2.jsp";
            } else {
                url = "/app/view_member_content_xml_v2.jsp?id=" + userId;
            }
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            AllFragment fragment = new AllFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class DiscoverFragment extends ThemeListFragment {

        public static DiscoverFragment newInstance(String userId) {
            String url;
            if (TextUtils.equals(userId, UserManager.getInstance().getUserId())) {
                url = "/app/user_content_list_xml_v2.jsp?t=discuss";
            } else {
                url = "/app/view_member_content_xml_v2.jsp?t=discuss&id=" + userId;
            }

            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            DiscoverFragment fragment = new DiscoverFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class CommentFragment extends ThemeListFragment {

        public static CommentFragment newInstance(String id) {
            String url = "/app/view_member_content_xml_v2.jsp?t=review&id=" + id;
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            CommentFragment fragment = new CommentFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public boolean onLongClick(EasyViewHolder holder, View view, DiscoverInfo data) {
            new ThemeMoreDialogFragment()
                    .setDiscoverInfo(data)
                    .isMe()
                    .show(context);
            return true;
        }

    }

    public static class CollectionsFragment extends CollectionListFragment {

        public static CollectionsFragment newInstance(String id) {
            String url = "/androidv3/yyj_user_xml.jsp?id=" + id;
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            CollectionsFragment fragment = new CollectionsFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public CollectionInfo createData(Element element) {
            return CollectionInfo.create(element);
        }

        @Override
        public boolean onLongClick(EasyViewHolder holder, View view, CollectionInfo data) {
            new AppCollectionMoreDialogFragment()
                    .setCollectionInfo(data)
                    .isMe()
                    .show(context);
            return true;
        }
    }

    public static class WallpaperFragment extends WallpaperListFragment {

        public static WallpaperFragment newInstance(String id) {
            String url = "/appv3/bizhi_list.jsp?member=" + id;
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            WallpaperFragment fragment = new WallpaperFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void handleArguments(Bundle arguments) {
            defaultUrl = arguments.getString(Keys.DEFAULT_URL, "");
            nextUrl = defaultUrl;
        }

        @Override
        public void onRefresh() {
//            data.clear();
//            nextUrl = defaultUrl;
//            recyclerLayout.notifyDataSetChanged();

            nextUrl = defaultUrl;
            if (data.isEmpty()) {
                refresh = false;
                recyclerLayout.showContent();
            } else {
                refresh = true;
                getData();
            }
        }

        @Override
        protected int getHeaderLayout() {
            return 0;
        }

    }

    public static class GiveLikeFragment extends ThemeListFragment {

        public static GiveLikeFragment newInstance() {
            String url = "/app/user_content_flower_send_xml_v2.jsp";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            GiveLikeFragment fragment = new GiveLikeFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
