package com.zpj.shouji.market.ui.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MessageInfo;
import com.zpj.shouji.market.model.UserInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.UserListFragment;
import com.zpj.shouji.market.ui.widget.indicator.BadgePagerTitle;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;

import java.util.ArrayList;
import java.util.List;

public class MyFriendsFragment extends ExpandablePagerFragment {

    private static final String[] TAB_TITLES = {"我关注的", "我的粉丝"};

    public static MyFriendsFragment newInstance(String id, boolean showToolbar) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        args.putBoolean(Keys.SHOW_TOOLBAR, showToolbar);
        MyFriendsFragment fragment = new MyFriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(String id) {
        start(newInstance(id, true));
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "我的好友";
    }

    @Override
    protected void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        FollowersFragment followersFragment = findChildFragment(FollowersFragment.class);
        if (followersFragment == null) {
            followersFragment = FollowersFragment.newInstance(userId);
        }
        FansFragment fansFragment = findChildFragment(FansFragment.class);
        if (fansFragment == null) {
            fansFragment = FansFragment.newInstance(userId);
        }
        fragments.add(followersFragment);
        fragments.add(fansFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());
//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES, true);

        MessageInfo messageInfo = UserManager.getInstance().getMessageInfo();
        MagicIndicatorHelper.builder(context)
                .setMagicIndicator(magicIndicator)
                .setViewPager(viewPager)
                .setTabTitles(TAB_TITLES)
                .setAdjustMode(true)
                .setOnGetTitleViewListener((context12, index) -> {
                    BadgePagerTitle badgePagerTitle = new BadgePagerTitle(context);
                    badgePagerTitle.setAdjustMode(true);
                    badgePagerTitle.setTitle(TAB_TITLES[index]);
                    badgePagerTitle.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                    if (index == 1) {
                        badgePagerTitle.setBadgeCount(messageInfo.getFanCount());
                    }
                    return badgePagerTitle;
                })
                .build();
    }

    public static class FollowersFragment extends UserListFragment {
        public static FollowersFragment newInstance(String id) {
//            String url = "http://tt.shouji.com.cn/app/user_friend_list_xml.jsp";
//            http://tt.tljpxm.com
//            String url = "/app/view_member_friend_xml.jsp?mmid=" + id;
            String url;
            if (TextUtils.equals(id, UserManager.getInstance().getUserId())) {
                url = "/appv3/user_friend_list_xml.jsp";
            } else {
                url = "/appv3/view_member_friend_xml.jsp?mmid=" + id;
            }
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            FollowersFragment fragment = new FollowersFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, UserInfo data) {
            ProfileFragment.start(data.getMemberId(), true);
        }

    }

    public static class FansFragment extends UserListFragment {
        public static FansFragment newInstance(String id) {
//            String url = "http://tt.shouji.com.cn/app/user_fensi_list_xml.jsp";
//            http://tt.tljpxm.com
//            String url = "/app/view_member_fensi_xml.jsp?mmid=" + id;
            String url;
            if (TextUtils.equals(id, UserManager.getInstance().getUserId())) {
                url = "/appv3/user_fensi_list_xml.jsp";
            } else {
                url = "/appv3/view_member_fensi_xml.jsp?mmid=" + id;
            }
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            FansFragment fragment = new FansFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onClick(EasyViewHolder holder, View view, UserInfo data) {
            ProfileFragment.start(data.getMemberId(), true);
        }

        @Override
        public void onDestroy() {
            HttpApi.updateFlagApi(UpdateFlagAction.FAN);
            super.onDestroy();
        }

    }



}
