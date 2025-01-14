package com.zpj.shouji.market.ui.fragment.profile;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.fragmentation.dialog.ZDialog;
import com.zpj.http.core.HttpObserver;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.AppConfig;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.glide.GlideRequestOptions;
import com.zpj.shouji.market.glide.transformations.CircleWithBorderTransformation;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.base.StateSwipeBackFragment;
import com.zpj.shouji.market.ui.fragment.chat.ChatFragment;
import com.zpj.shouji.market.ui.widget.indicator.ExpandablePagerTitle;
import com.zpj.shouji.market.utils.MagicIndicatorHelper;
import com.zpj.shouji.market.utils.PictureUtil;
import com.zpj.toast.ZToast;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends StateSwipeBackFragment
        implements View.OnClickListener {

    public static final String DEFAULT_URL = "/app/view_member_xml_v4.jsp?id=5636865";

    private static final String[] TAB_TITLES = {"动态", "收藏", "好友"}; // "下载"

    private LinearLayout headerLayout;
    private ImageView ivHeader;
    private ImageView ivAvater;
    private ImageView ivToolbarAvater;
    private TextView tvFollow;
    private ImageButton ivChat;
    private TextView tvName;
    private TextView tvToolbarName;
    private TextView tvInfo;
    private ViewPager mViewPager;
    private View buttonBarLayout;
    private MagicIndicator magicIndicator;

    private String userId;
    private String userName;
    private boolean isMe;
    private boolean isFriend;

    private String memberAvatar;
    private String memberBackground;

    public static ProfileFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, userId);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(String userId, boolean shouldLazyLoad) {
        ProfileFragment profileFragment = new ProfileFragment();
//        profileFragment.setShouldLazyLoad(shouldLazyLoad);
        Bundle bundle = new Bundle();
        bundle.putString(Keys.ID, userId);
        profileFragment.setArguments(bundle);
        start(profileFragment);
    }

    public static void start(String userName) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.NAME, userName);
        profileFragment.setArguments(bundle);
        start(profileFragment);
    }

//    public static void start(String userId, FragmentLifeCycler lifeCycler) {
//        ProfileFragment profileFragment = new ProfileFragment();
////        profileFragment.setShouldLazyLoad(shouldLazyLoad);
//        Bundle bundle = new Bundle();
//        bundle.putString(Keys.ID, userId);
//        profileFragment.setArguments(bundle);
//        profileFragment.setFragmentLifeCycler(lifeCycler);
//        start(profileFragment);
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initStatusBar() {
        if (isLazyInit() || AppConfig.isNightMode()) {
            lightStatusBar();
        } else {
            darkStatusBar();
        }
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString(Keys.ID, "");
            if (TextUtils.isEmpty(userId)) {
                userName = bundle.getString(Keys.NAME, "");
                isMe = userName.equals(UserManager.getInstance().getUserNickName());
            } else {
                isMe = userId.equals(UserManager.getInstance().getUserId());
            }
        } else {
            userId = null;
        }


//        stateLayout = view.findViewById(R.id.state_layout);

        if (TextUtils.isEmpty(userId) && TextUtils.isEmpty(userName)) {
//            stateLayout.showErrorView("用户不存在！");
            showError("用户不存在！");
            return;
        }

        headerLayout = view.findViewById(R.id.layout_header);

        tvFollow = view.findViewById(R.id.tv_follow);
        tvFollow.setOnClickListener(this);
        ivChat = view.findViewById(R.id.iv_chat);
        ivChat.setOnClickListener(this);
        if (isMe) {
            tvFollow.setText(R.string.text_edit);
            tvFollow.setBackgroundResource(R.drawable.bg_button_round_gray);
        }

        ivHeader = view.findViewById(R.id.iv_header);
        ivAvater = view.findViewById(R.id.iv_avatar);
        ivToolbarAvater = view.findViewById(R.id.toolbar_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvToolbarName = view.findViewById(R.id.toolbar_name);
        tvInfo = view.findViewById(R.id.tv_info);

        mViewPager = view.findViewById(R.id.view_pager);
        buttonBarLayout = toolbar.getCenterCustomView();
        magicIndicator = view.findViewById(R.id.magic_indicator);

        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        View shadowView = findViewById(R.id.shadow_view);
        shadowView.setAlpha(1f);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {
            float alpha = (float) Math.abs(i) / appBarLayout1.getTotalScrollRange();
            alpha = Math.min(1f, alpha);
            buttonBarLayout.setAlpha(alpha);
            headerLayout.setAlpha(1f - alpha);
//            shadowView.setAlpha(alpha >= 1f ? 1f : 0f);
        });

        buttonBarLayout.setAlpha(0);

