package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.constant.UpdateFlagAction;
import com.zpj.shouji.market.model.PrivateLetterInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.chat.ChatFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class MyPrivateLetterFragment extends NextUrlFragment<PrivateLetterInfo> {

    public static void start() {
        start(newInstance());
    }

    public static MyPrivateLetterFragment newInstance() {
        Bundle args = new Bundle();
//        http://tt.tljpxm.com
        args.putString(Keys.DEFAULT_URL, "/app/user_message_index_xml_v3.jsp");
        MyPrivateLetterFragment fragment = new MyPrivateLetterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_with_toolbar;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_private_letter;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        setToolbarTitle("我的私信");
    }

    @Override
    public void onDestroy() {
        HttpApi.updateFlagApi(UpdateFlagAction.PRIVATE);
        super.onDestroy();
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, PrivateLetterInfo data) {
//        super.onClick(holder, view, data);
        ChatFragment.start(data.getSendId(), data.getNikeName());
    }

    @Override
    public PrivateLetterInfo createData(Element element) {
        return BeanUtils.createBean(element, PrivateLetterInfo.class);
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<PrivateLetterInfo> list, int position, List<Object> payloads) {
        PrivateLetterInfo info = list.get(position);
        holder.setText(R.id.tv_name, info.getNikeName());
        holder.setText(R.id.tv_time, info.getTime());
        holder.setText(R.id.tv_content, info.getContent());
        ImageView img = holder.getView(R.id.iv_icon);
        Glide.with(img).load(info.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(img);
    }
}
