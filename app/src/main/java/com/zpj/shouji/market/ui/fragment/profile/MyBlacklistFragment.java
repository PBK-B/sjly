package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.BlacklistInfo;
import com.zpj.shouji.market.ui.fragment.WebFragment;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.dialog.BottomListMenuDialogFragment;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.toast.ZToast;

import java.util.List;

public class MyBlacklistFragment extends NextUrlFragment<BlacklistInfo> {

    public static void start() {
        Bundle args = new Bundle();
//        http://tt.tljpxm.com
        args.putString(Keys.DEFAULT_URL, "/androidv3/user_blacklist_xml.jsp");
        MyBlacklistFragment fragment = new MyBlacklistFragment();
        fragment.setArguments(args);
        start(fragment);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_user;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("我的黑名单");
    }

    @Override
    public BlacklistInfo createData(Element element) {
        return BeanUtils.createBean(element, BlacklistInfo.class);
//        return BlacklistInfo.from(element);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<BlacklistInfo> list, int position, List<Object> payloads) {
        final BlacklistInfo appItem = list.get(position);
        holder.setText(R.id.tv_title, appItem.getNickName());
        holder.setText(R.id.tv_info, "在线：" + appItem.isOnline());
        Glide.with(context).load(appItem.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, BlacklistInfo data) {
        ProfileFragment.start(data.getMemberId(), false);
    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, BlacklistInfo data) {
        new BottomListMenuDialogFragment<>()
                .setMenu(R.menu.menu_blacklist)
                .onItemClick((menu, view1, data1) -> {
                    switch (data1.getItemId()) {
                        case R.id.remove:
                            HttpApi.removeBlacklistApi(data.getMemberId())
                                    .onSuccess(doc -> {
                                        String info = doc.selectFirst("info").text();
                                        if ("success".equals(doc.selectFirst("result").text())) {
                                            ZToast.success(info);
                                            onRefresh();
                                        } else {
                                            ZToast.error(info);
                                        }
                                    })
                                    .onError(throwable -> ZToast.error(throwable.getMessage()))
                                    .subscribe();
                            break;
                        case R.id.report:
                            ZToast.normal("TODO 举报");
                            break;
                        case R.id.share:
                            WebFragment.shareHomepage(data.getMemberId());
                            break;
                    }
                    menu.dismiss();
                })
                .show(context);
        return true;
    }
}
