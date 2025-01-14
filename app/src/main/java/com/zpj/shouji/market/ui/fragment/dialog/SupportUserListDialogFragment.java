package com.zpj.shouji.market.ui.fragment.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.SupportUserInfo;
import com.zpj.shouji.market.ui.fragment.profile.ProfileFragment;
import com.zpj.shouji.market.ui.widget.DialogHeaderLayout;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.statemanager.StateManager;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class SupportUserListDialogFragment extends BottomDragDialogFragment<SupportUserListDialogFragment>
         implements IEasy.OnBindViewHolderListener<SupportUserInfo> {

    private final List<SupportUserInfo> userInfoList = new ArrayList<>();

    private StateManager stateManager;
    private EasyRecyclerView<SupportUserInfo> recyclerView;

    private String contentType;
    private String themeId;

    public static void start(String contentType, String themeId) {
        SupportUserListDialogFragment fragment = new SupportUserListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Keys.ID, themeId);
        bundle.putString(Keys.TYPE, contentType);
        fragment.setArguments(bundle);
        EventBus.post(fragment);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dialog_fragment_support_user_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        setCornerRadiusDp(20);
        super.initView(view, savedInstanceState);

        if (getArguments() != null) {
            contentType = getArguments().getString(Keys.TYPE);
            themeId = getArguments().getString(Keys.ID);
        }

        DialogHeaderLayout headerLayout = findViewById(R.id.layout_dialog_header);
        headerLayout.setOnCloseClickListener(view1 -> dismiss());

        RecyclerView recycler = findViewById(R.id.recycler_view);
        recyclerView = new EasyRecyclerView<>(recycler);
        recyclerView.setData(userInfoList)
                .setItemRes(R.layout.item_menu)
                .setLayoutManager(new LinearLayoutManager(getContext()))
                .onBindViewHolder(this)
                .onItemClick((holder, view1, data) -> {
                    dismiss();
                    ProfileFragment.start(data.getUserId(), false);
                })
                .build();
        stateManager = StateManager.with(recycler)
                .showLoading();
        stateManager.getStateView().setMinimumHeight((int) (ScreenUtils.getScreenHeight(context) / 1.5));
        getSupportUserList();

    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SupportUserInfo> list, int position, List<Object> payloads) {
        SupportUserInfo userInfo = list.get(position);
        Glide.with(context)
                .load(userInfo.getUserLogo())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.getImageView(R.id.iv_icon));
        holder.setText(R.id.tv_title, userInfo.getNickName());
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(context) - ScreenUtils.getStatusBarHeight(context) - ScreenUtils.dp2pxInt(context, 56);
    }

    private void getSupportUserList() {
        HttpApi.getSupportUserListApi(contentType, themeId)
                .onSuccess(data -> {
                    userInfoList.clear();
                    for (Element element : data.select("fuser")) {
                        SupportUserInfo userInfo = new SupportUserInfo();
                        userInfo.setNickName(element.selectFirst("fname").text());
                        userInfo.setUserId(element.selectFirst("fid").text());
                        userInfo.setUserLogo(element.selectFirst("avatar").text());
                        userInfoList.add(userInfo);
                    }
                    postDelayed(() -> {
                        stateManager.showContent();
                        recyclerView.notifyDataSetChanged();
                    }, 250);
                })
                .onError(throwable -> stateManager.showError(throwable.getMessage()))
                .subscribe();
    }


    public SupportUserListDialogFragment setThemeId(String id) {
        this.themeId = id;
        return this;
    }

}
