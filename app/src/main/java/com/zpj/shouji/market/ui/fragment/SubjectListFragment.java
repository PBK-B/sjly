package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.AppItem;
import com.zpj.shouji.market.model.SubjectItem;
import com.zpj.shouji.market.ui.fragment.base.LoadMoreFragment;
import com.zpj.shouji.market.ui.fragment.base.RecyclerLayoutFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchResultFragment;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import java.io.IOException;
import java.util.List;

public class SubjectListFragment extends LoadMoreFragment<SubjectItem>
        implements SearchResultFragment.KeywordObserver {

    public static SubjectListFragment newInstance(String defaultUrl) {
        Bundle args = new Bundle();
        args.putString(KEY_DEFAULT_URL, defaultUrl);
        SubjectListFragment fragment = new SubjectListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_linear;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<SubjectItem> list, int position, List<Object> payloads) {
        final SubjectItem appItem = list.get(position);
//        holder.getTextView(R.id.tv_title).setText(appItem.getAppTitle());
//        holder.getTextView(R.id.tv_info).setText(appItem.getAppSize() + " | " + appItem.getAppInfo());
//        holder.getTextView(R.id.tv_desc).setText(appItem.getAppComment());
//        Glide.with(context).load(appItem.getAppIcon()).into(holder.getImageView(R.id.iv_icon));
    }

    @Override
    public void updateKeyword(String key) {
        defaultUrl = "http://tt.shouji.com.cn/androidv3/app_search_xml.jsp?sdk=26&type=default&s=" + key;
        nextUrl = defaultUrl;
        onRefresh();
    }

    @Override
    public SubjectItem createData(Element element) {
        return SubjectItem.create(element);
    }

}