//        stateLayout.showLoadingView();

        if (isMe) {
            PictureUtil.loadAvatar(ivAvater);
            PictureUtil.loadAvatar(ivToolbarAvater);
            PictureUtil.loadBackground(ivHeader);
        }

        getMemberInfo();
    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        super.toolbarRightImageButton(imageButton);
        imageButton.setOnClickListener(v -> {
            ZDialog.attach()
                    .addItems("分享主页", "保存头像", "保存背景")
                    .addItemsIf(!isMe, "加入黑名单", "举报Ta")
                    .setOnSelectListener((fragment, position, title) -> {
                        switch (position) {
                            case 0:
                                WebFragment.shareHomepage(userId);
                                break;
                            case 1:
                                PictureUtil.saveImage(context, memberAvatar);
                                break;
                            case 2:
                                PictureUtil.saveImage(context, memberBackground);
                                break;
                            case 3:
                                HttpApi.addBlacklistApi(userId);
                                break;
                            case 4:
                                ZToast.warning("TODO");
                                break;
                        }
                        fragment.dismiss();
                    })
                    .setAttachView(imageButton)
                    .show(context);
        });
    }

    @Override
    protected void onRetry() {
        super.onRetry();
        getMemberInfo();
    }

    private void getMemberInfo() {
        darkStatusBar();
        HttpObserver<Document> task = TextUtils.isEmpty(userId)
                ? HttpApi.getMemberInfoByNameApi(userName) : HttpApi.getMemberInfoByIdApi(userId);
        task.bindToLife(this)
                .onSuccess(element -> {
                    Log.d("onGetUserItem", "element=" + element);
                    isFriend = "1".equals(element.selectFirst("isfriend").text());
                    if (isFriend) {
                        tvFollow.setText("已关注");
                    } else {
                        ivChat.setVisibility(View.GONE);
                    }
                    memberBackground = element.selectFirst("memberbackground").text();
                    memberAvatar = element.selectFirst("memberavatar").text();

                    if (TextUtils.isEmpty(userId)) {
                        userId = element.selectFirst("memberid").text();
                    }

                    if (!isMe) {
                        Glide.with(context).load(memberBackground)
                                .apply(
                                        new RequestOptions()
                                                .error(R.drawable.bg_member_default)
                                                .placeholder(R.drawable.bg_member_default)
                                )
                                .into(ivHeader);
                        RequestOptions options = GlideRequestOptions.with()
                                .addTransformation(new CircleWithBorderTransformation(0.5f, Color.LTGRAY))
                                .get()
                                .error(R.drawable.ic_profile)
                                .placeholder(R.drawable.ic_profile);
                        Glide.with(context)
                                .load(memberAvatar)
                                .apply(options)
                                .into(ivAvater);
                        Glide.with(context)
                                .load(memberAvatar)
                                .apply(options)
                                .into(ivToolbarAvater);
                    }

                    String nickName = element.selectFirst("nickname").text();
                    tvName.setText(nickName);
                    tvToolbarName.setText(nickName);
                    tvInfo.setText(element.selectFirst("membersignature").text());

                    postOnEnterAnimationEnd(() -> {
//                        stateLayout.showContentView();
                        showContent();
                        initViewPager();
                        lightStatusBar();
                    });
                })
                .onError(throwable -> {
                    ZToast.error(throwable.getMessage());
//                    stateLayout.showErrorView(throwable.getMessage());
                    showError(throwable.getMessage());
                })
                .subscribe();
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        MyDynamicFragment dynamicFragment = findChildFragment(MyDynamicFragment.class);
        if (dynamicFragment == null) {
            dynamicFragment = MyDynamicFragment.newInstance(userId, false);
        }
        fragments.add(dynamicFragment);
        MyCollectionFragment collectionFragment = findChildFragment(MyCollectionFragment.class);
        if (collectionFragment == null) {
            collectionFragment = MyCollectionFragment.newInstance(userId, false);
        }
        fragments.add(collectionFragment);
//        UserDownloadedFragment userDownloadedFragment = findChildFragment(UserDownloadedFragment.class);
//        if (userDownloadedFragment == null) {
//            userDownloadedFragment = UserDownloadedFragment.newInstance(userId);
//        }
//        fragments.add(userDownloadedFragment);

        MyFriendsFragment friendsFragment = findChildFragment(MyFriendsFragment.class);
        if (friendsFragment == null) {
            friendsFragment = MyFriendsFragment.newInstance(userId, false);
        }
        fragments.add(friendsFragment);
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(fragments.size());

//        MagicIndicatorHelper.bindViewPager(context, magicIndicator, mViewPager, TAB_TITLES, true);

        MagicIndicatorHelper.builder(context)
                .setMagicIndicator(magicIndicator)
                .setViewPager(mViewPager)
                .setTabTitles(TAB_TITLES)
                .setAdjustMode(true)
                .setOnGetTitleViewListener((context12, index) -> {
                    ExpandablePagerTitle pagerTitle = new ExpandablePagerTitle(context12);
                    switch (index) {
                        case 0:
                            pagerTitle.init(mViewPager, index, isMe, "动态", "发现", "评论", "应用集", "乐图", "下载", "赞");
                            break;
                        case 1:
                            pagerTitle.init(mViewPager, index, isMe, "应用", "应用集", "发现", "乐图", "评论", "专题", "攻略", "教程");
                            break;
                        case 2:
                            pagerTitle.init(mViewPager, index, isMe, "关注", "粉丝");
                            break;
                        default:
                            break;
                    }
                    return pagerTitle;
                })
                .setOnGetIndicatorListener(context -> {
                    WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                    indicator.setHorizontalPadding(0);
                    indicator.setVerticalPadding(0);
                    indicator.setRoundRadius(UIUtil.dip2px(context, 8));
                    indicator.setFillColor(Color.parseColor("#80eeeeee"));
                    return indicator;
//                    return new BezierPagerIndicator(context);
//                    return null;
                })
                .build();
    }

    @Override
    public void onClick(View v) {
        if (v == ivChat) {
            ChatFragment.start(userId, tvName.getText().toString());
        } else if (v == tvFollow) {
            if (isMe) {
                MyInfoFragment.start();
            } else if (isFriend) {
                ZDialog.alert()
                        .setTitle("取消关注")
                        .setContent("确定取消关注该用户？")
                        .setPositiveButton((fragment, which) -> HttpApi.deleteFriendApi(userId)
                                .bindToLife(ProfileFragment.this)
                                .onSuccess(data -> {
                                    Log.d("deleteFriendApi", "data=" + data);
                                    String result = data.selectFirst("result").text();
                                    if ("success".equals(result)) {
                                        ZToast.success("取消关注成功");
                                        tvFollow.setText("关注");
                                        ivChat.setVisibility(View.GONE);
                                        isFriend = false;
                                    } else {
                                        ZToast.error(data.selectFirst("info").text());
                                    }
                                })
                                .onError(throwable -> ZToast.error(throwable.getMessage()))
                                .subscribe())
                        .show(context);
            } else {
                HttpApi.addFriendApi(userId)
                        .bindToLife(this)
                        .onSuccess(data -> {
                            Log.d("addFriendApi", "data=" + data);
                            String result = data.selectFirst("result").text();
                            if ("success".equals(result)) {
                                ZToast.success("关注成功");
                                tvFollow.setText("已关注");
                                ivChat.setVisibility(View.VISIBLE);
                                isFriend = true;
                            } else {
                                ZToast.error(data.selectFirst("info").text());
                            }
                        })
                        .onError(throwable -> ZToast.error(throwable.getMessage()))
                        .subscribe();
            }
        }
    }

}
