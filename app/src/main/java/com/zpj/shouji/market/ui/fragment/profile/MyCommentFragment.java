package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.MessageInfo;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
import com.zpj.shouji.market.ui.widget.indicator.BadgePagerTitle;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class MyCommentFragment extends BaseSwipeBackFragment {

    private static final String[] TAB_TITLES = {"评论", "发起的评论"};

    protected ViewPager viewPager;
    private MagicIndicator magicIndicator;

    public static void start() {
        start(new MyCommentFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_discover;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setToolbarTitle("我的评论");
        viewPager = view.findViewById(R.id.view_pager);
        magicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        MyRelatedCommentFragment myRelatedCommentFragment = findChildFragment(MyRelatedCommentFragment.class);
        if (myRelatedCommentFragment == null) {
            myRelatedCommentFragment = MyRelatedCommentFragment.newInstance();
        }
        MyPublishCommentFragment myPublishCommentFragment = findChildFragment(MyPublishCommentFragment.class);
        if (myPublishCommentFragment == null) {
            myPublishCommentFragment = MyPublishCommentFragment.newInstance();
        }
        fragments.add(myRelatedCommentFragment);
        fragments.add(myPublishCommentFragment);
        viewPager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES));
        viewPager.setOffscreenPageLimit(fragments.size());

//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, viewPager, TAB_TITLES);

        MessageInfo messageInfo = UserManager.getInstance().getMessageInfo();
        MagicIndicatorHelper.builder(context)
                .setMagicIndicator(magicIndicator)
                .setViewPager(viewPager)
                .setTabTitles(TAB_TITLES)
                .setOnGetTitleViewListener((context12, index) -> {
                    BadgePagerTitle badgePagerTitle = new BadgePagerTitle(context);
                    badgePagerTitle.setTitle(TAB_TITLES[index]);
                    badgePagerTitle.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                    if (index == 0) {
                        badgePagerTitle.setBadgeCount(messageInfo.getMessageCount());
                    }
                    return badgePagerTitle;
                })
                .build();
    }

    @Override
    public void onDestroy() {
        HttpApi.updateFlagApi(UpdateFlagAction.COMMENT);
        super.onDestroy();
    }

    public static class MyRelatedCommentFragment extends ThemeListFragment {

        public static MyRelatedCommentFragment newInstance() {
//            http://tt.tljpxm.com
            String url = "/app/user_content_list_xml_v2.jsp?t=review";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            MyRelatedCommentFragment fragment = new MyRelatedCommentFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

    public static class MyPublishCommentFragment extends ThemeListFragment {

        public static MyPublishCommentFragment newInstance() {
//            http://tt.tljpxm.com
            String url = "/app/user_content_list_xml_v2.jsp?t=review&thread=thread";
            Bundle args = new Bundle();
            args.putString(Keys.DEFAULT_URL, url);
            MyPublishCommentFragment fragment = new MyPublishCommentFragment();
            fragment.setArguments(args);
            return fragment;
        }

    }

}
